package utils;

public class IncrementingInteger {

  int x;

  public void IncrementingInteger() {
    this.x = 0;
  }

  public int pick() {
    this.x = this.x + 1;
    return this.x;
  }
}
