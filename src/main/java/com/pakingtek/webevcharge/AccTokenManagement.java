package com.pakingtek.webevcharge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.pakingtek.webevcharge.bean.AccToken;
import com.pakingtek.webevcharge.bean.AccTokenStatus;
import com.pakingtek.webevcharge.bean.DataModel;

/**
 * �@��accToken���޲z�P�ϥΤ���
 */
public class AccTokenManagement {

	static final Logger LOG = LoggerFactory.getLogger(AccTokenManagement.class);
	static final Marker USER_MARKER = MarkerFactory.getMarker("USER");
	static final Marker SERVER_MARKER = MarkerFactory.getMarker("SERVER");
	static final long VALID_DURATION = 30 * 60 * 1000;	// FSC token�����Įɶ� 30 ����
	static final long VALID_REFRESH_DURATION = 86400 * 1000;	// FSC refresh token�����Įɶ� �@��

	static final String REFRESH_RESULT_FMT = "{\"result\":%d,\"msg\":\"%s\"}";
		
	private AccTokenStorage tokenStore;
	private DataModel dataModel;
	private Util util = new Util();
	private AccountManager accMgr;
	
	/**
	 * �ݫ��w�n�޲z��accToken�Oapp���άOdocking���A��ت�accToken�x�s���e�����@��
	 * @param usageType
	 */
	public AccTokenManagement() {
		
		tokenStore = AccTokenStorage.getAppInstance();
		
		this.accMgr = new AccountManager();
	}
	
	/**
	 * �ϥάJ����DataModel����C�]��֫إ�DataModle���󪺦��ơ^
	 * �ݫ��w�n�޲z��accToken�Oapp���άOdocking���A��ت�accToken�x�s���e�����@��
	 * @param dm
	 * @param usageType
	 */
	public AccTokenManagement(DataModel dm) {
		this();
		dataModel = dm;
		this.accMgr = new AccountManager(dm);
	}
	
	/**
	 * ���oaccToken�����A
	 * @param fscToken
	 * @return	�T�إi�઺���A�G���s�b�A�L���A�L��
	 */
	public AccTokenStatus getAccTokenStatus(String fscToken) {
		long expiredTime = tokenStore.getTokenExpiredTime(fscToken);
		if(expiredTime == 0) return AccTokenStatus.NOT_EXIST;
		else if(expiredTime < System.currentTimeMillis()) {
			return AccTokenStatus.EXPIRED;
		}else return AccTokenStatus.VALID;
	}
	
	/**
	 * ���orefresh token�ҥN��token�����A
	 * @param refreshToken
	 * @return	�T�إi�઺���A�G���s�b�A�L���A�L��
	 */
	public AccTokenStatus getRefreshTokenStatus(String refreshToken) {
		long refreshLimitTime = tokenStore.getRefreshLimitTime(refreshToken);
		if(refreshLimitTime == 0) return AccTokenStatus.NOT_EXIST;
		else if(refreshLimitTime < System.currentTimeMillis()) {	// TODO �令�w�ɲM�z���覡
			// ���Įɶ��W�L�{�b�ɶ��A�R��token
			tokenStore.removeRefreshToken(refreshToken);
			return AccTokenStatus.EXPIRED;
		}else{
			return AccTokenStatus.VALID;
		}
	}
			
	/**
	 * �]�w���ϥΪ�DataModle�ާ@����
	 * @param dm
	 */
	public void setDataModel(DataModel dm) {
		this.dataModel = dm;
	}
	
	/**
	 * ����fscToken�C�ھګ��w�������A�|�k�����x�s��app��docking�������e��
	 * @param userId	�����Ѽƫ��w��user�ϥ�
	 * @param macAddr	�����Ѽƫ��w�����~�ϥΡCapp�����ɡA�o�ӰѼƬO�Ū�
	 * @return
	 */
	public AccToken generateAccToken(String userId, String macAddr) {
		// ����fscAccToken
		String result = util.generate16Hash();
		String refreshToken = util.generate16Hash();
		// �s�J�t�Φ@�Ϊ�AccTokenStorage��
		AccToken token = new AccToken();
		token.setUserId(userId);
		token.setExpiresTime(System.currentTimeMillis() + VALID_DURATION);	// ���Įɶ�30������
		token.setRefreshLimitTime(System.currentTimeMillis() + VALID_REFRESH_DURATION);	// ���Įɶ��@�ѫ�
		token.setToken(result);
		token.setRefreshToken(refreshToken);
		
		tokenStore = AccTokenStorage.getAppInstance();
		tokenStore.addToken(token);
		
		return token;
	}
	
	/**
	 * ��sFscAccToken�C�Y�O�o��AccTokenException�A��ܫ��w��refreshToken���s�b
	 * @param refreshTokenStr
	 * @return �s��FS access token�C�Ĥ@�Ӥ����O�s��token�A�ĤG�Ӥ����O�s��refresh token.
	 * @throws AccTokenException
	 */
	public String[] refreshAccToken(String refreshTokenStr) throws AccTokenException{
		
		tokenStore = AccTokenStorage.getAppInstance();
		
		// ����fscAccToken
		String newToken = util.generate16Hash();
		String newRefreshToken = util.generate16Hash();
		AccToken tokenStale = tokenStore.refreshToken(refreshTokenStr,
				newToken,
				newRefreshToken,
				VALID_DURATION,
				VALID_REFRESH_DURATION);
		
		String[] newTokenPair = {newToken, newRefreshToken};
		
		return (tokenStale == null)? null: newTokenPair;
	}
	
		
	// TODO �C�j�@�q�ɶ��A�I�s��function�M�z�L�Ī�token�A����O����
	public void cleanInvalidFscAccTokens() {
		
	}
	
	/**
	 * �M���Y���~����fscAccToken
	 * @param macAddr
	 */
	public static void cleanFscAccTokenForMacAddr(String macAddr) {
		AccTokenStorage appTokenStore = AccTokenStorage.getAppInstance();
		
		LOG.info(USER_MARKER, "try to remove a fscAccToken of app...");
		//appTokenStore.removeTokenByMacAddr(macAddr);
	}
	
	/**
	 * �M���Y�ϥΪ̪�fscAccToken
	 * @param userId
	 */
	public static void cleanFscAccTokenForUserId(String userId) {
		AccTokenStorage appTokenStore = AccTokenStorage.getAppInstance();
		
		LOG.info(USER_MARKER, "try to remove a fscAccToken of app...");
		appTokenStore.removeTokenByUserId(userId);
	}
	
}