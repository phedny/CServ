package nl.limesco.cserv.cdr.api;

import com.google.common.base.Optional;

public interface SmsCdr extends Cdr {

	public enum Type {
		EXT_MOBILE, MOBILE_EXT
	}
	
	Optional<Type> getType();
	
}
