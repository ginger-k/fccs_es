package com.fccs.es_service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.GeoDistanceFilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fccs.es_api.exception.EsException;
import com.fccs.es_api.vo.EsPageBean;
import com.fccs.es_service.frame.SearchTemplate;



public class GeoSearchServiceImpl extends SearchTemplate {
	
	public EsPageBean<Map<String, Object>> getGeoSearchList(Map<String, Object> map, int pageNow, int pageSize)  throws EsException {
		return super.doSearch("oracle_fccs", "model", pageNow, pageSize, map);
	}

	@Override
	protected void setFilter(SearchRequestBuilder searchRequestBuilder, Map<String, Object> map) {
		int count = 0;
		BoolFilterBuilder totalFilter = FilterBuilders.boolFilter();
		
		
		
		Integer distance = (Integer) map.get("distance");
		if (distance != null && distance > 0) {
			double latitude = (Double) map.get("latitude");
			double longitude = (Double) map.get("longitude");
			GeoDistanceFilterBuilder geoDistance = FilterBuilders.geoDistanceFilter("mapXY")
					.point(latitude, longitude)
					.distance(distance, DistanceUnit.METERS)                 
					.optimizeBbox("memory")                                 
					.geoDistance(GeoDistance.ARC); 
			totalFilter.must(geoDistance);
			count++;
		}
		
		
		
		if (count > 0) {
			searchRequestBuilder.setPostFilter(totalFilter);
		}
	}
	
	@Override
	protected void addSort(SearchRequestBuilder searchRequestBuilder, Map<String, Object> map) {
		Integer distance = (Integer) map.get("distance");
		if (distance != null && distance > 0) {
			double latitude = (Double) map.get("latitude");
			double longitude = (Double) map.get("longitude");
			 // 获取距离多少公里 这个才是获取点与点之间的距离的
	        GeoDistanceSortBuilder sort = SortBuilders.geoDistanceSort("mapXY");
	        sort.unit(DistanceUnit.METERS);
	        sort.order(SortOrder.ASC);
	        sort.point(latitude, longitude);
	        searchRequestBuilder.addSort(sort);
		}
	}
	
	@Override
	protected List<Map<String, Object>> processSearchHits(SearchHits hits, Map<String, Object> params) throws EsException {
		SearchHit[] hitsArray = hits.hits();
		List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
		for (SearchHit hit : hitsArray) {
			Map<String, Object> map = hit.sourceAsMap();
			Integer distance = (Integer) map.get("distance");
			if (distance != null && distance > 0) {
				// 获取距离值，并保留两位小数点
				BigDecimal geoDis = new BigDecimal((Double)hit.getSortValues()[0]);
				// 在创建MAPPING的时候，属性名的不可为geoDistance。
				map.put("geoDistance", geoDis.setScale(0, BigDecimal.ROUND_HALF_DOWN));
			}
			list.add(map);
		}
		return list;
	}

}
