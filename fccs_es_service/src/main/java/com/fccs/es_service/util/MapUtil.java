package com.fccs.es_service.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.math.NumberUtils;

public class MapUtil {

	public static String toString(Map<String, Object> map, String name) {
		return toString(map, name, null);
	}

	public static String toString(Map<String, Object> map, String name,
			String defaultValue) {
		Object obj = map.get(name);
		if (obj == null)
			return defaultValue;
		return obj.toString();
	}

	public static int toInt(Map<String, Object> map, String name) {
		return toInt(map, name, 0);
	}

	public static int toInt(Map<String, Object> map, String name,
			int defaultValue) {
		if (map == null)
			return defaultValue;
		Object obj = map.get(name);
		if (obj == null)
			return defaultValue;
		return NumberUtils.toInt(obj.toString()+"", defaultValue);
	}

	public static long toLong(Map<String, Object> map, String name) {
		return toLong(map, name, 0);
	}

	public static long toLong(Map<String, Object> map, String name,
			int defaultValue) {
		if (map == null)
			return defaultValue;
		Object obj = map.get(name);
		if (obj == null)
			return defaultValue;
		return NumberUtils.toLong(obj.toString()+"", defaultValue);
	}

	public static float toFloat(Map<String, Object> map, String name) {
		return toFloat(map, name, 0);
	}

	public static float toFloat(Map<String, Object> map, String name,
			float defaultValue) {
		if (map == null)
			return defaultValue;
		Object obj = map.get(name);
		if (obj == null)
			return defaultValue;
		return NumberUtils.toFloat(obj.toString()+"", defaultValue);
	}

	public static double toDouble(Map<String, Object> map, String name,
			double defaultValue) {
		if (map == null)
			return defaultValue;
		Object obj = map.get(name);
		if (obj == null)
			return defaultValue;
		return NumberUtils.toDouble(obj.toString()+"", defaultValue);
	}

	public static double toDouble(Map<String, Object> map, String name) {
		return toDouble(map, name, 0);
	}

	public static Date toDate(Map<String, Object> map, String name,
			Date defaultValue) {
		Object obj = map.get(name);
		if (obj == null)
			return defaultValue;
		return (Date) obj;
	}

	public static Date toDate(Map<String, Object> map, String name) {
		return toDate(map, name, null);
	}

	/**
	 * Map����
	 * 
	 * @param unsort_map
	 * @return
	 */
	public static SortedMap<String, Object> mapSortByKey(
			Map<String, Object> unsort_map) {
		TreeMap<String, Object> result = new TreeMap<String, Object>();

		Object[] unsort_key = unsort_map.keySet().toArray();
		Arrays.sort(unsort_key);

		for (int i = 0; i < unsort_key.length; i++) {
			result.put(unsort_key[i].toString(), unsort_map.get(unsort_key[i]));
		}
		return result.tailMap(result.firstKey());
	}

	/**
	 * StringתJSON ע:�ִ����ܰ�˫��š�����ŵ� ���磺floorId:1,count:2,cache:0 ==>
	 * {"floorId":"1","count":"2","cache":"0"}
	 * 
	 * @param str
	 * @return
	 */
	public static String String2Json(String str) {
		String s[] = str.split(",");
		String json = "{";
		for (int i = 0; i < s.length; i++) {
			String s1[] = s[i].split(":");

			if (i > 0)
				json += ",";

			json += "\"" + s1[0] + "\":\"" + s1[1] + "\"";
		}
		json += "}";
		return json;
	}



	/**
	 * beanתmap
	 * 
	 * @param <T>
	 */
	public static <T> Map<String, Object> bean2Map(T obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Field.setAccessible(fields, true);
			for (int i = 0; i < fields.length; i++) {
				map.put(fields[i].getName(), fields[i].get(obj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * mapתbean
	 * 
	 * @param <T>
	 */
	@SuppressWarnings("unchecked")
	public static <T> T map2Bean(Map<String, Object> map, Class<T> cls) {
		Object obj = null;
		try {
			obj = cls.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Method[] methods = cls.getDeclaredMethods();
		for (Method m : methods) {
			String methodName = m.getName();
			Class<T>[] types = (Class<T>[]) m.getParameterTypes();
			if (types.length != 1) {
				continue;
			}
			if (methodName.indexOf("set") < 0) {
				continue;
			}
			String type = types[0].getSimpleName();

			Object field = methodName.substring(3, 4).toLowerCase()
					+ methodName.substring(4);
			if (map.containsKey(field)) {
				Object value = map.get(field);
				try {
					if (type.equals("String")) {
						m.invoke(obj, new Object[] { value });
					} else if (type.equals("int") || type.equals("Integer")) {
						m.invoke(obj, new Object[] { new Integer(value + "") });
					} else if (type.equals("double") || type.equals("Double")) {
						m.invoke(obj, new Object[] { new Double(value + "") });
					} else if (type.equals("float") || type.equals("Float")) {
						m.invoke(obj, new Object[] { new Float(value + "") });
					} else if (type.equals("long") || type.equals("Long")) {
						m.invoke(obj, new Object[] { new Long(value + "") });
					} else if (type.equals("boolean") || type.equals("Boolean")) {
						m.invoke(obj, new Object[] { new Boolean(value + "") });
					} else if (type.equals("BigDecimal")) {
						m.invoke(obj,
								new Object[] { new BigDecimal(value + "") });
					} else if (type.equals("Date")) {
						Date date = null;
						if (value.getClass().getName().equals("java.util.Date")) {
							date = (Date) value;
						} else if (value.getClass().getName().equals("java.lang.String")){
							String format = ((String) value).indexOf(":") > 0 ? "yyyy-MM-dd hh:mm:ss"
									: "yyyy-MM-dd";
							SimpleDateFormat sf = new SimpleDateFormat();
							sf.applyPattern(format);
							date = sf.parse((String) value);
						} else if (value.getClass().getName().equals("java.lang.Integer")) {
							date = new Date(Long.valueOf(value + ""));
						}
						if (date != null) {
							m.invoke(obj, new Object[] { date });
						}
					} else if (type.equals("byte[]")) {
						m.invoke(obj, new Object[] { new String(value + "")
								.getBytes() });
					} else if (type.equals("int") || type.equals("Integer")) {
						m.invoke(obj, new Object[] { new Integer("fieldName") });
					} else if (type.equals("int") || type.equals("Integer")) {
						m.invoke(obj, new Object[] { new Integer("fieldName") });
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return (T) obj;
	}

	/**
	 * rewrite2mapα��̬�����?��ֻ֧����λ��ĸ�Ĳ���ֻ֤����ָ���Ĳ���
	 * 
	 * pattern���磺 ai_hu_pl_ph_al_ah_rm_ha_to_bt_tl_th_ad_id_oq_so_ss_od_p
	 * ע�⣺��ҳ��ͳһΪp�������һ��λ�ã��������ʹ��2���ַ�
	 * 
	 * @param pattern
	 * @param rewriteUrl
	 * 
	 * @return
	 */
	public static Map<String, Object> parseRewrite(String pattern,
			String rewriteUrl) {
		String patternOriginal = pattern;

		// ȥ��p
		if (patternOriginal.endsWith("_p")) {
			pattern = pattern.substring(0, pattern.length() - 1);
		}

		// �����м����ͨ����
		String[] patternArr = pattern.split("\\_");

		Map<String, Object> xparams = new HashMap<String, Object>();

		if (StringUtil.hasText(rewriteUrl)) {
			String[] rewriteUrlArr = rewriteUrl.split("\\_");

			for (int i = 0; i < rewriteUrlArr.length; i++) {
				String aUrl = rewriteUrlArr[i];

				for (int j = 0; j < patternArr.length; j++) {
					String key = aUrl.substring(0, 2);
					String value = aUrl.substring(2, aUrl.length());

					if (key.equals(patternArr[j])) {
						xparams.put(key, value);
						break;
					}
				}
			}
		}

		// ���䴦��p
		if (patternOriginal.endsWith("_p") && StringUtil.hasText(rewriteUrl)) {
			String[] strArr = rewriteUrl.split("\\_");
			if (strArr.length > 0) {
				String str = strArr[strArr.length - 1];
				if (str.startsWith("p")) {
					str = str.replaceFirst("p", "");
					xparams.put("p", str);
				}
			}
		}

		return xparams;
	}

}
