package Server.LockManager;

import Server.Common.*;

public class TransactionManager {

  private static int TABLE_SIZE = 2039;

  private static TPHashTable activeTransactions = new TPHashTable(TransactionManager.TABLE_SIZE);

  private static IncrementingInteger txidPicker = new IncrementingInteger();

  public void TransactionManager() {
  }

  public boolean start(ResourceManager rm) throws RemoteException {
      int txid = txidPicker.pick();
      TransactionObject tx = new TransactionObject(txid);


  }
}
