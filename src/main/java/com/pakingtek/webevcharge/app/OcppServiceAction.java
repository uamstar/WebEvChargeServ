package com.pakingtek.webevcharge.app;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.pakingtek.webevcharge.OcppService;
import com.pakingtek.webevcharge.bean.CloudResponse;
import com.pakingtek.webevcharge.bean.OcppState;

@Path("apt/ocpp")
public class OcppServiceAction 
{
	@Path("/checkEvseState")
	@GET
	@Consumes("text/plain")
	@Produces("text/plain; charset=UTF-8")
	public String checkEvseState(@QueryParam("deviceId") String deviceId) 
	{
		CloudResponse resp = new CloudResponse();
		resp.setResult(1);
		
		if(deviceId != null && !"".equals(deviceId)) {
			OcppService ocppsrv = OcppService.INSTANCE;
			OcppState state = ocppsrv.getState(deviceId);
			// TODO 取得 該充電樁 的基本資料
			resp.addValue("台北內湖特力屋2號");
			
			if(state == OcppState.NONE) 
			{
				resp.setMsg("NONE");
				resp.addValue("未登錄此充電樁");
			}
			else if(state == OcppState.READY) 
			{
				resp.setMsg("READY");
				resp.addValue("充電費率 $4/kWh");
			}
			else if(state == OcppState.IN_USE)
			{
				resp.setMsg("IN_USE");
				resp.addValue("預計 2 小時 15 分後完成");
			}				
			else if(state == OcppState.MAINTAINING)
			{
				resp.setMsg("MAINTAINING");
				resp.addValue("維修中，預計 3 天又 9 小時後可用");
			}
			else if(state == OcppState.SYS_ERR)
			{
				resp.setMsg("SYS_ERR");
				resp.addValue("系統錯誤，無法得知充電樁狀況");
			}
			
			resp.setResult(0);
		}
		
		return resp.toJsonString();
	}
}
