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
  String timerName;
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

  private Timeout createTimeout(int xid) {
    Timeout timeout = null;
    if (TM != null) {
      timeout = new Timeout(xid, TM);
      timerName = "TransactionManager---Transaction(" + xid + ")";
    }
    return timeout;
  }

  private Timeout createTimeout() {
    Timeout timeout = null;
    if (MW != null) {
      timeout =  new Timeout(MW, rmName);
      timerName = "Middleware---RM:(" + rmName + ")";
    }
    if (C != null) {
      timeout = new Timeout(C);
      timerName = "Client---Middleware";
    }
    return timeout;
  }

  public void resetTimer(int xid) {
    // cancel previous TimerTask
    TimerTask timeout = timeoutTable.get(xid);
    timeout.cancel();

    // schedule new TimerTask
    timeout = createTimeout(xid);
    timer.schedule(timeout, TIMEOUT_LENGTH);
    timeoutTable.put(xid, timeout);
    // Trace.info(timerName + "::resetTimer called");
  }

  public void resetTimer() {
    // cancel previous TimerTask
    TimerTask timeout = timeoutTable.get(xid);
    timeout.cancel();

    // schedule new TimerTask
    timeout = createTimeout();
    timer.schedule(timeout, TIMEOUT_LENGTH);
    timeoutTable.put(xid, timeout);
    // Trace.info(timerName + "::resetTimer called");
  }

  public void startTimer(int xid) {
    TimerTask timeout = createTimeout(xid);
    timer.schedule(timeout, TIMEOUT_LENGTH);
    timeoutTable.put(xid, timeout);
    // Trace.info(timerName + "::startTimer called");
  }

  public void startTimer() {
    TimerTask timeout = createTimeout();
    timer.schedule(timeout, TIMEOUT_LENGTH);
    timeoutTable.put(xid, timeout);
    // Trace.info(timerName + "::startTimer called");
  }

  public void finishTimer(int xid) {
    timeoutTable.get(xid).cancel();
    // Trace.info(timerName + "::finishTimer called");
  }

  public void finishTimer() {
    timeoutTable.get(xid).cancel();
    // Trace.info(timerName + "::finishTimer called");
  }
}