package nl.limesco.cserv.invoice.mongo;

import nl.limesco.cserv.invoice.api.NormalItemLine;

public class NormalItemLineImpl extends AbstractItemLine implements NormalItemLine {

	private long itemPrice;
	
	private long itemCount;

	@Override
	public long getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(long itemPrice) {
		this.itemPrice = itemPrice;
	}

	@Override
	public long getItemCount() {
		return itemCount;
	}

	public void setItemCount(long itemCount) {
		this.itemCount = itemCount;
	}

	@Override
	public long computeTotalPrice() {
		return itemCount * itemPrice;
	}

}
