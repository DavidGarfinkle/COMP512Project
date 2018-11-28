package Server.Common;

import java.io.*;

// Resource manager data item
public class MasterRecord implements Serializable, Cloneable
{
	private int xid; 
  private String path; 
  public MasterRecord() { 
		super();
	}
	
	public void set(int xid) {
		this.xid = xid;
	}

	public void set(String path) {
		this.path = path;
	}

	public void set(int xid, String path) {
		this.xid = xid;
		this.path = path;
	}

	public int getXid() {
		return this.xid;
	}

	public String getPath() {
		return this.path;
	}
}

