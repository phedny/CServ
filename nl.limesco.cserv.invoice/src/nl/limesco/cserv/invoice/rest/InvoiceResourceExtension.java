package nl.limesco.cserv.invoice.rest;

import javax.ws.rs.Path;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountResourceExtension;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.util.pdflatex.PdfLatex;

import org.amdatu.template.processor.TemplateEngine;

@Path("invoices")
public class InvoiceResourceExtension implements AccountResourceExtension {

	private volatile InvoiceService invoiceService;
	
	private volatile TemplateEngine templateEngine;

	private volatile PdfLatex pdfLatex;
	
	@Override
	public InvoiceResource getAccountResourceExtention(Account account, boolean admin) {
		return new InvoiceResource(invoiceService, templateEngine, pdfLatex, account, admin);
	}

}
