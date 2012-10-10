package nl.limesco.cserv.invoice.api;

public interface DurationItemLine extends ItemLine {

	long getPricePerCall();

	long getPricePerMinute();

	long getNumberOfCalls();

	long getNumberOfSeconds();

}