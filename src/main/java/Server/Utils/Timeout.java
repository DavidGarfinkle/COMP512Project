package Server.Utils;

import Server.LockManager.*;
import java.util.Date;
import java.util.TimerTask;
import java.util.*;

public class Timeout extends TimerTask{

  TransactionManager TM;
  int xid;

  public Timeout(int xid, TransactionManager TM) {
    this.xid = xid;
    this.TM = TM;
  }

  public void run() {
    System.out.println("Timeout::run() --- xid " + this.xid + " timing out!");
    try {
      TM.abort(xid);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
