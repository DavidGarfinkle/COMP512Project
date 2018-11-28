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
  protected Hashtable<String, TimeManager> rmTimeManagers; 
  private static int TIMEOUT_LENGTH = 120000;
  protected TimeManager txTimeManager;

  public TransactionManager(Hashtable<String, TimeManager> rmTimeManagers) {
    Trace.info("TM::TransactionManager() Constructor");
    txTimeManager = new TimeManager(TIMEOUT_LENGTH, this);
    this.rmTimeManagers = rmTimeManagers;
  }

  public int start() throws RemoteException, TransactionAbortedException, InvalidTransactionException {
      Trace.info("TM::start() called");
      int xid = xidPicker.pick();
      TimeObject tx = new TimeObject(xid);

      if (!activeTransactions.containsKey(xid)){
        activeTransactions.put(xid, tx);
      }

      txTimeManager.startTimer(xid);

      // Overloaded put() will allocate new vector if first times
      // also will only add distinct ResourceManagers to the vector hashed at txid
      //involvedResourceManagers.put(tx.getXId(), resourceManagers);

      return xid;
  }

  public boolean commit(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("TM::commit called");
		if (!activeTransactions.containsKey(xid)){
			throw new InvalidTransactionException(xid, "Transaction manager cannot commit a transaction that has not been initialized");
		}
    
    Trace.info("TM::sending vote request to associated RMs");
    boolean abort = false;
    for (IResourceManager rm : involvedResourceManagers.get(xid)) {
      try {
        // instead of just telling rm to commit, send a vote request
        if(!rm.voteRequest(xid)){
          // one of the rms said they couldn't commit, therefore all should abort
          abort = true;
          break;
        }
      } catch (Exception e) {
        System.out.println(e);
      }
    }
    // at this point, all involved rms have been asked if they can commit and they all voted yes
    if (!abort){
      for (IResourceManager rm : involvedResourceManagers.get(xid)) {
        try {
          rm.doCommit(xid);
        } catch (Exception e) {
          System.out.println(e);
        }
      }
      involvedResourceManagers.remove(xid);
      activeTransactions.remove(xid);
    }
    else{
      abort(xid);
    }

    txTimeManager.finishTimer(xid);
    return true;
  }

  public void abort(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("TM::abort called");
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
    txTimeManager.finishTimer(xid);
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
    txTimeManager.resetTimer(xid);

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

    switch (rm.getName()) {
      case ("Flight"): {
        rmTimeManagers.get("Flight").resetTimer(0);
        break;
      }
      case ("Car"): {
        rmTimeManagers.get("Car").resetTimer(0);
        break;
      }
      case ("Room"): {
        rmTimeManagers.get("Room").resetTimer(0);
        break;
      }
    }
  }
}
