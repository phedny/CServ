package nl.limesco.cserv.cdr.transform;

import java.util.Calendar;
import java.util.Map;

import nl.limesco.cserv.cdr.api.Cdr;

import com.google.common.base.Optional;

public abstract class AbstractTransformedCdr implements Cdr {
	
	private final Cdr input;
	
	public AbstractTransformedCdr(Cdr input) {
		this.input = input;
	}

	@Override
	public String getSource() {
		return input.getSource();
	}

	@Override
	public String getCallId() {
		return input.getCallId();
	}

	@Override
	public Optional<String> getAccount() {
		return input.getAccount();
	}

	@Override
	public Calendar getTime() {
		return input.getTime();
	}

	@Override
	public String getFrom() {
		return input.getFrom();
	}

	@Override
	public String getTo() {
		return input.getTo();
	}

	@Override
	public Map<String, String> getAdditionalInfo() {
		return input.getAdditionalInfo();
	}

	@Override
	public Optional<String> getInvoice() {
		return input.getInvoice();
	}

	@Override
	public Optional<String> getInvoiceBuilder() {
		return input.getInvoiceBuilder();
	}

	@Override
	public Optional<Cdr.Pricing> getPricing() {
		return input.getPricing();
	}
	
}