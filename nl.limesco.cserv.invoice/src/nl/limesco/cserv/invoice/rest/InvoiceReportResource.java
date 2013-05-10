package nl.limesco.cserv.invoice.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import nl.limesco.cserv.auth.api.Role;
import nl.limesco.cserv.auth.api.WebAuthorizationService;
import nl.limesco.cserv.invoice.api.InvoiceReportService;

@Path("invoicereports")
public class InvoiceReportResource {

	private static final String[] MONTH_NAMES = { "jan", "feb", "maa", "apr", "mei", "jun", "jul", "aug", "sep", "okt", "nov", "dec" };
	
	private volatile WebAuthorizationService authorizationService;

	private volatile InvoiceReportService invoiceReportService;
	
	@GET
	@Path("{year}")
	@Produces("application/vnd.ms-excel")
	public Response getReportForYear(@Context HttpServletRequest request, @PathParam("year") int year) {
		authorizationService.requireUserRole(request, Role.ADMIN);
		
		return Response.ok(invoiceReportService.getExcelReportForYear(year))
				.header("Content-Disposition", "attachment; filename=invoices-" + year + ".xls")
				.build();
	}

	@GET
	@Path("{year}/q/{quarter}")
	@Produces("application/vnd.ms-excel")
	public Response getReportForQuarter(@Context HttpServletRequest request, @PathParam("year") int year, @PathParam("quarter") int quarter) {
		authorizationService.requireUserRole(request, Role.ADMIN);
		
		return Response.ok(invoiceReportService.getExcelReportForQuarter(year, quarter))
				.header("Content-Disposition", "attachment; filename=invoices-" + year + "-q" + quarter + ".xls")
				.build();
	}
	
	@GET
	@Path("{year}/m/{month}")
	@Produces("application/vnd.ms-excel")
	public Response getReportForMonth(@Context HttpServletRequest request, @PathParam("year") int year, @PathParam("month") int month) {
		authorizationService.requireUserRole(request, Role.ADMIN);
		
		return Response.ok(invoiceReportService.getExcelReportForMonth(year, month))
				.header("Content-Disposition", "attachment; filename=invoices-" + year + "-" + MONTH_NAMES[month] + ".xls")
				.build();
	}
	
}
