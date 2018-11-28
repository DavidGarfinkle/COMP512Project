package Server.LockManager;

import java.util.*;
import java.rmi.RemoteException;

import Server.Common.*;
import Server.Interface.*;

import Server.Utils.*;

public class TransactionManager {

  // private static int TABLE_SIZE = 2039;
  // private static TPHashTable activeTransactions = new TPHashTable(TransactionManager.TABLE_SIZE);
  // protected static RMHashtable involvedResourceManagers = new RMHashtable();

  protected static Hashtable<Integer, TransactionObject> activeTransactions = new Hashtable<Integer, TransactionObject>();
  protected static Hashtable<Integer, Vector<IResourceManager>> involvedResourceManagers = new Hashtable<Integer, Vector<IResourceManager>>();
  protected static IncrementingInteger xidPicker = new IncrementingInteger();
  protected static Hashtable<Integer, TimerTask> timeoutTable = new Hashtable<Integer, TimerTask>();
  private static Timer timer;
  private static int TIMEOUT_LENGTH = 3000;
  private int mode;

  public TransactionManager() {
    Trace.info("TM::TransactionManager() Constructor");
    timer = new Timer();
  }

  public int start() throws RemoteException, TransactionAbortedException, InvalidTransactionException {
      Trace.info("TM::start() called");
      int xid = xidPicker.pick();
      TimeObject tx = new TimeObject(xid);

      if (!activeTransactions.containsKey(xid)){
        activeTransactions.put(xid, tx);
      }

      startTimer(xid);

      // Overloaded put() will allocate new vector if first times
      // also will only add distinct ResourceManagers to the vector hashed at txid
      //involvedResourceManagers.put(tx.getXId(), resourceManagers);

      return xid;
  }

  public boolean commit(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("TM(" + xid + ")::commit called");
		if (!activeTransactions.containsKey(xid)){
			throw new InvalidTransactionException(xid, "Transaction manager cannot commit a transaction that has not been initialized");
		}

    Trace.info("TM(" + xid + ")::sending vote request to associated RMs");

    // type 1 TM crash (mode = 1)
    if (this.mode == 1){
      Trace.info("TM(" + xid + ")::crash mode 1 --- before sending vote requests");
      System.exit(1);
    }

    int counter = 0; // needed for crashing
    // create dictionary for those who voted yes and those who voted no
    Map<Boolean, ArrayList<String>> dict = new HashMap<Boolean,ArrayList<String>>();

    // sending out voteRequests phase
    for (IResourceManager rm : involvedResourceManagers.get(xid)) {
      counter +=1;
      // if mode 3 crash initiated
      if (this.mode == 3 && counter>1){
        // at least one vote request was sent
        Trace.info("TM(" + xid + ")::crash mode 3 --- Crashed after receiving some replies but not all");
        System.exit(1);
      }
      try {
        // instead of just telling rm to commit, send a vote request
        if(!rm.voteRequest(xid)){
          // one of the rms said they couldn't commit
          if (!dict.containsKey(false)){
            dict.put(false, new ArrayList<String>());
          }
          dict.get(false).add(rm.getName());
        }
        else{
          if (!dict.containsKey(true)){
            dict.put(true, new ArrayList<String>());
          }
          dict.get(true).add(rm.getName());
        }
      } catch (Exception e) {
        System.out.println(e);
      }
    }


    // crash mode 4 goes here (after sending all voteRequests but before decision phase)
    if (this.mode == 4){
      Trace.info("TM(" + xid + ")::crash mode 4 --- Crashed after sending all voteRequests but before decision phase");
      System.exit(1);
    }

    // got all votes from involed RMs --> decision phase
    boolean abort = false;
    if(dict.containsKey(false)){
      // someone voted NO
      System.out.println(dict.get(false) + "voted NO during voteRequest");
      // therefore, everyone should abort
      abort = true;
    }

    // decsion of whether or not to abort or commit is made, need to send it to involved RMs
    // crash mode 5 goes here
    if (this.mode == 5){
      Trace.info("TM(" + xid + ")::crash mode 5 --- Crashed after decision phase but before sending out decision to involvedResourceManagers");
      System.exit(1);
    }

    // decison made, now send it to involved RMs
    if (abort){
      // everyone should abort
      abort(xid);
    }
    else{
      // at this point, all involed RMs voted yes to commit, therefore can do global commit
      counter = 0;
      for (IResourceManager rm : involvedResourceManagers.get(xid)) {
        counter ++;
        // if mode 6 crash initiated
        if (this.mode == 6 && counter>1){
          // at least one RM recieved decision
          Trace.info("TM(" + xid + ")::crash mode 6 --- Crashed after sending decision to one but not all involvedResourceManagers");
          System.exit(1);
        }
        try {
          rm.doCommit(xid);
        } catch (Exception e) {
          System.out.println(e);
        }
      }
    }

    // if mode 7 crash initiated
    if (this.mode == 7 && counter>1){
      // at least one RM recieved decision
      Trace.info("TM(" + xid + ")::crash mode 7 --- Crashed after sending decision to all involvedResourceManagers");
      System.exit(1);
    }
    involvedResourceManagers.remove(xid);
    activeTransactions.remove(xid);
    finishTimer(xid);
    return true;
  }

  public void abort(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("TM::abort(" + xid + ") called");
		if (!activeTransactions.containsKey(xid)){
			throw new InvalidTransactionException(xid, "Transaction manager cannot abort a transaction that has not been initialized");
		}

    for (IResourceManager rm : involvedResourceManagers.get(xid)) {
      try {
        rm.abort(xid);
      } catch (Exception e) {
        System.out.println(e);
      }
    }
    involvedResourceManagers.remove(xid);
    activeTransactions.remove(xid);
    finishTimer(xid);
  }

  public void crash(int mode) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    this.mode = mode;
  }

  public void checkTransaction(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
		if (!activeTransactions.containsKey(xid)){
			throw new InvalidTransactionException(xid, "Middleware cannot add operation to uninitialized transaction");
		}
  }

  public void processTransaction(int xid, IResourceManager rm) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("TM::processTransaction(" + xid + ", " + rm.getName() + ")");

    // A transaction must be initialized with start() before it can handle operations
    checkTransaction(xid);
    resetTimer(xid);

    // Init resource manager vector
    if (!involvedResourceManagers.containsKey(xid)) {
      Trace.info("TM::processTransaction --- initializing RM vector for tx " + xid);
      involvedResourceManagers.put(xid, new Vector<IResourceManager>());
    }

    // If this tx doesn't have this rm, add the rm, and init its tx
    if (!involvedResourceManagers.get(xid).contains(rm)) {
      Trace.info("TM::processTransaction --- add & start " + rm.getName() + " to RM vector for tx " + xid);
      rm.start(xid);
      involvedResourceManagers.get(xid).add(rm);
    }
  }

  private void resetTimer(int xid) {
    Trace.info("TM::resetTimer(" + xid + ") called");
    // cancel previous TimerTask
    TimerTask timeout = timeoutTable.get(xid);
    timeout.cancel();

    // schedule new TimerTask
    timeout = new Timeout(xid, this);
    timer.schedule(timeout, TIMEOUT_LENGTH);
    timeoutTable.put(xid, timeout);
  }

  private void startTimer(int xid) {
    Trace.info("TM::startTimer(" + xid + ") called");
    TimerTask timeout = new Timeout(xid, this);
    timer.schedule(timeout, TIMEOUT_LENGTH);
    timeoutTable.put(xid, timeout);
  }

  private void finishTimer(int xid) {
    Trace.info("TM::finishTimer(" + xid + ") called");
    timeoutTable.get(xid).cancel();
  }
}
