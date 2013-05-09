package nl.limesco.cserv.invoice.poi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.account.api.Name;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.invoice.api.InvoiceReportService;
import nl.limesco.cserv.invoice.api.InvoiceService;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.osgi.service.log.LogService;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class PoiInvoiceReportService implements InvoiceReportService {
	
	private static final String[] MONTH_NAMES = { "jan", "feb", "maa", "apr", "mei", "jun", "jul", "aug", "sep", "okt", "nov", "dec" };
	
	private static final String[] COLUMN_NAMES = { "Factuurdatum", "Factuurnummer", "Bedrag excl. BTW", "BTW", "Bedrag incl. BTW", "BTW test", "Betaald", "Betaalwijze", "Datum betaald", "Debiteur" };
	
	private volatile InvoiceService invoiceService;
	
	private volatile AccountService accountService;
	
	private volatile LogService logService;

	@Override
	public byte[] getExcelReportForYear(int year) {
		final Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		start.setTimeInMillis(0);
		start.set(Calendar.YEAR, year);
		
		final Calendar end = (Calendar) start.clone();
		end.add(Calendar.YEAR, 1);
		
		return getExcelReportForPeriod(start, end, String.valueOf(year));
	}

	@Override
	public byte[] getExcelReportForQuarter(int year, int quarter) {
		final Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		start.setTimeInMillis(0);
		start.set(Calendar.YEAR, year);
		start.set(Calendar.MONTH, 3 * (quarter - 1));
		
		final Calendar end = (Calendar) start.clone();
		end.add(Calendar.MONTH, 3);
		
		return getExcelReportForPeriod(start, end, year + "-q" + quarter);
	}

	@Override
	public byte[] getExcelReportForMonth(int year, int month) {
		final Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		start.setTimeInMillis(0);
		start.set(Calendar.YEAR, year);
		start.set(Calendar.MONTH, month);
		
		final Calendar end = (Calendar) start.clone();
		end.add(Calendar.MONTH, 1);
		
		return getExcelReportForPeriod(start, end, year + '-' + MONTH_NAMES[month]);
	}

	byte[] getExcelReportForPeriod(Calendar start, Calendar end, String sheetName) {
		final HSSFWorkbook workbook = new HSSFWorkbook();
		final HSSFCreationHelper creationHelper = workbook.getCreationHelper();
		final HSSFCellStyle dateStyle = workbook.createCellStyle();
		dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("DD-MM-YY"));
		
		final HSSFSheet sheet = workbook.createSheet(sheetName);
		sheet.createFreezePane(0, 1);
		
		fillSheet(sheet, dateStyle, start, end);
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			workbook.write(baos);
		} catch (IOException e) {
			logService.log(LogService.LOG_WARNING, "Failed to write Excel file", e);
			return null;
		}
		return baos.toByteArray();
	}

	void fillSheet(Sheet sheet, CellStyle dateStyle, Calendar start, Calendar end) {
		final Map<String, String> accounts = Maps.newHashMap();
		final Row headerRow = sheet.createRow(0);
		
		for (int i = 0; i < COLUMN_NAMES.length; i++) {
			final Cell cell = headerRow.createCell(i);
			cell.setCellValue(COLUMN_NAMES[i]);
		}
		
		int lastRow = 0;
		for (Invoice invoice : invoiceService.getInvoicesByPeriod(start, end)) {
			final Row row = sheet.createRow(++lastRow);
			
			final Cell cell = row.createCell(0);
			cell.setCellValue(invoice.getCreationDate());
			cell.setCellStyle(dateStyle);
			
			row.createCell(1).setCellValue(invoice.getId());
			row.createCell(2).setCellValue(getAmount(invoice.getCurrency(), invoice.getTotalWithoutTaxes()));
			row.createCell(3).setCellValue(getAmount(invoice.getCurrency(), invoice.getTotalWithTaxes() - invoice.getTotalWithoutTaxes()));
			row.createCell(4).setCellValue(getAmount(invoice.getCurrency(), invoice.getTotalWithTaxes()));
			row.createCell(5).setCellFormula(String.format("C%1$d+D%1$d-E%1$d", lastRow + 1));
			row.createCell(9).setCellValue(getAccountName(accounts, invoice.getAccountId()));
		}
	}

	private double getAmount(InvoiceCurrency currency, long amount) {
		return BigDecimal.valueOf(amount)
				.scaleByPowerOfTen(-(currency.hiddenDigits + currency.fractionDigits))
				.setScale(2, RoundingMode.DOWN)
				.doubleValue();
	}
	
	private String getAccountName(Map<String, String> accounts, String accountId) {
		if (!accounts.containsKey(accountId)) {
			final Optional<? extends Account> account = accountService.getAccountById(accountId);
			if (!account.isPresent()) {
				accounts.put(accountId, null);
			} else if (account.get().getCompanyName() != null) {
				accounts.put(accountId, account.get().getCompanyName());
			} else {
				final Name fullName = account.get().getFullName();
				accounts.put(accountId, fullName != null ? fullName.getFullName() : null);
			}
		}
		
		return accounts.get(accountId);
	}

}
