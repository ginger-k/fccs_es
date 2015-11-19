package com.fccs.es_service.init;

import java.io.IOException;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.fccs.es_service.util.ElasticSearchUtil;

public class ModelInit {

	/*
	 * es户型搜索数据初始化
	 */
	public static void setModelMapping() throws IOException {
		Client client = ElasticSearchUtil.getClient();
		client.admin().indices().prepareCreate("oracle_fccs").execute().actionGet();
		XContentBuilder mapping = XContentFactory.jsonBuilder()
				.startObject()  
	        		.startObject("model")  //type
		        		.startObject("properties")         
			        		.startObject("areaId").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("balcony").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("buildingType").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("commentCount").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("feature").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("floor").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("hall").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("houseArea").field("type", "double").field("index", "not_analyzed").endObject()  
			        		.startObject("houseArea1").field("type", "double").field("index", "not_analyzed").endObject()  
			        		.startObject("houseAreaHigh").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("houseCount").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("houseFrame").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("houseModelId").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("houseUse").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("houseUseId").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("issueId").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("phone").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("pic").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("price").field("type", "double").field("index", "not_analyzed").endObject()  
			        		.startObject("room").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("sellSchedule").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("sellSchedule1").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("sellSchedule2").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("shopType").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("siteId").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("sumIssue").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("sumPriceAverage").field("type", "double").field("index", "not_analyzed").endObject()  
			        		.startObject("taskHouseUse").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("templetUrl").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("toilet").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("totalPrice").field("type", "double").field("index", "not_analyzed").endObject()  
			        		.startObject("totalUv").field("type", "long").field("index", "not_analyzed").endObject()  
			        		.startObject("unit").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("updateTime").field("type", "date").field("index", "not_analyzed").endObject()  
			        		.startObject("url").field("type", "string").field("index", "not_analyzed").endObject()  
			        		.startObject("hits").field("type", "long").field("index", "not_analyzed").endObject()  
		        		.endObject()  
		        	.endObject()  
		        .endObject();
		client.admin().indices().preparePutMapping("oracle_fccs").setType("model").setSource(mapping).execute().actionGet();
		client.close();
	}
	
	/*
	 * 设置river更新
	 */
	public static void riverUpdateModel() throws IOException {
		String sql ="SELECT  m.housemodelid||f.floorid||i.issueid||p.priceid AS \"_id\",m.sumPriceAverage,m.HouseArea1,m.houseAreaHigh,i.taskHouseUse,f.areaId, "
				+ " m.houseModelId,m.pic,m.houseFrame, m.houseArea, m.buildingType, m.buildingTypeId, "
				+ " m.houseUse,m.room,m.hall,m.toilet,m.balcony,i.sellSchedule,i.sellSchedule1,i.sellSchedule2, "
				+ " m.houseUseId, m.issueId, f.floorId, f.floor, p.priceLow, p.priceAverage, p.sumPriceLow, "
				+ " p.sumAverage, p.showPrice, p.showSumPrice,f.feature,f.phone,f.phone1,f.estate,show_Phone, "
				+ " (SELECT COUNT(issueId) FROM fccs.floorIssue cf WHERE AVAILABLE=1 AND thisIssue>0 AND cf.floorId = i.floorId) AS sumIssue, "
				+ " (SELECT COUNT(issueId) FROM fccs.floorIssue caf WHERE AVAILABLE=1 AND caf.floorId = i.floorId) AS sumAllIssue, "
				+ " i.alias, i.thisIssue,i.floorUse,m.templetUrl,i.shopType, m.housemodelid||f.floorid||i.issueid||p.priceid AS esid, "
				+ " f.AVAILABLE AS availablef , f.pass AS passf ,i.AVAILABLE AS availablei, "
				+ " i.pass AS passi ,m.AVAILABLE AS availablem,  m.pass AS passm , m.display AS displaym "
				+ " FROM  fccs.housemodel m LEFT JOIN fccs.fdc_housemodel fdcm ON fdcm.housemodelid=m.housemodelid, "
				+ " fccs.floor f, fccs.floorIssue i, fccs.Floorissueprice p "
				+ " WHERE  m.buildingTypeId = p.buildingTypeId AND m.houseUseId = p.houseUseId  AND "
				+ " m.issueid = p.issueid AND m.issueid = i.issueid AND i.floorid = f.floorid  AND  f.estate>0  ORDER BY i.issueid";
		Client client = ElasticSearchUtil.getClient();
		XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
				.startObject()
					.field("type", "jdbc")
					.startObject("jdbc")
						.field("url", "jdbc:oracle:thin:@10.10.20.49:1521:fccs")
						.field("user", "fccs")
						.field("password", "fccsnhtest")
						.field("sql", sql)
						.field("index","fccs_model_test")  
                        .field("type","model")
                        .field("bulk_size", 100)
                        .field("max_bulk_requests", 30)
                        .field("bulk_timeout", "10s")
                        .field("flush_interval", "5s")
                        .field("schedule", "0 0-59 0-23 * * ?")
					.endObject()
				.endObject();
		client.prepareIndex("_river", "river_fccs_model_test", "_meta").setSource(xContentBuilder).execute().actionGet();
		client.close();
	}
	
	/*
	 * 删除索引
	 */
	public static void deleteIndex() {
		Client client = ElasticSearchUtil.getClient();
		client.admin().indices().prepareDelete("oracle_fccs").execute().actionGet();
		client.close();
	}
 	
	public static void main(String[] args) throws Exception {
		setModelMapping();
//		riverUpdateModel();
//		deleteIndex();
	}
	
	
}
