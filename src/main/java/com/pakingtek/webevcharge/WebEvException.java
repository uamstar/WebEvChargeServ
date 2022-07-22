package com.pakingtek.webevcharge;

public class WebEvException extends Exception {

	private Msg msg;
	private Object data;
	
	public WebEvException(String message) {
		super(message);
	}
	
	public WebEvException(Msg msg) {
		super(msg.getDesc());
		this.msg = msg;
	}
	
	public Msg getMsg() {
		return msg;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object d) {
		this.data = d;
	}
}
