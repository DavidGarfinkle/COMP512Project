package Server.LockManager;

import java.util.*;

import Server.Common.*;

public class TransactionManager {

  private static int TABLE_SIZE = 2039;

  private static TPHashTable activeTransactions = new TPHashTable(TransactionManager.TABLE_SIZE);
  private static RMHashTable involvedResourceManagers = new HashTable<Integer, ResourceManager[]>();

  private static IncrementingInteger txidPicker = new IncrementingInteger();

  public void TransactionManager() {
  }

  public boolean start(ResourceManager[] resourceManagers) throws RemoteException {
      int txid = txidPicker.pick();
      TransactionObject tx = new TransactionObject(txid);

      activeTransactions.add(tx);
      involvedResourceManagers.put(tx.getXId(), resourceManagers);
  }
}
