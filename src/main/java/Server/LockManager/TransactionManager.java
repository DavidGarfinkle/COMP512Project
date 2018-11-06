package Server.LockManager;

import java.util.*;
import java.rmi.RemoteException;

import Server.Common.*;

import utils.*;

public class TransactionManager {

  private static int TABLE_SIZE = 2039;

  private static TPHashTable activeTransactions = new TPHashTable(TransactionManager.TABLE_SIZE);
  private static RMHashtable involvedResourceManagers = new RMHashtable();

  private static IncrementingInteger txidPicker = new IncrementingInteger();

  public void TransactionManager() {
  }

  public int start(ResourceManager[] resourceManagers) throws RemoteException {
      int txid = txidPicker.pick();
      TransactionObject tx = new TimeObject(txid);

      if (!activeTransactions.contains(tx)){
        activeTransactions.add(tx);
      }
      // Overloaded put() will allocate new vector if first times
      // also will only add distinct ResourceManagers to the vector hashed at txid
      involvedResourceManagers.put(tx.getXId(), resourceManagers);

      return txid;
  }
}
