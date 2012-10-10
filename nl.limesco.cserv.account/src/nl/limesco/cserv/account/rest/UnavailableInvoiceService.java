package nl.limesco.cserv.account.rest;

import java.util.Collection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceService;

import com.google.common.base.Optional;

public class UnavailableInvoiceService implements InvoiceService {

	@Override
	public Optional<? extends Invoice> getInvoiceById(String id) {
		throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
	}

	@Override
	public Collection<? extends Invoice> getInvoicesByAccountId(String accountId) {
		throw new WebApplicationException(Status.SERVICE_UNAVAILABLE);
	}

}
