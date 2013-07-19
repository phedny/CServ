package nl.limesco.cserv.invoice.api;

public class PhoneNumberMissingException extends Exception {
	private static final long serialVersionUID = 1833870249234492153L;

	public PhoneNumberMissingException(String phoneNumber) {
		super("Phone number could not be matched to SIM: " + phoneNumber);
	}
}
