package com.lpoezy.nexpa.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class StringFormattingUtils {

	public static String setBroadcastChatEquivalent(String msg, String localArea){
		String newChat = "";
		newChat = "<#t_cn_local_ar_nme>"+ localArea +"</#t_cn_local_ar_nme><#user_msg_dsk>"+ msg +"</#user_msg_dsk>";
		return newChat;
	}
	public static String getBroadcastChatEquivalentMes(String foo){
		String result = "";
		//String foo = "<#t_cn_local_ar_nme>"+ "Bot" +"</#t_cn_local_ar_nme><#user_msg_dsk>"+ "Hi" +"</#user_msg_dsk>";
		 Pattern pattern = Pattern.compile("<#t_cn_local_ar_nme>(.*?)</#t_cn_local_ar_nme>");
		    Matcher matcher = pattern.matcher(foo);
		    while (matcher.find()) {
		    	result = (matcher.group(1));
		    }
		
		return result;
	}
	public static String getBroadcastChatEquivalentLocation(String foo){
		String result = "";
		//String foo = "<#t_cn_local_ar_nme>"+ "Bot" +"</#t_cn_local_ar_nme><#user_msg_dsk>"+ "Hi" +"</#user_msg_dsk>";
		 Pattern pattern = Pattern.compile("<#user_msg_dsk>(.*?)</#user_msg_dsk>");
		    Matcher matcher = pattern.matcher(foo);
		    while (matcher.find()) {
		    	result = (matcher.group(1));
		    }
		
		return result;
	}
}
