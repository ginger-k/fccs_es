package com.fccs.es_api.factory;

import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import com.fccs.es_api.constant.Constant;
import com.fccs.es_api.service.NhFloorElasticSearchService;
import com.fccs.es_api.service.NhFloorGeoSearchService;
import com.fccs.es_api.service.NhModelElasticSearchService;

public class EsServiceFactory {
	
	private static NhFloorElasticSearchService nhFloorElasticSearchService;
	private static NhModelElasticSearchService nhModelElasticSearchService;
	private static NhFloorGeoSearchService nhFloorGeoSearchService;
	
	//用户使用的时候，第一次加载，之后都不加载
	public static synchronized NhFloorElasticSearchService createNhFloorElasticSearchService() {
		if (nhFloorElasticSearchService == null) {
			nhFloorElasticSearchService = getRemoteService("/sc/nhFloorElasticSearchServiceHttp.sc", NhFloorElasticSearchService.class);
		}
		return nhFloorElasticSearchService;
	}

	public static synchronized NhModelElasticSearchService createNhModelElasticSearchService() {
		if (nhModelElasticSearchService == null) {
			nhModelElasticSearchService = getRemoteService("/sc/nhModelElasticSearchServiceHttp.sc", NhModelElasticSearchService.class);
		} 
		return nhModelElasticSearchService;
	}
	
	public static synchronized NhFloorGeoSearchService createNhFloorGeoSearchService() {
		if (nhFloorGeoSearchService == null) {
			nhFloorGeoSearchService = getRemoteService("/sc/nhFloorGeoSearchServiceHttp.sc", NhFloorGeoSearchService.class);
		}
		return nhFloorGeoSearchService;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T getRemoteService(String url, Class<T> interfaceClass) {
		HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
		httpInvokerProxyFactoryBean.setServiceUrl(Constant.ES_SERVICE_ADDRESS + url);
		httpInvokerProxyFactoryBean.setServiceInterface(interfaceClass);
		httpInvokerProxyFactoryBean.afterPropertiesSet();
		return (T) httpInvokerProxyFactoryBean.getObject();
	}
	
}
