package nl.limesco.cserv.invoice.transform;

import java.io.IOException;
import java.net.URL;

import javax.ws.rs.WebApplicationException;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceTransformationService;
import nl.limesco.cserv.util.pdflatex.PdfLatex;

import org.amdatu.template.processor.TemplateContext;
import org.amdatu.template.processor.TemplateEngine;
import org.amdatu.template.processor.TemplateException;
import org.amdatu.template.processor.TemplateProcessor;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class InvoiceTransformationServiceImpl implements InvoiceTransformationService {

	private volatile TemplateEngine templateEngine;

	private volatile PdfLatex pdfLatex;
	
	@Override
	public String transformToJson(Invoice invoice) {
		try {
			return new ObjectMapper().writeValueAsString(invoice);
		} catch (JsonGenerationException e) {
			throw new WebApplicationException(e);
		} catch (JsonMappingException e) {
			throw new WebApplicationException(e);
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}
	}

	@Override
	public String transformToTex(Invoice invoice, Account account) {
		final TemplateContext context = templateEngine.createContext();
		context.put("invoice", invoice);
		context.put("account", account);
		context.put("util", VelocityUtils.class);

		final URL template = getClass().getClassLoader().getResource("resources/invoice.tex");
		try {
			TemplateProcessor processor = templateEngine.createProcessor(template);
			return processor.generateString(context);
		} catch (TemplateException e) {
			return null;
		}
	}

	@Override
	public byte[] transformToPdf(Invoice invoice, Account account) {
		final String invoiceAsTex = transformToTex(invoice, account);
		try {
			return pdfLatex.compile(invoiceAsTex);
		} catch (InterruptedException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

}
