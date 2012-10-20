package nl.limesco.cserv.cdr.retriever.steps;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import nl.limesco.cserv.cdr.api.Cdr;

import org.apache.http.client.HttpClient;
import org.osgi.service.cm.ConfigurationException;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Throwables;

class ParseCsvStep extends Step {
	
	private final String inputVar;
	
	private final char delimiterChar;
	
	private final char quoteChar;
	
	private final int skipLines;
	
	private final String[] fields;
	
	ParseCsvStep(Dictionary properties, int index) throws ConfigurationException {
		super(properties, index);
		inputVar = (String) properties.get(index + "_inputvar");
		final String delimiterString = (String) properties.get(index + "_delimiterchar");
		if (delimiterString.length() != 1) {
			throw new ConfigurationException(index + "_delimiterchar", "Delimiter char must be single char");
		}
		delimiterChar = delimiterString.charAt(0);
		final String quoteString = (String) properties.get(index + "_quotechar");
		if (quoteString.length() != 1) {
			throw new ConfigurationException(index + "_quotechar", "Quote char must be single char");
		}
		quoteChar = quoteString.charAt(0);
		final String skipLinesString = (String) properties.get(index + "_skiplines");
		try {
			skipLines = Integer.parseInt(skipLinesString);
		} catch (NumberFormatException e) {
			throw new ConfigurationException(index + "_skiplines", "Skip lines must a numeric", e);
		}
		fields = ((String) properties.get(index + "_fields")).split(",");
	}

	@Override
	public boolean execute(HttpClient client, Map<String, Object> variables) throws IOException {
		final InputStream inputStream = (InputStream) variables.remove(inputVar);
		final CSVReader reader = new CSVReader(new InputStreamReader(inputStream), delimiterChar, quoteChar, skipLines);
		variables.put(outputVar, new CdrIterator(reader));
		return true;
	}

	private final class CdrIterator implements Iterator<Cdr> {
		private final CSVReader reader;
		private String[] next;

		private CdrIterator(CSVReader reader) {
			this.reader = reader;
		}

		@Override
		public boolean hasNext() {
			if (next != null) {
				return true;
			}
			
			try {
				next = reader.readNext();
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
			return next != null;
		}

		@Override
		public Cdr next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			
			try {
				final Cdr cdr = new CsvLineCdr(source, fields, next);
				next = null;
				return cdr;
			} catch (ParseException e) {
				throw Throwables.propagate(e);
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}