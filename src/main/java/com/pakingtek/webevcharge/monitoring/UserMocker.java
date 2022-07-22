package com.pakingtek.webevcharge.monitoring;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pakingtek.webevcharge.AccountManager;
import com.pakingtek.webevcharge.bean.User;

@Path("monitoring/usermock")
public class UserMocker {

	private AccountManager accMgr = new AccountManager();
	
	@Path("/getAllUsers")
	@GET
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json; charset=UTF-8")
	public String getAllUsers() 
	{
		String users_json = "";
		
		List<User> users = accMgr.getAllUsers();
		
		ObjectMapper mapper = new ObjectMapper();
		try 
		{
			users_json = mapper.writeValueAsString(users);
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}		
		
		return users_json;
	}
}
