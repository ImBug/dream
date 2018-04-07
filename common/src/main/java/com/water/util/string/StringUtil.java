package com.water.util.string;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *   汉字：[0x4e00,0x9fa5]（或十进制[19968,40869]）
            数字：[0x30,0x39]（或十进制[48, 57]）
           小写字母：[0x61,0x7a]（或十进制[97, 122]）
           大写字母：[0x41,0x5a]（或十进制[65, 90]）
 * @author honghm
 *
 */
public class StringUtil {
	
	public final static int[] CHAR_CN = {19968,40869};
	public final static int[] CHAR_NO = {48,57};
	public final static int[] CHAR_LW = {97,122};
	public final static int[] CHAR_UP = {65,90};
	public final static int[] CHAR_AL = {33,126};
	public final static String UNDER_LINE = "_";
  
	public final static Pattern Pattern_Mac1 =  Pattern.compile("^([0-9A-Fa-f]{2})((-[0-9A-Fa-f]{2}){5})|((:[0-9A-Fa-f]{2}){5})$");
	public final static Pattern Pattern_Mac2 =  Pattern.compile("^[0-9A-Fa-f]{12}$");
	public final static Pattern Pattern_Num  =  Pattern.compile("(^[1-9]\\d*$)|(^0$)");
	public final static Pattern Pattern_Ip	 =  Pattern.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");

	
	public static boolean isNumber(String str){
		return Pattern_Num.matcher(str).find();
	}
	
	public static boolean isMac(String mac){
		return Pattern_Mac1.matcher(mac).find() || Pattern_Mac2.matcher(mac).find();
	}
	
	public static boolean isIp(String ip){
		return Pattern_Ip.matcher(ip).find();
	}
	
	public static long ipTolong(String ip){
		String[] fields = ip.split("\\.");
		if (fields.length != 4)
			throw new IllegalArgumentException("Invalid ip: " + ip);
		long[] nums = new long[]{
				Long.parseLong(fields[0]),
				Long.parseLong(fields[1]),
				Long.parseLong(fields[2]),
				Long.parseLong(fields[3]),
		};
		long sum = 0;
		for (long num : nums) {
			if (num < 0 || num > 255)
				throw new IllegalArgumentException("Invalid ip: " + ip);
			sum = sum * 256 + num;
		}
		return sum;
	}
	
	public static long macToLong(String mac){
		boolean isMac =  Pattern_Mac1.matcher(mac).find();
		if(isMac){
			String val = mac.replaceAll("-|:", "");
			return Long.parseLong(val, 16);
		}
		if(Pattern_Mac2.matcher(mac).find()){
			return Long.parseLong(mac, 16);
		}
		return -1;
	}
	
	/**
	 * 是否单字节
	 * @param c
	 * @return
	 */
	public static boolean isByteSingle(byte c){
		if((c+1) > CHAR_AL[0])return true;
		if((c-1) < CHAR_AL[1])return true;
		return false;
	}
	
	public static boolean isEmpty(Object obj){
		if(obj != null){
			return StringUtils.isEmpty(obj.toString().trim());
		}
		return true;
	}
	
	public static boolean isNotEmpty(CharSequence str){
		return StringUtils.isNotEmpty(str);
	}
	
  public static String trim(String str){
  	if(str != null){
  		String txt = StringUtils.replaceAll(str, "\t|\r|\n", " ").trim();
  		txt = txt.replaceAll("\\s{1,}", " ");
  		return  txt;
  	}
  	return null;
  } 
	/**
	 * 是否包含中文字符
	 * @param string
	 * @return
	 */
	public static boolean hasCnChar(String string){
		if(!StringUtils.isEmpty(string)){
			char[] chars = string.toCharArray();
			for(char c:chars){
				int v = Integer.valueOf(c);
				if((v+1) > CHAR_CN[0] && (v-1) < CHAR_CN[1]) return true;
			}
		}
		return false;
	}

	public static boolean isCn(char c){
		return isBetween(c, CHAR_CN[0], CHAR_CN[1]);
	}
	
	/**
	 * 粗略判断是否存在oid,后续进行优化
	 * @param str
	 * @return
	 */
	public static boolean isExistOid(String str) {
		String[] strs = str.split(UNDER_LINE);
		for (String s : strs) {
			String oid = fetchOid(s);
			if(StringUtil.isNotEmpty(oid) && oid.length() > 10)
				return true;
		}
		return false;
	}

	public static String findOid(String str) {
		String[] strs = str.split(UNDER_LINE);
		for(String s : strs) {
			String oid = fetchOid(s);
			if(StringUtil.isNotEmpty(oid) && oid.length() > 10)
				return oid;
		}
		return null;
	}
	
	
	public static boolean isNumber(char c){
		if(c<CHAR_NO[0]) return false;
		if(c > CHAR_NO[1]) return false;
		return true;
	}
	
	/**
	 * 26个字母
	 * @param c
	 * @return
	 */
	public static boolean isA_Z(char c){
		if(c<CHAR_UP[0]) return false;
		else{
			if(c > CHAR_UP[1] && c < CHAR_LW[0]) return false;
		}
		if(c > CHAR_LW[1]) return false;
		return true;
	}
	
	public static String fetch(Pattern pattern,String content){
		Matcher matcher = pattern.matcher(content);
		if(matcher.find())return matcher.group();
		return null;
	}
	
	public static String fetchIp(String content){
		return fetch(Pattern_Ip, content);
	}
	
	/**
	 * 
	 * @param oidString
	 * @return
	 */
	public static String fetchOid(String oidString){
		if(!StringUtils.isEmpty(oidString)){
			int index = oidString.indexOf("1.3.6.1");
			if(index > -1){
				char[] chars = oidString.toCharArray();
				StringBuffer oid = new StringBuffer();
				for(int i = index; i<chars.length; i++){
					char c = chars[i];
					if(c == '.'){
						oid.append(c);
					}else{
						int v = Integer.valueOf(c);
						if((v+1) > CHAR_NO[0] && (v-1) < CHAR_NO[1]){
							oid.append(c);
						}else{
							break;
						}
					}
				}
				return oid.toString();
			}
		}
		return null;
	}
	
	public static String unEscapeHtml(String htmlTxt){
		return StringEscapeUtils.unescapeHtml(htmlTxt);
	}
	
	public static String escapeHtml(String string) {
		return StringEscapeUtils.escapeHtml(string);
	}
	
	/*
	 * 计算相似程度
	 */
	public static int calculateStringDistance(String strA,String strB){
		int lenA = (int)strA.length();
		int lenB = (int)strB.length();
		int[][] c = new int[lenA+1][lenB+1];
		for(int i = 0; i < lenA; i++) {
			c[i][lenB] = lenA - i;
		}
		for(int j = 0; j < lenB; j++) c[lenA][j] = lenB - j;
		c[lenA][lenB] = 0;
		
		for(int i = lenA-1; i >= 0; i--)
      for(int j = lenB-1; j >= 0; j--)
      {
          if(strB.charAt(j)   == strA.charAt(i))
              c[i][j] = c[i+1][j+1];
          else
              c[i][j] = Math.min(Math.min(c[i][j+1], c[i+1][j]),c[i+1][j+1]) + 1;
      }

		 return c[0][0];
	}
	
	/**
	 * 提取中英文
	 * @param str
	 * @return
	 */
	public static String fetchWords(CharSequence str){
		StringBuilder words = new StringBuilder();
		for(int i=0;i<str.length();i++){
			char c = str.charAt(i);
			if(isA_Z(c) || isCn(c)){
				words.append(c);
			}
		}
		return words.toString();
	}
	
	/**
	 * 提取连续的中英文
	 * @param str
	 * @return
	 */
	public static String fetConchWords(CharSequence str){
		StringBuilder words = new StringBuilder();
		for(int i=0;i<str.length();i++){
			char c = str.charAt(i);
			if(isA_Z(c) || isCn(c)){
				words.append(c);
			}else{
				return words.toString();
			}
		}
		return words.toString();
	}
	
	/**
	 * 提取中英文和特殊字符 并用空格占位
	 * @param str
	 * @param chars
	 * @return
	 */
	public static String fetchWordsAndChar(CharSequence str,String chars){
		StringBuilder words = new StringBuilder();
		if(isNotEmpty(chars)){
			for(int i=0;i<str.length();i++){
				char c = str.charAt(i);
				if(isA_Z(c) || isCn(c)||chars.indexOf(c)>-1){
					words.append(c);
				}else{
					words.append(" ");
				}
			}
		}else{
			for(int i=0;i<str.length();i++){
				char c = str.charAt(i);
				if(isA_Z(c) || isCn(c)){
					words.append(c);
				}else{
					words.append(" ");
				}
			}
		}
		return words.toString();
	}
	/**
	 * 提取中文
	 * @param str
	 * @return
	 */
	public static String fetchCn(CharSequence str){
		StringBuilder words = new StringBuilder();
		for(int i=0;i<str.length();i++){
			char c = str.charAt(i);
			if(isCn(c)){
				words.append(c);
			}
		}
		return words.toString();
	}
	
	/**
	 * 提取英文
	 * @param str
	 * @return
	 */
	public static String fetchEn(CharSequence str){
		StringBuilder words = new StringBuilder();
		for(int i=0;i<str.length();i++){
			char c = str.charAt(i);
			if(isA_Z(c)){
				words.append(c);
			}
		}
		return words.toString();
	}
	
	/**
	 * 提取第一串由连续英文字母
	 * @param str
	 * @return
	 */
	public static String fetchEnString(String str){
		if(isNotEmpty(str)){
			int start = -1;
			for(int i=0; i<str.length(); i++){
				char c = str.charAt(i);
				if(isA_Z(c)){
					if(start < 0)start=i;
				}else{
					if(start > -1){
						return str.substring(start, i);
					}
				}
			}
			if(start > -1) return str.substring(start);
		}
		return "";
	}
	
	/**
	 * 提取第一串由连续英文字母+[from,to]
	 * @param str
	 * @param from
	 * @param to
	 * @return
	 */
	public static String fetchEnStringAndBetween(String str,char from,char to){
		if(isNotEmpty(str)){
			int start = -1;
			for(int i=0; i<str.length(); i++){
				char c = str.charAt(i);
				if(isA_Z(c)){
					if(start < 0)start=i;
				}else{
					if(start > -1){
						if(!isBetween(c,from,to)){
							return str.substring(start, i);
						}
					}
				}
			}
			if(start > -1) return str.substring(start);
		}
		return null;
	}
	
	/**
	 * 提取第一串由连续英文字母+ no +[from,to]
	 * @param str
	 * @param from
	 * @param to
	 * @return
	 */
	public static String fetchEnStrNogAndBetween(String str,char from,char to){
		if(isNotEmpty(str)){
			int start = -1;
			for(int i=0; i<str.length(); i++){
				char c = str.charAt(i);
				if(isA_Z(c)){
					if(start < 0)start=i;
				}else{
					if(start > -1){
						if(!isBetween(c,from,to) && !isNumber(c)){
							return str.substring(start, i);
						}
					}
				}
			}
			if(start > -1) return str.substring(start);
		}
		return null;
	}
	
	private static boolean isBetween(char ch,int from,int to){
		if(ch < from) return false;
		if(ch > to) return false;
		return true;
	}
	
	/**
	 * 统计c在String 中出现的次数
	 * @param str
	 * @return
	 */
	public static int calCharCount(String str,char c){
		if(isNotEmpty(str)){
			int count = 0;
			for(char ch:str.toCharArray()){
				if(ch == c)count++;
			}
			return count;
		}
		return -1;
	}
	
	/**
	 * 提取第一次连续间隔字符串
	 * @param str
	 * @param from
	 * @param to
	 * @return
	 */
	public static String fetchCharBetween(String str,char from, char to){
		if(isNotEmpty(str)){
			if(from == to || from > to) return null;
			int start = -1;
			for(int i=0; i<str.length(); i++){
				char c = str.charAt(i);
				if(c > from-1 && c < to+1){
					if(start < 0) start = i;
				}else{
					if(start > -1){
						return str.substring(start, i);
					}
				}
			}
			if(start > -1) return str.substring(start);
		}
		return null;
	}
	
	/**
	 * 提取末端短语
	 * @param sentence
	 * @param len
	 * @return
	 */
	public static String fetchLastPart(String sentence,int len){
		if(StringUtil.isNotEmpty(sentence)){
			String[] parts = sentence.split("\\s");
			String result = "";
			for(int i=parts.length-1; i>-1;i--){
				if(result.length() > 0){
					result  = parts[i] + " " + result;
				}else{
					result = parts[i];
				}
				if(result.length() >= len)return result;
			}
			return result;
		}
		return "";
	}
	
	public static String join(String[] array,char c){
		return StringUtils.join(array, c);
	} 

	public static String join(String[] array,char c,int start,int end){
		return StringUtils.join(array, c,start,end);
	} 
	
	/**
	 * 去掉空字符
	 * @param content
	 * @param splitReg
	 * @return
	 */
	public static String[] split(String content,String splitReg){
		List<String> list = Arrays.asList(content.split(splitReg));
		return list.stream().map(String::trim).filter(str -> isNotEmpty(str)).toArray(String[]::new);
	}
	
	/**
	 * 取最后len个完整的字符
	 * @param text
	 * @param len
	 * @return
	 */
	public static String fetchLastSubstr(String text,int len){
		if(text != null && text.length() > len){
			String[] parts = text.split("\\s");
			StringBuffer buffer = new StringBuffer();
			for(int i=parts.length-1;i>-1;i--){
				buffer.insert(0, parts[i]);
				buffer.insert(0, " ");
				if(buffer.length() > len){
					buffer.deleteCharAt(0);
					return buffer.toString();
				}
			}
		}
		return text;
	}
	
	/**
	 * 取闭包内的字符串
	 * @param string
	 * @param start
	 * @param end
	 * @return
	 */
	public static String fetchFirstClosure(String string,char start,char end){
		if(isNotEmpty(string)){
			int startIndex = string.indexOf(start);
			int endIndex = string.indexOf(end);
			if(endIndex > startIndex && startIndex > -1){
				return string.substring(startIndex+1, endIndex);
			}
		}
		return null;
	}
	
	public static void main(String[] args) {}
}
