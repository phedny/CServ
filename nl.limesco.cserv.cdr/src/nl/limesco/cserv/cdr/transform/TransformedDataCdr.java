package nl.limesco.cserv.cdr.transform;

import nl.limesco.cserv.cdr.api.DataCdr;

public class TransformedDataCdr extends AbstractTransformedCdr implements DataCdr {
	
	private final DataCdr input;

	public TransformedDataCdr(DataCdr input) {
		super(input);
		this.input = input;
	}

	@Override
	public long getKilobytes() {
		return input.getKilobytes();
	}

}