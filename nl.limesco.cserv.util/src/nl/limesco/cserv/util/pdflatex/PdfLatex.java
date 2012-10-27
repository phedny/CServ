package nl.limesco.cserv.util.pdflatex;

import java.io.IOException;

public interface PdfLatex {

	byte[] compile(String latex) throws InterruptedException, IOException;
	
}
