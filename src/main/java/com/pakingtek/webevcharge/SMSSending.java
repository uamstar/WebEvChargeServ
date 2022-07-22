package com.pakingtek.webevcharge;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import javax.ws.rs.core.UriBuilder;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 *   用來透過簡訊傳送認證碼的class
 * @author laihioh
 *
 */
public class SMSSending {

	static final private String RECEIVER_URL = "https://api.kotsms.com.tw/kotsmsapi-1.php";
	static final private String RESPONSE_URL = "http://server512.webhostingpad.com/~startupc/sms_resp.php";
	static final private String PARAM_USER_NAME = "laihioh";
	static final private String PARAM_PASSWD = "kotsms1234";
	static final private String SMS_MSG_ENCODING = "BIG5";
	static private String PARAM_MSG_FMT = "您的認證碼為:%s";
	static final Logger LOG = LoggerFactory.getLogger(SMSSending.class);
	static final Marker SERVER_MARKER = MarkerFactory.getMarker("SERVER");
		
	public boolean sendMsgToPhone(String phone, String msg){
		boolean result = false;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		
		try{
			// URL and parameters
			
			
			UriBuilder builder = UriBuilder.fromUri(RECEIVER_URL)
					.queryParam("username", PARAM_USER_NAME)
				    .queryParam("password", PARAM_PASSWD)
				    .queryParam("dstaddr", phone)
					.queryParam("smbody", URLEncoder.encode(msg, SMS_MSG_ENCODING))
					.queryParam("response", RESPONSE_URL);
			
			URI uri = builder.build();
			LOG.info(SERVER_MARKER, "uri: {}", uri);
			HttpGet httpGet = new HttpGet(uri);
			LOG.info(SERVER_MARKER, "Start sending SMS msg: {} to phone: {}", msg, phone);
			response = httpclient.execute(httpGet);
			String responseString = EntityUtils.toString(response.getEntity());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 如果回傳是 200 OK 的話才輸出
				
				int returnCode = Integer.parseInt(responseString.substring(7).trim());	// responseString is like "kmsgid=114187448\n"
				
				if(returnCode >= 0){	// 回傳值為簡訊序號，大於0表示成功
					LOG.info(SERVER_MARKER, "SMS send done. serial no: {}", returnCode);
					result = true;
				}else{
					LOG.error(SERVER_MARKER, "fail to send SMS, server return errod code: {}", responseString);
				}
			} else {
				LOG.error(SERVER_MARKER, "fail to send SMS message {} to phone:{} response:{}", msg, phone, response.getStatusLine());
			}
		}catch(UnsupportedEncodingException uee){
			// this would not happen. ignore this exception.
			uee.printStackTrace();
		}catch(ClientProtocolException ce){
			// this would not happen. ignore this exception.
			ce.printStackTrace();
		}catch(IOException ie){
			// this would not happen. ignore this exception.
			ie.printStackTrace();
		}finally {
			try{
				response.close();
			}catch(IOException ioe){
				// ignore the exception
			}
		}
		
		return result;
	}
	
	/**
	 * 發送認證碼給特定電話
	 * @param phone
	 * @param authCode
	 * @return 成功：true; 失敗：false
	 */
	public boolean sendAuthCodeToPhone(String phone, String authCode){
		String msg = String.format(PARAM_MSG_FMT, authCode);
		return sendMsgToPhone(phone, msg);
	}
	
	public static void main(String[] args){
		SMSSending sender = new SMSSending();
		sender.sendMsgToPhone("0958670287", "測試簡訊");
	}
}
