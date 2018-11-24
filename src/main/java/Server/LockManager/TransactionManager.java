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
  private static int TIMEOUT_LENGTH = 120000;

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
    Trace.info("TM::commit called");
		if (!activeTransactions.containsKey(xid)){
			throw new InvalidTransactionException(xid, "Transaction manager cannot commit a transaction that has not been initialized");
		}

    if (involvedResourceManagers.containsKey(xid)) {
      for (IResourceManager rm : involvedResourceManagers.get(xid)) {
        try {
          rm.commit(xid);
        } catch (Exception e) {
          System.out.println(e);
        }
      }
      involvedResourceManagers.remove(xid);
      activeTransactions.remove(xid);
    }
    finishTimer(xid);

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
    finishTimer(xid);
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
