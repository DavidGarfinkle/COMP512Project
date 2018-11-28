package Server.Utils;

import Server.Interface.IResourceManager;
import Server.LockManager.*;
import Server.RMI.RMIMiddleware;
import Client.*;
import java.util.Date;
import java.util.TimerTask;
import java.util.*;

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
        System.out.println("TM::client --- xid " + xid + " timed out!");
        TM.abort(xid);
      }
      if (MW != null) {
        System.out.println("MW::resourceManager ---"  + rmName + " timed out!");
        MW.reconnectServer(rmName);
      }
      if (C != null) {
        System.out.println("Client::middleware --- timed out!");
        C.connectServer();
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}