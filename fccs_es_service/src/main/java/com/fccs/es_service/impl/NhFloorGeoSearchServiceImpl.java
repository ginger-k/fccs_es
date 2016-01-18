package com.fccs.es_service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.GeoDistanceRangeFilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fccs.es_api.bean.NhFloorIssue;
import com.fccs.es_api.service.NhFloorGeoSearchService;
import com.fccs.es_api.vo.EsPageBean;
import com.fccs.es_service.util.ElasticSearchUtil;
import com.fccs.es_service.util.MapUtil;



public class NhFloorGeoSearchServiceImpl implements NhFloorGeoSearchService {
	
	private static final Logger log = Logger.getLogger(NhFloorGeoSearchServiceImpl.class);
	
	@Override
	public EsPageBean<NhFloorIssue> getFloorSearchList(Map<String, Object> map, int pageNow, int pageSize) {
		try {
			if (pageNow <= 0 || pageSize <= 0) {
				throw new IllegalArgumentException("-------> 参数有错误 <-------");
			}
			Client client = ElasticSearchUtil.getClient();
			SearchRequestBuilder searchRequestBuilder = client.prepareSearch("oracle_fccs").setTypes("floor");
			
			if (this.setGeo(searchRequestBuilder, map)) {
				int esFrom = pageSize * (pageNow - 1);
				SearchResponse response = searchRequestBuilder.setFrom(esFrom).setSize(pageSize).setExplain(true).execute().actionGet();
				SearchHits hits = response.getHits();
				List<NhFloorIssue> list = this.processBean(hits, map);
				long totalHits = hits.getTotalHits();
				int totalRecord = Integer.valueOf(String.valueOf(totalHits));
				int totalPage = (totalRecord - 1)/pageSize + 1;
				return new EsPageBean<NhFloorIssue>(pageSize, pageNow, totalPage, totalRecord, list);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	private boolean setGeo(SearchRequestBuilder searchRequestBuilder, Map<String, Object> map) {
		Integer distance = (Integer) map.get("distance");
		Double latitude = (Double) map.get("latitude");
		Double longitude = (Double) map.get("longitude");
		if (distance != null && distance > 0 && latitude != null && latitude > 0 && longitude != null && longitude > 0) {
				GeoDistanceRangeFilterBuilder geoDistance = FilterBuilders.geoDistanceRangeFilter("mapXY")
						.point(latitude, longitude)
						.gt("0m").lte(distance+"m")
						.optimizeBbox("memory")                                 
						.geoDistance(GeoDistance.ARC); 
				searchRequestBuilder.setPostFilter(geoDistance);
				GeoDistanceSortBuilder sort = SortBuilders.geoDistanceSort("mapXY");
		        sort.unit(DistanceUnit.METERS);
		        sort.order(SortOrder.ASC);
		        sort.point(latitude, longitude);
		        searchRequestBuilder.addSort(sort);
		        return true;
		} 
		return false;
	}
	
	private List<NhFloorIssue> processBean(SearchHits hits, Map<String, Object> params) {
		List<NhFloorIssue> list = new ArrayList<NhFloorIssue>();
		for (SearchHit hit : hits) {
				Map<String, Object> result = hit.sourceAsMap();
				Integer distance = (Integer) params.get("distance");
				if (distance != null && distance > 0) {
					double latitude = (Double) params.get("latitude");
					double longitude = (Double) params.get("longitude");
					if (latitude > 0 && longitude > 0) {
						// 获取距离值
						Object[] geoValues = hit.getSortValues();
						if (geoValues != null && geoValues.length > 0) {
							result.put("geoDistance", (Double)geoValues[0]);
						}
					}
				}
				NhFloorIssue obj = this.innerProcess(result);
				list.add(obj);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private NhFloorIssue innerProcess(Map<String, Object> result) {
		NhFloorIssue obj = MapUtil.map2Bean(result, NhFloorIssue.class);
		List<Map<String, Object>> priceList = (List<Map<String, Object>>) result.get("priceList");
		obj.setPriceList(priceList);
		List<Map<String, Object>> houseList = (List<Map<String, Object>>) result.get("houseList");
		obj.setHouseList(houseList);
		List<Map<String, Object>> newsList = (List<Map<String, Object>>) result.get("newsList");
		obj.setNewsList(newsList);
		List<Map<String, Object>> modelList = (List<Map<String, Object>>) result.get("modelList");
		obj.setModelList(modelList);
		Map<String, Object> other = (Map<String, Object>) result.get("other");
		if (other == null) {
			other = new HashMap<String, Object>();
		}
		other.put("spell", result.get("spell"));
		other.put("spellFull", result.get("spellFull"));
		other.put("gradeSchool", result.get("gradeSchool"));
		other.put("highSchool", result.get("highSchool"));
		other.put("fletter", result.get("fletter"));
		other.put("subwayId", result.get("subwayId"));
		other.put("sale", result.get("sale"));
		other.put("fccsMoneyState", result.get("fccsMoneyState"));
		other.put("fccsMoneyUpdateTime", result.get("fccsMoneyUpdateTime"));
		other.put("estate", result.get("estate"));
		other.put("orderNumber", result.get("orderNumber"));
		other.put("updateTimeNumber", result.get("updateTimeNumber"));
		other.put("adMoney", result.get("adMoney"));
		other.put("hits", result.get("hits"));
		other.put("shellOutNumber", result.get("shellOutNumber"));
		other.put("openQuotationNumber", result.get("openQuotationNumber"));
		other.put("totalPriceOrder", result.get("totalPriceOrder"));
		other.put("priceOrder", result.get("priceOrder"));
		other.put("fccsMoneyState", result.get("fccsMoneyState"));
		other.put("fccsMoneyUpdateTime", result.get("fccsMoneyUpdateTime"));
		other.put("groupBuyCount", result.get("groupBuyCount"));
		other.put("geoDistance", result.get("geoDistance"));
		other.put("totalHits", result.get("totalHits"));
		obj.setOther(other);
		return obj;
	}

}
