package nl.limesco.cserv.util.pdflatex.impl;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Random;

import nl.limesco.cserv.util.pdflatex.PdfLatex;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public class PdfLatexImpl implements PdfLatex, ManagedService {
	
	private volatile BundleContext context;
	
	private volatile String binaryPath;
	
	private synchronized File createWorkingDirectory() {
		final String name = Calendar.getInstance().getTimeInMillis() + "-" + new Random().nextInt();
		final File file = context.getDataFile(name);
		checkState(file.mkdir());
		return file;
	}

	@Override
	public byte[] compile(String latex) throws InterruptedException, IOException {
		final File dir = createWorkingDirectory();
		try {
			
			final File source = new File(dir, "file.tex");
			final Writer writer = new OutputStreamWriter(new FileOutputStream(source));
			try {
				writer.append(latex);
			} finally {
				writer.close();
			}
			
			final ProcessBuilder processBuilder = new ProcessBuilder(binaryPath, "-halt-on-error", "-interaction=batchmode", "file.tex");
			processBuilder.directory(dir);
			final int exitCode = processBuilder.start().waitFor();
			checkState(exitCode == 0);

			final File pdf = new File(dir, "file.pdf");
			checkState(pdf.canRead());
			final long fileLength = pdf.length();
			final byte output[] = new byte[(int) fileLength];
			
			final InputStream inputStream = new FileInputStream(pdf);
			int bytes = 0;
			try {
				while (bytes < output.length) {
					int newBytes = inputStream.read(output, bytes, output.length - bytes);
					checkState(newBytes > 0);
					bytes += newBytes;
				}
			} finally {
				inputStream.close();
			}
			
			return output;
			
		} finally {
			for (File file : dir.listFiles()) {
				file.delete();
			}
			dir.delete();
		}
	}

	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		if (properties != null) {
			binaryPath = (String) properties.get("binary_path");
		}
	}

}
