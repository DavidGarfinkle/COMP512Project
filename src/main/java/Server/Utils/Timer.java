package Server.Utils;

import Server.LockManager.*;
import java.util.Date;

public class Timer extends Thread{

  private Date m_date = new Date();
  TimeObject obj;

  public Timer(TimeObject obj) {
    this.obj = obj;
  }

  public void run() {
    while(true) {
      if (m_date.getTime() - obj.getTime() > 30000) {
        break;
      }
    }
  }
}
