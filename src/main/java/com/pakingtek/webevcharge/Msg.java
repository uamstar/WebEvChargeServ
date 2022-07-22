package com.pakingtek.webevcharge;

public enum Msg {
	SUCCEED("SUCCEED", 0),

	ERR9999("System Error. Try again later or contact service provider.", 9999);
	
	private String desc;
	private int code;
	private  static String HEAD = "ERR.";
	
	private Msg(String desc, int code) {
		this.desc = desc;
		this.code = code;
	}
	
	public String getName() {
		return HEAD+this.code;
	}
	
	public String getDesc() {
		return this.desc;
	}
}
