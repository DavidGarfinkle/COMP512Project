package Server.LockManager;

import java.util.Date;

public class TimeObject extends TransactionObject
{
	private Date m_date = new Date();
	private long curTime;

	// The data members inherited are
	// TransactionObject:: private int m_xid;

	TimeObject()
	{
		super();
	}

	TimeObject(int xid)
	{
		super(xid);
	}

	public void resetTime() {
		curTime = m_date.getTime();
	}

	public long getTime()
	{
		return curTime;
	}
}
