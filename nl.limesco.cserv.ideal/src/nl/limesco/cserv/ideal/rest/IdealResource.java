package nl.limesco.cserv.ideal.rest;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import nl.limesco.cserv.ideal.api.Issuer;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.collect.Maps;

@Path("ideal")
public class IdealResource {

	private final Map<String, Issuer> issuers = Maps.newConcurrentMap();
	
	public void addIssuer(Issuer issuer) {
		issuers.put(issuer.getIdentifier(), issuer);
	}
	
	public void removeIssuer(Issuer issuer) {
		issuers.remove(issuer.getIdentifier());
	}
	
	@GET
	@Path("issuers")
	@Produces(MediaType.APPLICATION_JSON)
	public String getIssuers() {
		try {
			return new ObjectMapper().writeValueAsString(issuers.values());
		} catch (JsonGenerationException e) {
			throw new WebApplicationException(e);
		} catch (JsonMappingException e) {
			throw new WebApplicationException(e);
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}
	}
	
}
