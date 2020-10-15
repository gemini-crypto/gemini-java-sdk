package com.yzcm.common.util;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
public class StringUtil {
	//public static DecimalFormat df=new DecimalFormat("0.000000");
	//public static DecimalFormat cnydf=new DecimalFormat("0.0000");
	//public static DecimalFormat rosedf=new DecimalFormat("0.00");
	
	/**
	 */
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
		}else if(jd==6){
			dc= new DecimalFormat("0.000000");
		}else if(jd==7){
			dc= new DecimalFormat("0.0000000");
		}else{
			dc= new DecimalFormat("0.00000000");
		}
		dc.setRoundingMode(RoundingMode.FLOOR);
		return dc;
	}  
	
	/**
	 * @author Administrator
	 */
	public static String returnRandomNumber(int type){
		if(type==0){
			return String.valueOf(((int)((Math.random()*9+1)*100000)));
		}else{
			return String.valueOf(((int)((Math.random()*9+1)*1000)));
		}
	}
	
	/**
	 */
	public static List<String> getNewList(List<String> li){
        List<String> list = new ArrayList<String>();
        for(int i=0; i<li.size(); i++){
            String str = li.get(i);
            if(!list.contains(str)){
                list.add(str);
            }
        }
        return list;
    }
	
	
	public static boolean isNumeric(String str){
		  for (int i = 0; i < str.length(); i++){
			   if (!Character.isDigit(str.charAt(i))){
				   return false;
			   }
		  }
		  return true;
	}
	
	public static void main(String[] args) {
		
		
	}
}
