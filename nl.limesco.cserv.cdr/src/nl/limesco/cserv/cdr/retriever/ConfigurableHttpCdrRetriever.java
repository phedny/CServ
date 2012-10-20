package nl.limesco.cserv.cdr.retriever;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.limesco.cserv.cdr.api.Cdr;
import nl.limesco.cserv.cdr.api.CdrRetriever;
import nl.limesco.cserv.cdr.retriever.steps.Step;

import org.apache.http.client.CookieStore;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.log.LogService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ConfigurableHttpCdrRetriever implements CdrRetriever {
	
	private volatile LogService logService;
	
	private List<Step> steps = Collections.emptyList();

	void configure(Dictionary properties) throws ConfigurationException {
		final List<Step> newSteps = Lists.newLinkedList();
		for (int i = 0; ; i++) {
			final Step step = Step.newInstance(properties, i);
			if (step != null) {
				newSteps.add(step);
			} else {
				break;
			}
		}
		
		steps = newSteps;
	}

	@Override
	public Iterable<Cdr> retrieveCdrsForDay(Calendar day) throws IOException {
		final Map<String, Object> variables = Maps.newHashMap();
		try {
			day.set(Calendar.HOUR_OF_DAY, 0);
			day.set(Calendar.MINUTE, 0);
			day.set(Calendar.SECOND, 0);
			day.set(Calendar.MILLISECOND, 0);
			variables.put(":DATE", day);
			
			final CookieStore cookieStore = new BasicCookieStore();
			for (Step step : steps) {
				final DefaultHttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
				client.setCookieStore(cookieStore);
				if (!step.execute(client, variables)) {
					final Set<Cdr> emptySet = Collections.emptySet();
					return emptySet;
				}
			}
			
			final Iterator<Cdr> iterator = (Iterator<Cdr>) variables.get(":CDR");
			return new Iterable<Cdr>() {
				
				private volatile boolean unused = true;

				@Override
				public Iterator<Cdr> iterator() {
					checkArgument(unused);
					unused = false;
					return iterator;
				}
				
			};
		} finally {
			for (Object obj : variables.values()) {
				if (obj instanceof InputStream) {
					try {
						((InputStream) obj).close();
					} catch (IOException e) {
						logService.log(LogService.LOG_WARNING, "Failed to close stream");
					}
				}
			}
		}
	}
	
}
