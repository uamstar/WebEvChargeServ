package com.pakingtek.webevcharge.bean;

import java.util.ArrayList;
import java.util.List;

public class CloudResponse {
	
	private int result;
	private String msg = "";
	private List<String> values = new ArrayList<String>();
	
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public void addValue(String value) {
		this.values.add(value);
	}
	
	public String toJsonString() {
		String values_str = "";
		if(values.size() > 0) 
		{
			StringBuffer strbuf = new StringBuffer();
			for(int i = 0; i < values.size() - 1; i++) {
				strbuf.append('\"').append(values.get(i)).append("\",");
			}
			strbuf.append('\"').append(values.get(values.size() - 1)).append("\"");
			values_str = strbuf.toString();
		}
		return "{\"result\":\"" + result + "\",\"msg\":\"" + msg + "\",\"values\":[" + values_str + "]}";
	}	
}
