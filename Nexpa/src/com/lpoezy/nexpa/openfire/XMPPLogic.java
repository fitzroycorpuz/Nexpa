package com.lpoezy.nexpa.openfire;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import android.util.Log;

public class XMPPLogic {
	private XMPPConnection connection = null;

	  private static XMPPLogic instance = null;

	  public synchronized static XMPPLogic getInstance() {
	    if(instance==null){
	      instance = new XMPPLogic();
	    }
	    return instance;
	  }

	  public void setConnection(XMPPConnection connection){
	    this.connection = connection;
	    Log.e("XMPPLogic","Setting connection...");
	  }

	  public XMPPConnection getConnection() {
		  Log.e("XMPPLogic","Retrireving connection...");
	    return this.connection;
	  }

}
