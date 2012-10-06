package nl.limesco.cserv.ideal.targetpay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Dictionary;

import nl.limesco.cserv.ideal.api.Currency;
import nl.limesco.cserv.ideal.api.IdealException;
import nl.limesco.cserv.ideal.api.IdealService;
import nl.limesco.cserv.ideal.api.Issuer;
import nl.limesco.cserv.ideal.api.Transaction;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

import com.google.common.base.Throwables;

public class IdealServiceImpl implements IdealService, ManagedService {
	
	private final URL IDEAL_START_URL;

	private volatile LogService logService;
	
	private volatile String layoutCode;
	
	public IdealServiceImpl() throws MalformedURLException {
		IDEAL_START_URL = new URL("https://www.targetpay.com/ideal/start");
	}

	@Override
	public Transaction createTransaction(Issuer issuer, Currency currency, int amount, String description, URL returnUrl) throws IdealException {
		final String parameters = buildParameters(issuer, currency, amount, description, returnUrl);

		try {
			final HttpURLConnection conn = prepareConnection();
	
			logService.log(LogService.LOG_DEBUG, "Sending to " + IDEAL_START_URL + ": " + parameters);
			sendParameters(conn, parameters);
			final String response = readResponse(conn);
			logService.log(LogService.LOG_DEBUG, "Received: " + response);
			return parseResponse(issuer, currency, amount, returnUrl, response);
		} catch (IOException e) {
			throw new IdealException(e);
		}
	}

	private String buildParameters(Issuer issuer, Currency currency, int amount, String description, URL returnUrl) {
		try {
			StringBuilder parameters = new StringBuilder();
			parameters.append("rtlo=").append(URLEncoder.encode(layoutCode, "UTF-8"));
			parameters.append("&bank=").append(URLEncoder.encode(issuer.getIdentifier(), "UTF-8"));
			parameters.append("&description=").append(URLEncoder.encode(description, "UTF-8"));
			parameters.append("&currency=").append(URLEncoder.encode(currency.toString(), "UTF-8"));
			parameters.append("&amount=").append(URLEncoder.encode(Integer.toString(amount), "UTF-8"));
			parameters.append("&returnurl=").append(returnUrl.toString().replaceAll("&", ""));
			return parameters.toString().replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw Throwables.propagate(e);
		}
	}

	private HttpURLConnection prepareConnection() throws IOException {
		final HttpURLConnection conn = (HttpURLConnection) IDEAL_START_URL.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		return conn;
	}

	private void sendParameters(HttpURLConnection conn, String parameters) throws IOException {
		final OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
		try {
			writer.append(parameters);
			writer.flush();
		} finally {
			writer.close();
		}
	}

	private String readResponse(HttpURLConnection conn) throws IOException {
		final int responseCode = conn.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			throw new IOException("Received response code " + responseCode + " " + conn.getResponseMessage());
		}
		
		final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		final String response;
		try {
			response = reader.readLine();
		} finally {
			reader.close();
		}
		return response;
	}

	private Transaction parseResponse(Issuer issuer, Currency currency, int amount, URL returnUrl, final String response) throws IdealException {
		// Parse the response
		if (response.startsWith("000000 ")) {
			String r2 = response.substring(7);
			int pos = r2.indexOf('|');
			if (pos == -1 || pos == response.length() - 1) {
				throw new AssertionError(response);
			} else {
				try {
					return new TransactionImpl(r2.substring(0, pos), issuer, currency, amount, returnUrl, new URL(r2.substring(pos + 1)));
				} catch (MalformedURLException e) {
					throw new IdealException(e);
				}
			}
		} else {
			throw new IdealException(response);
		}
	}

	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		if (properties != null) {
			layoutCode = (String) properties.get("layout_code");
			if (layoutCode == null) {
				throw new ConfigurationException("layout_code", "Layout code must be set");
			}
		} else {
			layoutCode = null;
		}
	}

}
