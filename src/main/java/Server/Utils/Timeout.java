package Server.Utils;

import Server.LockManager.*;
import Server.RMI.RMIMiddleware;
import Client.*;
import java.util.TimerTask;

public class Timeout extends TimerTask{

  TransactionManager TM;
  RMIMiddleware MW;
  RMIClient C;
  String rmName;
  int xid;

  public Timeout(int xid, TransactionManager TM) {
    this.xid = xid;
    this.TM = TM;
  }

  public Timeout(RMIMiddleware MW, String rmName) {
    this.MW = MW;
    this.rmName = rmName;
  }

  public Timeout(RMIClient C) {
    this.C = C;
  }

  public void run() {
    try {
      if (TM != null) {
        System.out.println("TransactionManager---Transaction(" + xid + ") timed out! Aborting Transaction");
        TM.abort(xid);
      }
      if (MW != null) {
        System.out.println("Middleware---RM:(" + rmName + ") timed out! Reconnecting to RM");
        MW.reconnectServer(rmName);
      }
      if (C != null) {
        System.out.println("Client---Middleware timed out! Reconnecting to Middleware");
        C.reconnectServer();
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}