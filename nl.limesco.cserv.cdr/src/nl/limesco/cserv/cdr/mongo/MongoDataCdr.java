package nl.limesco.cserv.cdr.mongo;

import nl.limesco.cserv.cdr.api.DataCdr;

public class MongoDataCdr extends AbstractMongoCdr implements DataCdr {

	private long kilobytes;

	public MongoDataCdr() {
		// Default constructor.
	}

	public MongoDataCdr(DataCdr cdr) {
		super(cdr);
		this.kilobytes = cdr.getKilobytes();
	}

	@Override
	public long getKilobytes() {
		return kilobytes;
	}
	
	public void setKilobytes(long kilobytes) {
		this.kilobytes = kilobytes;
	}

}
