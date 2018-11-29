package Server.Common;

import Server.LockManager.*;
import java.io.*;
import java.util.*;
import Server.Utils.*;

// Resource manager data item
public class TransactionRecord implements Serializable, Cloneable
{
	public Hashtable<Integer, TransactionObject> activeTransactions = new Hashtable<Integer, TransactionObject>();
  public Hashtable<Integer, Vector<String>> involvedResourceManagers = new Hashtable<Integer, Vector<String>>();
	public IncrementingInteger xidPicker = new IncrementingInteger();
	public TransactionRecord() { 
		super();
	}
	
	public void setActive(Hashtable<Integer, TransactionObject> activeTransactions) {
		this.activeTransactions = activeTransactions;
	}

	public void setInvolved(Hashtable<Integer, Vector<String>> involvedResourceManagers) {
		this.involvedResourceManagers = involvedResourceManagers;
	}

	public void setPicker(IncrementingInteger xidPicker) {
		this.xidPicker = xidPicker;
	}

	public Hashtable<Integer, TransactionObject> getActive() {
		return this.activeTransactions;
	}

	public Hashtable<Integer, Vector<String>> getInvolved() {
		return this.involvedResourceManagers;
	}

	public IncrementingInteger getPicker() {
		return this.xidPicker;
	}

	// public void setTM(TransactionManager TM) {
	// 	this.TM = TM;
	// }

	// public TransactionManager getTM() {
	// 	return this.TM;
	// }
}

