package Server.Utils;

public class IncrementingInteger {

  int x;

  public IncrementingInteger() {
    this.x = 0;
  }

  public int pick() {
    this.x = this.x + 1;
    return this.x;
  }
}
