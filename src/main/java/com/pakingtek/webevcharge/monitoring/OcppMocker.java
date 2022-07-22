package com.pakingtek.webevcharge.monitoring;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.pakingtek.webevcharge.OcppService;
import com.pakingtek.webevcharge.bean.OcppState;

@Path("monitoring/ocppmock")
public class OcppMocker 
{
	
	@Path("/setEvseState")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain; charset=UTF-8")
	public String setEvseState(
			@FormParam("deviceId") String deviceId,
			@FormParam("state") String state) 
	{
		String result = "0";
		if(deviceId != null && !"".equals(deviceId)) 
		{
			OcppService ocppsrv = OcppService.INSTANCE;
			if(state.equals("NONE"))
				ocppsrv.setState(deviceId, OcppState.NONE);
			else if(state.equals("READY"))
				ocppsrv.setState(deviceId, OcppState.READY);
			else if(state.equals("IN_USE"))
				ocppsrv.setState(deviceId, OcppState.IN_USE);
			else if(state.equals("MAINTAINING"))
				ocppsrv.setState(deviceId, OcppState.MAINTAINING);
			else if(state.equals("SYS_ERR"))
				ocppsrv.setState(deviceId, OcppState.SYS_ERR);
		}
		
		return result;
	}
}
