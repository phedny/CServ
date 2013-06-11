package nl.limesco.cserv.invoice.mongo;

import net.vz.mongodb.jackson.Id;
import nl.limesco.cserv.invoice.api.QueuedItemLine;

import org.codehaus.jackson.annotate.JsonIgnore;

public class QueuedItemLineImpl extends NormalItemLineImpl implements QueuedItemLine {

	private String id;
	private String queuedForAccountId;
	
	@Id
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	@JsonIgnore
	public String queuedForAccountId() {
		return getQueuedForAccountId();
	}
	
	public String getQueuedForAccountId() {
		return queuedForAccountId;
	}
	
	public void setQueuedForAccountId(String queuedForAccountId) {
		this.queuedForAccountId = queuedForAccountId;
	}
	
}
