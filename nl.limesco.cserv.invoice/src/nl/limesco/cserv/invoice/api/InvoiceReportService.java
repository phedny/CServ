package nl.limesco.cserv.invoice.api;

public interface InvoiceReportService {

	byte[] getExcelReportForYear(int year);
	
	byte[] getExcelReportForQuarter(int year, int quarter);
	
	byte[] getExcelReportForMonth(int year, int month);
	
}
