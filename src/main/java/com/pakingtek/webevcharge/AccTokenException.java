package com.pakingtek.webevcharge;

public class AccTokenException extends Exception {
	
	private String msg;
	
	public void setMessage(String msg){
		this.msg = msg;
	}
	
	@Override
	public String getMessage() {
		return msg;
	}
}