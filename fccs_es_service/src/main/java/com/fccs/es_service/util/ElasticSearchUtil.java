package com.fccs.es_service.util;

import java.lang.reflect.Constructor;
import java.util.Properties;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class ElasticSearchUtil {

	private static final String propertiesName = "config.properties";
	private static final String propertiesKey = "elasticsearch_host";
	private static TransportClient client;
	private static Settings settings = ImmutableSettings.settingsBuilder().put("client.transport.sniff", true).build();
	
	static {
		try {
			Class<?> clazz = Class.forName(TransportClient.class.getName());
			Constructor<?> constructor = clazz.getDeclaredConstructor(Settings.class);
			constructor.setAccessible(true);
			client = (TransportClient) constructor.newInstance(settings);
			
			Properties properties = PropertiesLoaderUtils.loadAllProperties(propertiesName);
			String propertiesValue = properties.getProperty(propertiesKey);
			String[] hosts = propertiesValue.split(",");
			
			if (client != null && hosts != null) {
				for(int i = 0; i < hosts.length; i ++) {
					String host = hosts[i].split(":")[0];
					int port = Integer.parseInt(hosts[i].split(":")[1]);
					client.addTransportAddress(new InetSocketTransportAddress(host, port));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private ElasticSearchUtil() {
	
	}
	
	public static synchronized TransportClient getClient() {
		return client;
	}
	
}
