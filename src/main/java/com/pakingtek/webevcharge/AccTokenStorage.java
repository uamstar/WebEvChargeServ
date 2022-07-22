package com.pakingtek.webevcharge;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.pakingtek.webevcharge.bean.AccToken;

public class AccTokenStorage {
	static final Logger LOG = LoggerFactory.getLogger(AccTokenStorage.class);
	static final Marker ACCESS_MARKER = MarkerFactory.getMarker("ACCESS");
	
	private static AccTokenStorage appInstance = new AccTokenStorage();
	
	private TreeMap<String, AccToken> tokenSet;
	private TreeMap<String, AccToken> refreshSet;
		
	public static AccTokenStorage getAppInstance() {
		return appInstance;
	}
	
	private AccTokenStorage() {
		// �Htoken�ȷ�KEY
		tokenSet = new TreeMap<String, AccToken>(new TokenComparator());
		// �Hrefresh token��KEY
		refreshSet = new TreeMap<String, AccToken>(new TokenComparator());
	}
	
	private class TokenComparator implements Comparator<String>{
		@Override
		public int compare(String s1, String s2) {
			return s1.compareTo(s2);
		}
	}
	
	public synchronized void addToken(AccToken token) {
		refreshSet.put(token.getRefreshToken(), token);
		tokenSet.put(token.getToken(), token);
		LOG.debug(ACCESS_MARKER, "add accToken to accTokenStorage: {}, for user: {}", token.getToken(), token.getUserId());
	}
	
	public long getTokenExpiredTime(String tokenStr) {
		AccToken token = tokenSet.get(tokenStr);
		return (token != null) ? token.getExpiresTime() : 0L;
	}
	
	public long getRefreshLimitTime(String refreshTokenStr) {
		AccToken token = refreshSet.get(refreshTokenStr);
		return (token != null) ? token.getRefreshLimitTime() : 0L;
	}
	
	public synchronized void removeToken(String tokenStr) {
		AccToken token = tokenSet.remove(tokenStr);
		if(token != null) {
			refreshSet.remove(token.getRefreshToken());
		}
	}
	
	public synchronized void removeRefreshToken(String refreshTokenStr) {
		AccToken token = refreshSet.remove(refreshTokenStr);
		if(token != null) {
			tokenSet.remove(token.getToken());
		}
	}
	
	public synchronized void removeTokenByUserId(String userId) {
		AccToken token = null;
		
		Collection<AccToken> set = tokenSet.values();
		Iterator<AccToken> iter = set.iterator();
		while(iter.hasNext()) {
			AccToken t = iter.next();
			if(userId.equals(t.getUserId())) {
				token = t;
				break;
			}
		}
		
		if(token != null) {
			tokenSet.remove(token.getToken());
			refreshSet.remove(token.getRefreshToken());
			LOG.info(ACCESS_MARKER, "fscAccToken for {} has removed.", userId);
		}
	}
	
	/**
	 * �ھ�token�ȡA�Ǧ^���ݪ�userId
	 * @param tokenStr
	 * @return
	 */
	public String getUserIdOfToken(String tokenStr) {
		String result = null;
		AccToken token = tokenSet.get(tokenStr);
		if(token != null) {
			result = token.getUserId();
			LOG.debug(ACCESS_MARKER, "userId of accToken {} is {}", tokenStr, result);
		}else LOG.error(ACCESS_MARKER, "accToken {} not found in accTokenStorage.", tokenStr);
		
		return result;
	}
	
	public synchronized AccToken refreshToken(String refreshTokenStr, 
			String newTokenStr, 
			String newRefreshTokenStr,
			long validDuration,
			long validRefreshDuration) throws AccTokenException
	{
		AccToken token = this.refreshSet.get(refreshTokenStr);
		if(token == null) {
			throw new AccTokenException();
		}else{
			this.removeToken(token.getToken());
			token.setExpiresTime(System.currentTimeMillis() + validDuration);
			token.setRefreshLimitTime(System.currentTimeMillis() + validRefreshDuration);
			token.setToken(newTokenStr);
			token.setRefreshToken(newRefreshTokenStr);
			this.addToken(token);
		}
		return token;
	}
	
	// TODO �C�ѭn����o�ӵ{���@���A�����L�ɪ�token
	public synchronized void cleanInvalidToken() {
		Collection<AccToken> tokens = this.tokenSet.values();
		Iterator<AccToken> iter = tokens.iterator();
		long now = System.currentTimeMillis();
		while(iter.hasNext()) {
			AccToken token = iter.next();
			if(now > token.getExpiresTime()) {
				iter.remove();
				this.refreshSet.remove(token.getRefreshToken());
			}
		}
	}
}
