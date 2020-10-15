package com.yzcm.common.util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Random;
import java.util.UUID;
public class Utils {
	
	public static String callInterface(String targeturl) {
        BufferedReader in = null;
        StringBuffer result = null;
        try {
            URI uri = new URI(targeturl);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(10*1000);
            connection.connect();
            result = new StringBuffer();
            //读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }
	public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String getUUID(){
		UUID uuid=UUID.randomUUID();
		return uuid.toString();
	}
    public static String toMD5(String inStr){  
        MessageDigest md5 = null; 
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){
            e.printStackTrace();
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));
        }  
        return hexValue.toString();  
    }  
    
	public static String toSHA(String inStr){
		MessageDigest sha = null;  
        try{  
        	sha = MessageDigest.getInstance("SHA-512");  
        }catch (Exception e){  
           System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = sha.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));
        }  
        return hexValue.toString();
	}
	
	public static String multipleEncryption(String pwd){
		return toSHA(toSHA(toMD5(toSHA(toMD5(pwd)))));
	}
	
	public static String getFourBitRandomCode(){
		String str="abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random r=new Random();
        String arr[]=new String [4];
        String b="";
        for(int i=0;i<4;i++)
        {
           int n=r.nextInt(62);
           arr[i]=str.substring(n,n+1);
           b+=arr[i];
        }
        return b;
	}
	
	public static String getRandomFiveNumber(){
		return "1"+String.valueOf((int)((Math.random()*9+1)*1000));
	}
	
	public static String getRandomSixNumber(){
		return "1"+String.valueOf((int)((Math.random()*9+1)*10000));
	}
	
	public static String toAnyConversion(BigInteger Bigtemp, BigInteger base) {
		String ans = "";
		while (Bigtemp.compareTo(BigInteger.ZERO) != 0) {
			BigInteger temp = Bigtemp.mod(base);
			Bigtemp = Bigtemp.divide(base);
			char ch = changToNum(temp);
			ans = ch + ans;
		}
		return ans;
		//this.setToAnyConversion(ans);
	}
	
	static char changToNum(BigInteger temp) {
		int n = temp.intValue();

		if (n >= 10 && n <= 35)
			return (char) (n - 10 + 'A');

		else if (n >= 36 && n <= 61)
			return (char) (n - 36 + 'a');

		else
			return (char) (n + '0');
	}
	
	public static String getBalanceNumber(String number,int count){
		if("0x".equals(number)){
			number = "0";
		}else{
			 number = number.substring(2, number.length());
			 BigInteger big = new BigInteger(number, 16);
			 number = new BigDecimal(String.valueOf(big.doubleValue()/Math.pow(10, count))).toPlainString();
		}
		return number;
	}
	
	
	public static String convertGasTo10(String gasprice){
		 gasprice = gasprice.substring(2, gasprice.length());
		 BigInteger big = new BigInteger(gasprice, 16);
		 gasprice = new BigDecimal(String.valueOf(big.doubleValue())).toPlainString();
		 return gasprice;
	}
	
	public static String convertGasTo16(String gasprice){
		 BigInteger big = new BigInteger(gasprice, 10);
		 gasprice = new BigInteger(String.valueOf(big)).toString(16);
		 return "0x"+gasprice;
	}
	
	public static String convertDoubleGas(String gasprice){
		 String gas=new BigInteger((convertGasTo10(gasprice))).multiply(new BigInteger("2")).toString();
		 return convertGasTo16(gas);
	}
	
	public static String convertNumber(String number,int decimal){
		number = new BigDecimal(String.valueOf(Double.parseDouble(number)/Math.pow(10, decimal))).toPlainString();
		return number;
	}

	public static String getBlockNumber(String number){
		number = number.substring(2, number.length());
		BigInteger big =  new BigInteger(number, 16);
		number = new BigDecimal(String.valueOf(big.doubleValue())).toBigInteger().toString();
		return number;
	}
	
	public static String subZeroAndDot(String s){  
        if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");
            s = s.replaceAll("[.]$", "");
        }  
        return s;  
    }  
	
	public static DecimalFormat getDecimal(int jd){  
		DecimalFormat dc=new DecimalFormat("0");
		if(jd==0){
			dc=new DecimalFormat("0");
		}else if(jd==1){
			dc= new DecimalFormat("0.0");
		}else if(jd==2){
			dc= new DecimalFormat("0.00");
		}else if(jd==3){
			dc= new DecimalFormat("0.000");
		}else if(jd==4){
			dc= new DecimalFormat("0.0000");
		}else if(jd==5){
			dc= new DecimalFormat("0.00000");
		}else{
			dc= new DecimalFormat("0.000000");
		}
		dc.setRoundingMode(RoundingMode.FLOOR);
		return dc;
	}  
	
	public static String getHttpResponse(String allConfigUrl) {
        BufferedReader in = null;
        StringBuffer result = null;
        try {
        	URI uri = new URI(allConfigUrl);
            URL url = uri.toURL();
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36)");
            connection.connect();
            connection.setConnectTimeout(30*1000);
            result = new StringBuffer();
            //读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));        
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }
	
	/**
	 * @return
	 */
	public static String fun1(double x,double fd,int type) {
		double max;
		double min;
		if(type==0){
			max = x;
			min = x*(1-fd);
		}else{
			max = x*(1+fd+0.2);
			min = x;
		}
		double d = Math.random()*(max-min)+min;
        return String.valueOf(d);
    }
	
	public static String fun(double x,double fd,int type) {
		double max;
		double min;
		if(type==0){
			max = x;
			min = x*(1-fd);
		}else{
			max = x*(1+fd);
			min = x;
		}
		double d = Math.random()*(max-min)+min;
        return String.valueOf(d);
    }
	
	public static boolean getProbability(){
		if(Math.random()>0.5){
			return true;
		}
		return false;
	}
	
	public static String formpost(String url, Map<String, String> params) {  
        URL u = null;  
        HttpURLConnection con = null;  
        StringBuffer sb = new StringBuffer();  
        if (params != null) {  
            for (Entry<String, String> e : params.entrySet()) {  
                sb.append(e.getKey());  
                sb.append("=");  
                sb.append(e.getValue());  
                sb.append("&");  
            }  
            sb.substring(0, sb.length() - 1);  
        }  
        try {  
            u = new URL(url);  
            con = (HttpURLConnection) u.openConnection();  
            con.setRequestMethod("POST");  
            con.setDoOutput(true);  
            con.setDoInput(true);  
            con.setUseCaches(false);  
            con.setRequestProperty("ver", "robot");
            con.setRequestProperty("versiontype", "0");
            con.setRequestProperty("os", "ios");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
            con.setConnectTimeout(60*1000);
            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");  
            osw.write(sb.toString());  
            osw.flush();  
            osw.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (con != null) {  
                con.disconnect();  
            }  
        }  
        StringBuffer buffer = new StringBuffer();  
        try {  
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));  
            String temp;  
            while ((temp = br.readLine()) != null) {  
                buffer.append(temp);  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return buffer.toString();  
    } 
	
	
	public static List<Map<String, String>> toMaps(ResultSet rs) throws SQLException {
		List<Map<String, String>> list=new ArrayList<>();
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		Map<String, String> map=null;
		while (rs.next()) {
			map=new HashMap<>();
			for (int i = 1; i <= columnCount; i++) {
				String columnName = metaData.getColumnLabel(i);
				String value = rs.getString(columnName);
				map.put(columnName, value);
			}
			list.add(map);
		}
		return list;
	}
	

	/**
	 */
	public static String toJSON(String msg,String jsonstr,String state){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("message", msg);
		jsonObject.put("data", jsonstr);
		jsonObject.put("state", state);
		return jsonObject.toString();
	}
	
	public static double getHuoBiOTC_CNY(String url){
		double price=0;
		try {
			JSONArray array=JSONObject.fromObject(getHttpResponse(url)).getJSONArray("data");
			if (array.size()>0) {
				price=JSONObject.fromObject(array.get(0)).getDouble("price");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return price;
	}
	
	public static boolean isNumeric(String str){
		boolean isflag=false;
		String reg = "^[0-9]+(.[0-9]+)?$";
		if (str.matches(reg)) {
			if (Double.parseDouble(str)>0) {
				isflag=true;
			}
		}
		return isflag;
	}
	
	public static void main(String[] args) {
		System.out.println(isNumeric("5.0000"));
	}
}
