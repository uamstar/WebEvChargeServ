package com.pakingtek.webevcharge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	private static final String CHAR_SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	/**
	 * 產生簡訊認證用的四位數數字
	 * @return
	 */
	public String generateVerfCode(){
		return generateRandomNum(4);
	}
	
	/**
	 * 產生六位數字的email確認碼
	 * @return
	 */
	public String generateEmailConfirmCode(){
		return generateRandomNum(6);
	}
	
	/**
	 * 產生認證用的數字碼
	 * @param n 幾位數字
	 * @return
	 */
	private String generateRandomNum(int n){
		char data[] = new char[n];
		int number = 0;
		for(int i = 0; i < n; i++){
			number = ThreadLocalRandom.current().nextInt(10) + 48;	// 48 is ascii code '0'
			data[i] = (char)number;
		}
		
		return new String(data);
	}
	/**
	 * 產生6位數hash編碼。作為db的primary key時，insert前記得先檢查是否有重複
	 * @return
	 */
	public String generateHash6Id(){
		
		return generateHashWithParam(6);
	}
	
	public String generate32Hash(){
		
		return generateHashWithParam(32);
	}
	
	public String generate16Hash(){
		
		return generateHashWithParam(16);
	}
	
	/**
	 * 產生user ID,格式是[大寫英文字母][四位hash碼轉換成的數字]
	 * @return
	 */
	public String generateUserId(){
		int tmp_num = ThreadLocalRandom.current().nextInt(26);
		char head_char = (char)(tmp_num+65);
		String hash_code = generateHashWithParam(4);
		hash_code = hashIdToSerialNumber(hash_code);
		return new String(head_char+hash_code);
	}
	
	/**
	 * 產生並傳回指定位數的亂碼字串
	 * @param n
	 * @return
	 */
	private String generateHashWithParam(int n){
		char data[] = new char[n];
		int number = 0;
		for(int i = 0; i < n; i++){
			number = ThreadLocalRandom.current().nextInt(62);	// generate 0~61	[0-9A-Za-z]共62個字元
			if(number < 10){
				data[i] = (char)(number+48);	// 0~9
			}else if(number < 36){
				data[i] = (char)(number+55);	// A~Z
			}else if(number < 62){
				data[i] = (char)(number+61);	// a~z
			}
		}
		
		return new String(data);
	}
	
	/**
	 * 將帶有hash code的Id碼轉為純數字序號
	 * @param pId
	 * @return
	 */
	public String hashIdToSerialNumber(String pId){
		String result = null;
	    long v = 1;
	    // premiumId only contains [a-zA-Z0-9], 2^6 can represent all the symbols
	    for (int i = 0, l = pId.length(); i < l; i++) {
	        char c = pId.charAt(i);
	        v = v << 6;
	        if (c >=48 && c <=57) {
	            v = v | (c - 48);
	        }else if (c >=65 && c <=90) {
	            v = v | (c - 55);
	        }else if (c >=97 && c <=122) {
	            v = v | (c - 61);
	        }else {
	            v = v | 63;
	        }
	        
//	        NSLog(@"v: %lld, c:%c", v, c);
	    }
	    result = String.valueOf(v);
	    
	    return result;
	}
	
	/**
	 * 將純數字序號轉為帶有hash code的Id碼
	 * @param sn
	 * @return
	 */
	public String serialNumberToHashId(String sn){
		String pId = null;
		char[] r = new char[20];  // character maximum is 20.
	    int length = 0;
	    long v = Long.parseLong(sn);
	    int i = 0;
	    while (v > 1) {
	        i = (int)v & 63;
	        //        NSLog(@"char: %c", charSet[i]);
	        r[length] = CHAR_SET.charAt(i);
	        v = v >> 6;
	        length++;
	    }
	    
	    // reverse the above result
	    char[] result = new char[length];
	    for (int j = 0, k = length - 1; j < length; j++, k--) {
	        result[j] = r[k];
	    }
	    
	    pId = new String(result);
		return pId;
	}
	
	/**
	 * 產生n位數的pwdMask
	 * @param n
	 * @return
	 */
	public String generatePwdMask(int n){
		char data[] = new char[n];
		int number = 0;
		for(int i = 0; i < n; i++){
			number = ThreadLocalRandom.current().nextInt(16);	// 16進位就夠了
			if(number < 10){
				data[i] = (char)(number+48);	// 0~9
			}else if(number < 17){
				data[i] = (char)(number+55);	// A~F
			}
		}
		return new String(data);
	}
	
	/**
	 * 取得密碼。用pwdMask套用在longStr上算出來的
	 * @param longStr
	 * @param pwdMask
	 * @return
	 */
	public String getReloginPwd(String longStr, String pwdMask){
		int tokenLength = longStr.length();
		char [] resultChars = new char[pwdMask.length()];
		int v = 0;
		int p = 0;
		for(int i = 0; i < pwdMask.length(); i++){
			v = pwdMask.charAt(i);
			if (v >=48 && v <=57) v -= 48;
			else if(v >=65 && v <=70) v -= 55;
			p += v;
			while(p >= tokenLength) p -= tokenLength;
			
			resultChars[i] = longStr.charAt(p);
		}
		
		return new String(resultChars);
	}
	
	/**
	 * 讀入檔案內容成為字串
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
	
	public String getFileExtention(String fileName){
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
		    extension = fileName.substring(i+1);
		}
		return extension.toLowerCase();
	}
	
	// save uploaded file to a defined location on the server
	public void saveFile(InputStream uploadedInputStream, String serverLocation) throws IOException {
		int read = 0;
		byte[] bytes = new byte[1024];
		OutputStream outpuStream = new FileOutputStream(new File(serverLocation), false);
		while ((read = uploadedInputStream.read(bytes)) != -1) {
			outpuStream.write(bytes, 0, read);
		}	
		uploadedInputStream.close();
		outpuStream.flush();
		outpuStream.close();			
	}
	
	public void delFile(String filePath){
		try{
			File file = new File(filePath);
			file.delete();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 檢查並轉換成「http://」開頭的合法的url，如果原本就沒值，傳回原本數值
	 * @param str
	 * @return
	 */
	public String strToHttpUrl(String str){
		if(str != null && !str.equals("")){
			Pattern urlP = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
					, Pattern.CASE_INSENSITIVE);
			Matcher m = urlP.matcher(str);
			if(m.matches()){
				return str;
			}else{	// 檢查是否符合email格式
				Pattern emailP = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
				m = emailP.matcher(str);
				if(m.matches()){
					return str;
				}else return "http://"+str;
			}
		}else return str;
	}
	
	/**
	 * 產生對應的bt指令
	 * @param start	起始位元
	 * @param data
	 * @return
	 */
	public String generateBtCmd(short start, short cmd, short[] data) {
		short length = (short)(data.length + 2);
	    // 計算CheckSum
		int dataSum = cmd;
	    for(int i = 0; i < data.length; i++) {
	    	dataSum += data[i];
	    }
	    short checkSum = (short)(dataSum & 0xFF);
	    // 開始組成資料
	    short[] result = new short[length + 3];
	    result[0]=start;
	    result[1]=length;
	    result[2]=cmd;
	    int j = 0;
	    for(; j < data.length; j++){
	        result[j+3] = data[j];
	    }
	    result[j+3] = checkSum;
	    result[j+4] = 0x7D;
	    
	    System.out.println("dataSum: "+dataSum);
	    System.out.println("checkSum: "+checkSum);
	    
	    StringBuffer hex = new StringBuffer();
		for(int i = 0; i < result.length; i++){
			if(result[i] < 0x10) hex.append('0');
			String tmp = Integer.toHexString(result[i]);
			System.out.println("tmp: "+tmp);
			hex.append(tmp);
		}
		
		return hex.toString().toUpperCase();
	}
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	private String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public short[] stringToShortArray(String str) {
		byte[] b = str.getBytes(StandardCharsets.UTF_8);
		System.out.println("byte[] length: "+b.length);
		short[] result = new short[b.length];
		for(int i = 0; i < b.length; i++) {
			result[i] = (short)(b[i] & 0xFF);
		}
		return result;
	}
	
	public static void main(String[] args){
		Util util = new Util();
/*
		System.out.print(util.generateHash6Id());		
		String premiumId = "Ywa4";
		String sn = util.hashIdToSerialNumber(premiumId);
		System.out.println("premiumId: "+premiumId);
		System.out.println("sn: "+sn);	//*/
		
		// System.out.println(util.generateUserId());
		/*
		String str = "www.startup168.com";
		System.out.println(str+" -> "+util.strToHttpUrl(str));
		str = "http://www.startup168.com";
		System.out.println(str+" -> "+util.strToHttpUrl(str));
		str = "https://www.startup168.com";
		System.out.println(str+" -> "+util.strToHttpUrl(str));
		str = "HTtPs://www.startup168.com";
		System.out.println(str+" -> "+util.strToHttpUrl(str));
		str = "www@startup168.com";
		System.out.println(str+" -> "+util.strToHttpUrl(str));
		str = "gopher://www.startup168.com";
		System.out.println(str+" -> "+util.strToHttpUrl(str));	*/
		
		/*
		short[] data = new short[6];
		data[0]=0x01;
		data[1]=0x68;
		data[2]=0x01;
		data[3]=0xAF;
		data[4]=0x03;
		data[5]=0x2F;	//*/
				
		/*
		short[] data = new short[0];//*/
		
		//*
		short[] data = new short[1];
		data[0]=0xFD;	//*/
		/*
		short[] data = new short[2];
		data[0]=0x01;
		data[1]=0x08;//*/
		/*
		short[] data = new short[6];
		data[0]=0xDD;
		data[1]=0x00;
		data[2]=0x00;
		data[3]=0x00;
		data[4]=0x00;
		data[5]=0x00;
		//*/
				
		/*
		short[] ssid = util.stringToShortArray("first WIFI AP");
		short[] order = new short[1];
		order[0] = 0x01;
		short[] data = (short[])ArrayUtils.addAll(order, ssid);	//*/
		
		String output = util.generateBtCmd((short)0x7C, (short)0xAC, data);
		System.out.println(output);
		
		/*
		String target = "2018-11-14 14:20:15";
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    try {
	    	Date result =  df.parse(target);  
	    	System.out.println("result: "+result.getTime());
	    }catch(ParseException pe) {
	    	pe.printStackTrace();
	    }	//*/
		
		/*
		Date date = new Date(1534336379312L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		System.out.println("date: "+sdf.format(date));
		
		Date now = new Date();
		System.out.println("now long: "+now.getTime());
		System.out.println("now: "+sdf.format(now));	*/
	}
}