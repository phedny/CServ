package nl.limesco.cserv.pricing.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import nl.limesco.cserv.pricing.api.DataPricingRule;
import nl.limesco.cserv.pricing.api.PricingService;
import nl.limesco.cserv.pricing.api.SmsPricingRule;
import nl.limesco.cserv.pricing.api.VoicePricingRule;

import com.google.common.collect.Sets;

@Path("pricing")
public class PricingResource {

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyyMMdd") {{
		setTimeZone(TimeZone.getTimeZone("UTC"));
	}};

	private volatile PricingService pricingService;
	
	@Path("current")
	public PricingSubResource getCurrent() {
		return new PricingSubResource(Calendar.getInstance());
	}
	
	@Path("{day}")
	public PricingSubResource getByDate(@PathParam("day") String day) {
		try {
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(DAY_FORMAT.parse(day));
			return new PricingSubResource(calendar);
		} catch (ParseException e) {
			throw new WebApplicationException(e, Status.NOT_FOUND);
		}
	}
	
	public class PricingSubResource {
		
		private final Calendar day;
		
		public PricingSubResource(Calendar day) {
			this.day = day;
		}

		@GET
		@Path("voice")
		@Produces(MediaType.APPLICATION_JSON)
		public Collection<RestPricingRule> getVoice() {
			final Collection<RestPricingRule> pricingRules = Sets.newHashSet();
			for (VoicePricingRule rule : pricingService.getApplicablePricingRules(VoicePricingRule.class, day)) {
				pricingRules.add(new RestPricingRule(rule));
			}
			return pricingRules;
		}

		@GET
		@Path("sms")
		@Produces(MediaType.APPLICATION_JSON)
		public Collection<RestPricingRule> getSms() {
			final Collection<RestPricingRule> pricingRules = Sets.newHashSet();
			for (SmsPricingRule rule : pricingService.getApplicablePricingRules(SmsPricingRule.class, day)) {
				pricingRules.add(new RestPricingRule(rule));
			}
			return pricingRules;
		}

		@GET
		@Path("data")
		@Produces(MediaType.APPLICATION_JSON)
		public Collection<RestPricingRule> getData() {
			final Collection<RestPricingRule> pricingRules = Sets.newHashSet();
			for (DataPricingRule rule : pricingService.getApplicablePricingRules(DataPricingRule.class, day)) {
				pricingRules.add(new RestPricingRule(rule));
			}
			return pricingRules;
		}
		
	}
	
}
