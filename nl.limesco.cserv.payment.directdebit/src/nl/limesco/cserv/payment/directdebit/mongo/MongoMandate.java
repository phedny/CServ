package nl.limesco.cserv.payment.directdebit.mongo;

import java.util.Calendar;
import java.util.Date;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;
import nl.limesco.cserv.payment.directdebit.api.Mandate;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class MongoMandate implements Mandate {
	
	private String id;
	
	private String creditorId;
	
	private String accountId;
	
	private boolean active;
	
	private String name;
	
	private String address;
	
	private String postalCode;
	
	private String locality;
	
	private String country;
	
	private String iban;
	
	private String bic;
	
	private String signatureLocality;
	
	private Calendar signatureDate;

	@Id
	@ObjectId
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getCreditorId() {
		return creditorId;
	}

	@Override
	public String getAccountId() {
		return accountId;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getPostalCode() {
		return postalCode;
	}

	@Override
	public String getLocality() {
		return locality;
	}

	@Override
	public String getCountry() {
		return country;
	}

	@Override
	public String getIban() {
		return iban;
	}

	@Override
	public String getBic() {
		return bic;
	}

	@Override
	public String getSignatureLocality() {
		return signatureLocality;
	}

	@Override
	@JsonIgnore
	public Calendar getSignatureDate() {
		return (Calendar) signatureDate.clone();
	}
	
	@JsonProperty("signatureDate")
	public Date getSignatureDateAsDate() {
		if (signatureDate == null) {
			return null;
		} else {
			return signatureDate.getTime();
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCreditorId(String creditorId) {
		this.creditorId = creditorId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public void setSignatureLocality(String signatureLocality) {
		this.signatureLocality = signatureLocality;
	}

	public void setSignatureDate(Calendar signatureDate) {
		this.signatureDate = (Calendar) signatureDate.clone();
	}
	
	public void setSignatureDateAsDate(Date signatureDate) {
		if (signatureDate == null) {
			this.signatureDate = null;
		} else {
			final Calendar newCalendar = Calendar.getInstance();
			newCalendar.setTime(signatureDate);
			this.signatureDate = newCalendar;
		}
	}

}
