package com.fccs.es_api.builder;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fccs.es_api.service.NhFloorElasticSearchService;
import com.fccs.es_api.service.NhModelElasticSearchService;

public class ServiceBuilder {
	
	private static NhFloorElasticSearchService nhFloorElasticSearchService;
	private static NhModelElasticSearchService nhModelElasticSearchService;
	
	//用户使用的时候，第一次加载，之后都不加载
	public static NhFloorElasticSearchService getNhFloorElasticSearchService() {
		if (nhFloorElasticSearchService == null) {
			System.out.println("------- ------- -------> 加载-开始 <------- ------- -------");
			@SuppressWarnings("resource")
			ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("NhFloorElasticSearchService.xml");
			nhFloorElasticSearchService = (NhFloorElasticSearchService) applicationContext.getBean("com.fccs.es_api.nhFloorElasticSearchService");
			System.out.println("------- ------- -------> 加载-结束 <------- ------- -------");
		} else {
			System.out.println("------- ------- -------> 不加载 <------- ------- -------");
		}
		return nhFloorElasticSearchService;
	}

	public static NhModelElasticSearchService getNhModelElasticSearchService() {
		if (nhModelElasticSearchService == null) {
			@SuppressWarnings("resource")
			ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("NhModelElasticSearchService.xml");
			nhModelElasticSearchService = (NhModelElasticSearchService) applicationContext.getBean("com.fccs.es_api.nhModelElasticSearchService");
		} 
		return nhModelElasticSearchService;
	}
}
