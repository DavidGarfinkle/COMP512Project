package Server.Utils;

import Server.LockManager.*;
import java.util.Date;
import java.util.TimerTask;

public class Timeout extends TimerTask{

  TransactionManager TM;
  int xid;

  public Timeout(int xid, TransactionManager TM) {
    this.xid = xid;
    this.TM = TM;
  }

  public void run() {
    try {
      TM.abort(xid);
    } catch (Exception e) {
      System.out.println(e);
    }
    // if (m_date.getTime() - obj.getTime() > 30000) {
    //   try {
    //     TM.abort(obj.getXId());
    //   } catch (Exception e) {
    //     System.out.println(e);
    //   }
    // }
  }
}
