package Server.LockManager;

import java.util.Date;
import java.io.*;

public class TimeObject extends TransactionObject implements Serializable
{
	private Date m_date = new Date();
	private long curTime;

	// The data members inherited are
	// TransactionObject:: private int m_xid;

	public TimeObject()
	{
		super();
	}

	public TimeObject(int xid)
	{
		super(xid);
	}

	public void resetTime() {
		this.m_date = new Date();
	}

	public long getTime()
	{
		return m_date.getTime();
	}
}
