package com.fccs.es_service.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




import net.sourceforge.pinyin4j.PinyinHelper;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.StringUtils;



public class StringUtil {

	public static String encode(String str, String fromEncoding,
			String toEncoding) {
		if (str != null) {
			try {
				return new String(str.getBytes(fromEncoding), toEncoding);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return str;
	}

	public static String encodeGBK(String str) {
		if (str != null) {
			try {
				return URLEncoder.encode(str, "GBK");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return str;
	}
	
	public static String ISO88591ToGBK(String str) {
		return encode(str, "ISO-8859-1", "GBK");
	}

	public static String GBKToIso88591(String str) {
		return encode(str, "GBK", "ISO-8859-1");
	}

	public static String ISO88591ToUTF(String str) {
		return encode(str, "ISO8859_1", "UTF-8");
	}
	
	public static String GBKToUTF(String str) {
		return encode(str, "GBK", "UTF-8");
	}

	/**
	 * �ַ�ת����16�����ַ�
	 * 
	 * @param str
	 * @return
	 */
	public static String str2hex(String str) {
		if (str == null)
			return null;
		byte bytes[] = str.getBytes();
		StringBuffer retString = new StringBuffer();
		for (int i = 0; i < bytes.length; ++i) {
			retString.append(Integer.toHexString(0x0100 + (bytes[i] & 0x00FF))
					.substring(1).toUpperCase());
		}
		return retString.toString();
	}

	/**
	 * ��16�����ַ�ת�����ַ�
	 * 
	 * @param hex
	 * @return
	 */
	public static String hex2str(String hex) {
		try {
			if (hex == null)
				return null;
			byte[] bts = new byte[hex.length() / 2];
			for (int i = 0; i < bts.length; i++) {
				bts[i] = (byte) Integer.parseInt(
						hex.substring(2 * i, 2 * i + 2), 16);
			}
			return new String(bts);
		} catch (Exception e) {
			return null;
		}
	}


	/**
	 * ����תƴ��
	 * 
	 * @param str
	 * @return
	 */
	public static String getPinYin(String str) {
		if(str==null) str ="";
		String convert = ""; 
		for (int j = 0; j < str.length(); j++) { 
			char word = str.charAt(j); 
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word); 
			if (pinyinArray != null) { 
				convert += pinyinArray[0].charAt(0); 
			} else { 
				convert += word; 
			} 
		} 
		return convert; 
	}

	/**
	 * ��Ӣ�Ļ���ַ��ȡ������ȫ����1����
	 * 
	 * @param str
	 * @param toCount
	 * @param more
	 * @return
	 */
	public static String subStringByChinese(String str, int toCount, String more) {
		toCount = toCount * 2;
		int reInt = 0;
		String reStr = "";
		if (str == null)
			return "";
		char[] tempChar = str.toCharArray();
		for (int kk = 0; (kk < tempChar.length && toCount > reInt); kk++) {
			@SuppressWarnings("static-access")
			String s1 = str.valueOf(tempChar[kk]);
			byte[] b = s1.getBytes();
			reInt += b.length;
			reStr += tempChar[kk];
		}
		if (toCount == reInt || (toCount == reInt - 1))
			reStr += more;
		return reStr;
	}

	public static int toInt(String str){
		return NumberUtils.toInt(str+"", 0);
	}
	
	public static int toInt(String str,int defaultValue){
		return NumberUtils.toInt(str+"", defaultValue);
	}
	
	public static int areaIdToCounty(String site,String areaId){
		int county = 0;
		String[] siteSplit = site.split("\\.");
		String[] areaIdSplit = areaId.split("\\.");
		if(areaIdSplit.length>siteSplit.length){
			county=NumberUtils.toInt(areaIdSplit[siteSplit.length], 0);
		}
		return county;
	}
	public static int areaIdToArea(String site,String areaId){
		int area = 0;
		String[] siteSplit = site.split("\\.");
		String[] areaIdSplit = areaId.split("\\.");
		if(areaIdSplit.length>siteSplit.length+1){
			area=NumberUtils.toInt(areaIdSplit[siteSplit.length+1], 0);
		}
		return area;
	}
	
	public static boolean hasText(Object s){
		if(s==null||s.toString().equals("")||s.toString().trim().equals(""))
			return false;
		return true;
	}

	/**
	 * ����תunicode
	 * 
	 * @param str
	 * @param toCount
	 * @param more
	 * @return
	 */
	 public static String toUnicode(final String gbString) {
		  char[] utfBytes = gbString.toCharArray();
		  StringBuffer buffer = new StringBuffer();
		  for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
		   String hexB = Integer.toHexString(utfBytes[byteIndex]);
		   if (hexB.length() <= 2) {
		    hexB = "00" + hexB;
		   }
		   buffer.append("\\u" + hexB);
		  }
		  return buffer.substring(0);
		 }
	 
	 
	 public static List<String> spiltString(String source){
		 if(!StringUtils.hasText(source)){
			 return Arrays.asList();
		 }
		 String[] results; 
		 try {
			results = source.split(",");
		} catch (Exception e) {
			List<String> list = new ArrayList<String>();
			list.add(source);
			return list;
		}
		 return Arrays.asList(results);
	 }
		public static String Num2Chi(int Num)
		{
			String Chi="",StrNum=String.valueOf(Num),ChiStr="��һ�����������߰˾�";
			int NumLen = StrNum.length();
			if(NumLen==1)
			{
				Chi=ChiStr.substring(Num,Num+1);
			}else{
				for(int i=0;i<NumLen;i++)
				{
					Chi=Chi+ChiStr.substring(Integer.parseInt(StrNum.substring(i,i+1)),Integer.parseInt(StrNum.substring(i,i+1))+1);
				}
			}
			return Chi;
		}
		
		/**
		 * ��ȡ���URL�е�¥��ID
		 * 
		 * @param url
		 * @return
		 */
		public static int getNewhouseHotIssueId(String url) {
			int issueId = 0;
			if (url != null) {
				String regex = "http://(\\w+).fccs.com/(newhouse|shop|office)/(\\d+)/index.shtml";

				Matcher matcher;
				Pattern ptn = Pattern.compile(regex);
				matcher = ptn.matcher(url);

				if (matcher.find()) {
					issueId = Integer.valueOf(matcher.group(3));
				}
			}
			return issueId;
		}
		public static String  mapGet(Map<String,Object> m,String key,String d){
			return mapGet(m,key,(Object)d)+"";
		}
		public static int mapGet(Map<String,Object> m,String key,int d){
			return parseInt(mapGet(m,key,(Object)d));
		}
		public static Object mapGet(Map<String,Object> m,String key,Object d){
			if(m==null) return null;
			Object o=m.get(key);
			if(o!=null)
				return o;
			return d;
		}
		public static int parseInt(Object o){
			return parseInt(o,0);
		}
		public static int parseInt(Object o,int d){
			try{
				return Integer.parseInt(o.toString());
			}catch(Exception e){
				return d;
			}
		}
}
