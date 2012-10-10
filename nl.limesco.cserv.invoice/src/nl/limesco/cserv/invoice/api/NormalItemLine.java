package nl.limesco.cserv.invoice.api;

public interface NormalItemLine extends ItemLine {

	long getItemPrice();

	long getItemCount();

}