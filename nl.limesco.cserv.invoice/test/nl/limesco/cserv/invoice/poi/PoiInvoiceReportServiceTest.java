package nl.limesco.cserv.invoice.poi;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;

import nl.limesco.cserv.account.api.Account;
import nl.limesco.cserv.account.api.AccountService;
import nl.limesco.cserv.invoice.api.Invoice;
import nl.limesco.cserv.invoice.api.InvoiceCurrency;
import nl.limesco.cserv.invoice.api.InvoiceService;
import nl.limesco.cserv.invoice.mongo.InvoiceBuilderImpl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.common.base.Optional;

@RunWith(MockitoJUnitRunner.class)
public class PoiInvoiceReportServiceTest {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	@Spy @InjectMocks
	private PoiInvoiceReportService service;
	
	@Mock
	private InvoiceService invoiceService;
	
	@Mock
	private AccountService accountService;

	@Mock
	private Sheet sheet;
	
	@Before
	public void setUp() {
		final Answer<Collection<? extends Invoice>> invoiceAnswer = new Answer<Collection<? extends Invoice>>() {

			@Override
			public Collection<? extends Invoice> answer(InvocationOnMock invocation) throws Throwable {
				return Arrays.asList(
						new InvoiceBuilderImpl()
								.accountId("a")
								.id("TEST1")
								.creationDate(calendar("07-05-2013"))
								.currency(InvoiceCurrency.EUR)
								.normalItemLine("Item 1", 1, 150000, 0.21)
								.normalItemLine("Item 2", 1, 160000, 0.21)
								.buildInvoice(),
						new InvoiceBuilderImpl()
								.accountId("b")
								.id("TEST2")
								.creationDate(calendar("08-05-2013"))
								.currency(InvoiceCurrency.EUR)
								.normalItemLine("Item 1", 1, 150000, 0.21)
								.buildInvoice()
						);
			}
		};
		
		when(invoiceService.getInvoicesByPeriod(any(Calendar.class), any(Calendar.class))).then(invoiceAnswer);
		
		final Answer<Optional<? extends Account>> accountAnswer = new Answer<Optional<? extends Account>>() {

			@Override
			public Optional<? extends Account> answer(InvocationOnMock invocation) throws Throwable {
				final String accountId = (String) invocation.getArguments()[0];
				final Account account = mock(Account.class);
				when(account.getCompanyName()).thenReturn("Company " + accountId);
				return Optional.of(account);
			}
		};
		
		when(accountService.getAccountById(any(String.class))).then(accountAnswer);
	}

	private Calendar calendar(String day) throws ParseException {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(DAY_FORMAT.parse(day));
		return calendar;
	}

	@Test
	public void threeRowsAreCreated() throws ParseException {
		when(sheet.createRow(anyInt())).thenReturn(mock(Row.class, withSettings().defaultAnswer(RETURNS_MOCKS)));
		
		service.fillSheet(sheet, null, calendar("01-05-2013"), calendar("01-06-2013"));
		
		verify(sheet, times(3)).createRow(anyInt());
	}

	@Test
	public void headerRowIsCreated() throws ParseException {
		final Row headerRow = mock(Row.class, withSettings().defaultAnswer(RETURNS_MOCKS));
		when(sheet.createRow(anyInt())).thenReturn(mock(Row.class, withSettings().defaultAnswer(RETURNS_MOCKS)));
		when(sheet.createRow(0)).thenReturn(headerRow);
		
		service.fillSheet(sheet, null, calendar("01-05-2013"), calendar("01-06-2013"));
		
		verify(headerRow, times(10)).createCell(anyInt());
	}

	@Test
	public void secondRowContainsFirstInvoice() throws ParseException {
		final Row invoiceRow = mock(Row.class, withSettings().defaultAnswer(RETURNS_MOCKS));
		when(sheet.createRow(anyInt())).thenReturn(mock(Row.class, withSettings().defaultAnswer(RETURNS_MOCKS)));
		when(sheet.createRow(1)).thenReturn(invoiceRow);
		
		final Cell dateCell = mock(Cell.class);
		when(invoiceRow.createCell(0)).thenReturn(dateCell);
		final Cell idCell = mock(Cell.class);
		when(invoiceRow.createCell(1)).thenReturn(idCell);
		final Cell exclVatCell = mock(Cell.class);
		when(invoiceRow.createCell(2)).thenReturn(exclVatCell);
		final Cell vatCell = mock(Cell.class);
		when(invoiceRow.createCell(3)).thenReturn(vatCell);
		final Cell inclVatCell = mock(Cell.class);
		when(invoiceRow.createCell(4)).thenReturn(inclVatCell);
		final Cell debtorCell = mock(Cell.class);
		when(invoiceRow.createCell(9)).thenReturn(debtorCell);

		service.fillSheet(sheet, null, calendar("01-05-2013"), calendar("01-06-2013"));
		
		verify(dateCell).setCellValue(calendar("07-05-2013"));
		verify(idCell).setCellValue("TEST1");
		verify(exclVatCell).setCellValue(31d);
		verify(vatCell).setCellValue(6.51d);
		verify(inclVatCell).setCellValue(37.51d);
		verify(debtorCell).setCellValue("Company a");
	}

	@Test
	public void thirdRowContainsFirstInvoice() throws ParseException {
		final Row invoiceRow = mock(Row.class, withSettings().defaultAnswer(RETURNS_MOCKS));
		when(sheet.createRow(anyInt())).thenReturn(mock(Row.class, withSettings().defaultAnswer(RETURNS_MOCKS)));
		when(sheet.createRow(2)).thenReturn(invoiceRow);
		
		final Cell dateCell = mock(Cell.class);
		when(invoiceRow.createCell(0)).thenReturn(dateCell);
		final Cell idCell = mock(Cell.class);
		when(invoiceRow.createCell(1)).thenReturn(idCell);
		final Cell exclVatCell = mock(Cell.class);
		when(invoiceRow.createCell(2)).thenReturn(exclVatCell);
		final Cell vatCell = mock(Cell.class);
		when(invoiceRow.createCell(3)).thenReturn(vatCell);
		final Cell inclVatCell = mock(Cell.class);
		when(invoiceRow.createCell(4)).thenReturn(inclVatCell);
		final Cell debtorCell = mock(Cell.class);
		when(invoiceRow.createCell(9)).thenReturn(debtorCell);

		service.fillSheet(sheet, null, calendar("01-05-2013"), calendar("01-06-2013"));
		
		verify(dateCell).setCellValue(calendar("08-05-2013"));
		verify(idCell).setCellValue("TEST2");
		verify(exclVatCell).setCellValue(15d);
		verify(vatCell).setCellValue(3.15d);
		verify(inclVatCell).setCellValue(18.15d);
		verify(debtorCell).setCellValue("Company b");
	}
	
}
