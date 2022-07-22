package com.pakingtek.webevcharge;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

@ApplicationPath("*")
public class ServiceApplication extends ResourceConfig {
	public ServiceApplication(){
		packages("com.pakingtek.webevcharge");
		register(MultiPartFeature.class);
		property("jersey.config.beanValidation.enableOutputValidationErrorEntity.server", "true");
		
		property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
		property(ServerProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
		property(ServerProperties.MOXY_JSON_FEATURE_DISABLE, true);	
		property(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE, true);	
		
		register(CORSResponseFilter.class);
	}	
}