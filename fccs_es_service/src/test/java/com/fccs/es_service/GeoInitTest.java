package com.fccs.es_service;


import java.text.DecimalFormat;
import java.util.Random;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
//import org.junit.Test;

import com.fccs.es_service.util.ElasticSearchUtil;

public class GeoInitTest {
	
	/*
	 * 初始化mapping
	 */
//	@Test
	public void initMapping() throws Exception {
		Client client = ElasticSearchUtil.getClient();
		client.admin().indices().prepareCreate("oracle_fccs2").execute().actionGet(); //index
		XContentBuilder mapping = XContentFactory.jsonBuilder()
				.startObject()  
	        		.startObject("floor2")  //type
		        		.startObject("properties")         
			        		.startObject("mapXY").field("type", "geo_point").endObject()  
		        		.endObject()  
		        	.endObject()  
		        .endObject();
		client.admin().indices().preparePutMapping("oracle_fccs2").setType("floor2").setSource(mapping).execute().actionGet();
		client.close();
	}
	
	/*
	 * 初始化index
	 */
//	@Test
	public void initIndex() throws Exception {
		Client client = ElasticSearchUtil.getClient();
		double lat = 39.929986;
        double lon = 116.395645;
		for (int i = 1; i < 10000; i++) {
			 double max = 0.00001;
            double min = 0.000001;
            Random random = new Random();
            double s = random.nextDouble() % (max - min + 1) + max;
            System.out.println(s);
            DecimalFormat df = new DecimalFormat("######0.000000");
            String lons = df.format(s + lon);
            String lats = df.format(s + lat);
            Double dlon = Double.valueOf(lons);
            Double dlat = Double.valueOf(lats);
			XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
					.startObject()
					.field("issueid", i)
					.field("name", "楼盘名称" + i)
					.field("latitude", dlat)
					.field("longitude", dlon)
					.startArray("location").value(dlon).value(dlat).endArray()
					.endObject();
			client.prepareIndex("floor", "position", i + "").setSource(xContentBuilder).execute().actionGet();
		}
		client.close();
	}

	
//	@Test
	public void deleteIndex() {
		Client client = ElasticSearchUtil.getClient();
		client.admin().indices().prepareDelete("floor").execute().actionGet();
		client.close();
	}
}
