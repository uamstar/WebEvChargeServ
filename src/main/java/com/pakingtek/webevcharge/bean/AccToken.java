package com.pakingtek.webevcharge.bean;

public class AccToken {

	private String userId;
	private String token;
	private String refreshToken;
	private long expiresTime;
	private long refreshLimitTime;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getExpiresTime() {
		return expiresTime;
	}
	public void setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public long getRefreshLimitTime() {
		return refreshLimitTime;
	}
	public void setRefreshLimitTime(long refreshLimitTime) {
		this.refreshLimitTime = refreshLimitTime;
	}
}
