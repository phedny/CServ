package nl.limesco.cserv.cdr.api;

import com.google.common.base.Optional;

public interface VoiceCdr extends Cdr {

	public enum Type {
		EXT_EXT     ,  PBX_EXT     ,  MOBILE_EXT     ,
		EXT_PBX     ,  PBX_PBX     ,  MOBILE_PBX     ,
		EXT_MOBILE  ,  PBX_MOBILE  ,  MOBILE_MOBILE  ,
	}

	boolean isConnected();

	long getSeconds();

	Optional<Type> getType();
	
	String getDestination();
	
}
