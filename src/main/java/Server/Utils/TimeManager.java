package Server.Utils;

import Server.Common.*;
import Server.LockManager.*;
import Server.RMI.*;
import Client.*;
import java.util.TimerTask;
import java.util.*;

public class TimeManager {

  protected Hashtable<Integer, TimerTask> timeoutTable = new Hashtable<Integer, TimerTask>();
  private Timer timer;
  private int TIMEOUT_LENGTH;
  TransactionManager TM;
  RMIMiddleware MW;
  RMIClient C;
  String rmName;
  int xid;

  public TimeManager (int length, TransactionManager TM) {
    this.TIMEOUT_LENGTH = length;
    this.timer = new Timer();
    this.TM = TM;
  }

  public TimeManager (int length, RMIMiddleware MW, String rmName) {
    this.TIMEOUT_LENGTH = length;
    this.timer = new Timer();
    this.MW = MW;
    this.rmName = rmName;
  }

  public TimeManager (int length, RMIClient C) {
    this.TIMEOUT_LENGTH = length;
    this.timer = new Timer();
    this.C = C;
  }

  private Timeout createTimeout() {
    Timeout timeout = null;
    if (TM != null) {
      timeout = new Timeout(xid, TM);
    }
    if (MW != null) {
      timeout =  new Timeout(MW, rmName);
    }
    if (C != null) {
      timeout = new Timeout(C);
    }
    return timeout;
  }

  public void resetTimer(int xid) {
    Trace.info("TimeManager::resetTimer(" + xid + ") called");
    // cancel previous TimerTask
    TimerTask timeout = timeoutTable.get(xid);
    timeout.cancel();

    // schedule new TimerTask
    timeout = createTimeout();
    timer.schedule(timeout, TIMEOUT_LENGTH);
    timeoutTable.put(xid, timeout);
  }

  public void startTimer(int xid) {
    Trace.info("TimeManager::startTimer(" + xid + ") called");
    TimerTask timeout = createTimeout();
    timer.schedule(timeout, TIMEOUT_LENGTH);
    timeoutTable.put(xid, timeout);
  }

  public void finishTimer(int xid) {
    Trace.info("TimeManager::startTimer" + xid + ") called");
    timeoutTable.get(xid).cancel();
  }
}