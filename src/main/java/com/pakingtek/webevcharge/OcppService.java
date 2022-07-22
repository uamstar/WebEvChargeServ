package com.pakingtek.webevcharge;

import java.util.HashMap;

import com.pakingtek.webevcharge.bean.OcppState;

public class OcppService {
	
	public static OcppService INSTANCE = new OcppService();
		
	private HashMap<String, OcppState> tempStore = new HashMap<String, OcppState>();
	
	private OcppService() 
	{
		
		
		
	}

	public OcppState getState(String deviceId) {
		
		OcppState state = tempStore.get(deviceId);
		if(state == null) state = OcppState.NONE;
		return state;
	}

	public void setState(String deviceId, OcppState state) {
		
		tempStore.put(deviceId, state);
	}

	
}
