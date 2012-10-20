package nl.limesco.cserv.cdr.api;

import java.util.Calendar;

public interface Cdr {

	String getSource();
	
	String getCallId();
	
	String getAccount();
	
	Calendar getTime();
	
	String getFrom();
	
	String getTo();
	
	boolean isConnected();
	
	long getSeconds();
	
}
