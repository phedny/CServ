package nl.limesco.cserv.ideal.targetpay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class IdealHttpToolImpl implements IdealHttpTool {

	@Override
	public String doIdeal(URL url, String parameters) throws IOException {
		final HttpURLConnection conn = prepareConnection(url);
		sendParameters(conn, parameters);
		return readResponse(conn);
	}

	private HttpURLConnection prepareConnection(URL url) throws IOException {
		final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

}
