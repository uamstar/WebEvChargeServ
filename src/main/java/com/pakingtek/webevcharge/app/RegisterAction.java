package com.pakingtek.webevcharge.app;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.pakingtek.webevcharge.AccountManager;
import com.pakingtek.webevcharge.SMSSending;
import com.pakingtek.webevcharge.Util;
import com.pakingtek.webevcharge.bean.CloudResponse;
import com.pakingtek.webevcharge.bean.User;
import com.pakingtek.webevcharge.bean.UserStatus;

@Path("reg")
public class RegisterAction {
	static final Logger LOG = LoggerFactory.getLogger(RegisterAction.class);
	static final Marker SERVER_MARKER = MarkerFactory.getMarker("SERVER");
	static final Marker REGISTER_MARKER = MarkerFactory.getMarker("Register");
	static Util UTIL = new Util();
	static SMSSending SMS_SENDER = new SMSSending();
	private AccountManager accMgr = new AccountManager();
	
	@Path("/getVerfCode")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json; charset=UTF-8")
    public String retrieveVerfCode(@FormParam("phoneNum") String phoneNum) 
	{
		String result = null;
		
		User user = accMgr.getUserByPhoneNum(phoneNum);
		if(user == null)	// 使用者不存在，發出 verfCode 簡訊，建立使用者物件 
		{
			String verfCode = UTIL.generateVerfCode();
			try {
				user = accMgr.createUser(phoneNum, verfCode);
				/*
				if(Config.SMS_ENABLE){
					if(SMS_SENDER.sendAuthCodeToPhone(user.getPhoneNum(), verfCode))
					{ // 透過簡訊傳送認證碼
						
					}		
				}*/
				result = "{\"result\": \"0\", \"msg\":\"認證碼已發送\"}";
			}
			catch(Exception e) 
			{
				result = "{\"result\": \"1\", \"msg\":\"" + e.getMessage() + "\"}";		// TODO: 改為系統統一錯誤訊息格式
			}
		}
		else	// 使用者已存在
		{
			if(user.getStatus() == UserStatus.PHONE_NUM_CONFIRMED) 
			{	// 	使用者已通過認證
				result = "{\"result\": \"2\", \"msg\":\"帳號已存在\"}";
			}
			else // 使用者尚未通過認證
			{
				long now = new Date().getTime();
				if((now - user.getLastUpdateDate().getTime()) < 120000)		// 取得驗證碼120秒倒數後，才能再點擊
				{	
					result = "{\"result\": \"3\", \"msg\":\"認證碼已發送，請勿重複點擊\"}";
				}
				else
				{
					String newVerfCode = UTIL.generateVerfCode();
					try {
						accMgr.updateVerCodeOfUser(user.getUserId(), newVerfCode);
						/*
						if(Config.SMS_ENABLE){
							if(SMS_SENDER.sendAuthCodeToPhone(user.getPhoneNum(), verfCode))
							{ // 透過簡訊傳送認證碼
								
							}		
						}*/
						result = "{\"result\": \"0\", \"msg\":\"認證碼已發送\"}";
					}
					catch(Exception e) 
					{
						result = "{\"result\": \"1\", \"msg\":\"" + e.getMessage() + "\"}";		// TODO: 改為系統統一錯誤訊息格式
					}
				}
			}
		}
		return result;
	}
	
	@Path("/register")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("application/json; charset=UTF-8")
    public String doRegistering(
    		@FormParam("phoneNum") String phoneNum,
    		@FormParam("verfCode") String verfCode,
    		@FormParam("passwd") String passwd,
    		@FormParam("userName") String userName,
    		@FormParam("email") String email
    		) {
		
		String result = null;
		try{
			// 檢查手機號碼是否已註冊且為有效
			User user_cache = accMgr.getUserByPhoneNum(phoneNum);
			if(user_cache == null) {	// 使用者尚未點擊"取得認證碼"
				result = "{\"result\": \"4\", \"msg\":\"未點擊「取得認證碼」\"}";
			}
			else
			{
				if(UserStatus.NEW == user_cache.getStatus()) 
				{
					if(user_cache.getVerfCode().equals(verfCode)) 
					{	// 認證碼相同，通過驗證，建立帳號
						User user_p = accMgr.updateUser(user_cache.getUserId(), userName, email, passwd, UserStatus.PHONE_NUM_CONFIRMED);
						CloudResponse resp = new CloudResponse();
						resp.setResult(0);
						resp.setMsg("註冊完成");
						resp.addValue(user_p.getUserId());
						//resp.addValue(accToken);			// 配發 access_token 視同已登入
						
						result = resp.toJsonString();	
					}
					else
					{
						accMgr.updateUserStatus(user_cache.getUserId(), UserStatus.PHONE_NUM_CONFIRMED_FAIL);
						result = "{\"result\": \"5\", \"msg\":\"認證碼不符\"}";
						// TODO: 防止重複試驗
					}
				}
				else if(UserStatus.PHONE_NUM_CONFIRMED == user_cache.getStatus())
				{	// 使用者已通過驗證，照理說不該走到這步驟，因為前面按下「取得認證碼」，就會引導他去登入畫面
					result = "{\"result\": \"6\", \"msg\":\"已通過驗證，請重新登入\"}";
				}
				else if(UserStatus.PHONE_NUM_CONFIRMED_FAIL == user_cache.getStatus()) 
				{
					if(user_cache.getVerfCode().equals(verfCode)) 
					{	// 認證碼相同，通過驗證，建立帳號
						User user_p = accMgr.updateUser(user_cache.getUserId(), userName, email, passwd, UserStatus.PHONE_NUM_CONFIRMED);
						CloudResponse resp = new CloudResponse();
						resp.setResult(0);
						resp.setMsg("註冊完成");
						resp.addValue(user_p.getUserId());
						//resp.addValue(accToken);			// 配發 access_token 視同已登入
						
						result = resp.toJsonString();	
					}
					else 
					{	// 輸入的驗證碼已經錯了至少一次，這次又再錯
						result = "{\"result\": \"7\", \"msg\":\"認證碼不符\"}";
						// TODO: 紀錄使用者重複嘗試的錯誤次數 
					}
				}
			}
		}catch(Exception se){
			// database error, log it.
			// TODO log error.
			se.printStackTrace();
			LOG.error(SERVER_MARKER, "create user fail, phone: {}, Exception Msg: {}",phoneNum, se.getMessage());
			result = "{\"result\":\"-99\", \"msg\":\"系統忙碌中，請稍後再試\"}";
		}
		return result;
	}
}
