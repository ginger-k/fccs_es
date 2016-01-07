package com.fccs.es_service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.junit.Test;

import com.fccs.es_api.exception.EsException;
import com.fccs.es_api.vo.EsPageBean;
import com.fccs.es_service.impl.GeoSearchServiceImpl;

public class GeoServiceTest {

//	@Test
	public void test1() throws EsException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("distance", 2000);
		map.put("latitude", 30.753955894612);
		map.put("longitude", 120.79773351903);
		long start = System.currentTimeMillis();
		GeoSearchServiceImpl geoService = new GeoSearchServiceImpl();
		long end = System.currentTimeMillis();
		System.out.println("花费时间：" + (end - start) + "ms");
		EsPageBean<Map<String,Object>> esPageBean = geoService.getGeoSearchList(map, 1, 100);
		System.out.println("totalPage: " + esPageBean.getTotalPage());
		System.out.println("totalRecord: " + esPageBean.getTotalRecord());
		List<Map<String,Object>> items = esPageBean.getItems();
		for (Map<String, Object> m : items) {
			System.out.println("floor: " + m.get("floor"));
			System.out.println("mapX: " + m.get("mapX"));
			System.out.println("mapY: " + m.get("mapY"));
			System.out.println("mapXY: " + m.get("mapXY"));
			System.out.println("geoDistance: " + m.get("geoDistance") + "米");
			System.out.println("");
		}
	}
	
}
