package Server.Utils;

import Server.LockManager.*;
import Server.RMI.RMIMiddleware;
import Server.Interface.*;
import Server.Common.*;
import Client.*;
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
        Trace.info("TransactionManager---Transaction(" + xid + ") timed out! Aborting Transaction");
        TM.abort(xid);
      }
      if (MW != null) {
        TransactionManager TM = MW.TM;
        try {
          // Trace.info(rmName + " checkConnection() called");
          MW.checkConnection(rmName);
          TM.resetRMTimer(rmName);
          // Trace.info(rmName + " checkConnection() finished");
        } catch (Exception e) {
          // Trace.info(rmName + " reconnectServer() called");
          MW.reconnectServer(rmName);
          // Trace.info(rmName + " reconnectServer() finished");
        }
      }
      if (C != null) {
        Vector<String> arguments = new Vector<String>();
        Command cmd = Command.fromString("CheckConnection");
        C.execute(cmd, arguments);
      }
    } catch (Exception e) {
      System.err.println("Exception in Timeout: " + e);
    }
  }
}
