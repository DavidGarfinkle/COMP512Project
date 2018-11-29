package Server.Utils;

import java.io.Serializable;

public class IncrementingInteger implements Serializable {

  int x;

  public IncrementingInteger() {
    this.x = 0;
  }

  public int pick() {
    this.x = this.x + 1;
    return this.x;
  }
}
