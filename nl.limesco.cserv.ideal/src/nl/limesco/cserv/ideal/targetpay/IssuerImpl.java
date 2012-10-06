package nl.limesco.cserv.ideal.targetpay;

import nl.limesco.cserv.ideal.api.Issuer;

public class IssuerImpl implements Issuer {

	private final String identifier;
	
	private final String name;
	
	public IssuerImpl(String identifier, String name) {
		this.identifier = identifier;
		this.name = name;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String getName() {
		return name;
	}

}
