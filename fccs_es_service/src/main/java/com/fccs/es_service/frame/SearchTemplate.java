package com.fccs.es_service.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;

import com.fccs.es_api.vo.EsPageBean;
import com.fccs.es_service.util.ElasticSearchUtil;

public abstract class SearchTemplate {
	
	protected EsPageBean<Map<String, Object>> doSearch(String index, String type, int pageNow, int pageSize, Map<String, Object> params) throws Exception {
			if (StringUtils.isBlank(index) || StringUtils.isBlank(type) || pageNow <= 0 || pageSize <= 0) {
				throw new IllegalArgumentException("-------> 参数有错误 <-------"); 
			}
			Client client = ElasticSearchUtil.getClient();
			SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
			
			this.setQuery(searchRequestBuilder, params);
			this.setFilter(searchRequestBuilder, params);
			this.addSort(searchRequestBuilder, params);
			this.addHighlighted(searchRequestBuilder, params);
			
			int esFrom = pageSize * (pageNow - 1);
			SearchResponse response = searchRequestBuilder.setFrom(esFrom).setSize(pageSize).setExplain(true).execute().actionGet();
			SearchHits hits = response.getHits();
			
			List<Map<String, Object>> items = this.processSearchHits(hits, params);
			
			long totalHits = hits.getTotalHits();
			int totalRecord = Integer.valueOf(String.valueOf(totalHits));
			int totalPage = (totalRecord - 1)/pageSize + 1;
			return new EsPageBean<Map<String, Object>>(pageSize, pageNow, totalPage, totalRecord, items);
	}
	
	protected void setQuery(SearchRequestBuilder searchRequestBuilder, Map<String, Object> params) {
	}

	protected void setFilter(SearchRequestBuilder searchRequestBuilder, Map<String, Object> params) {
	}
	
	protected void addSort(SearchRequestBuilder searchRequestBuilder, Map<String, Object> params) {
	}
	
	protected void addHighlighted(SearchRequestBuilder searchRequestBuilder, Map<String, Object> params) {
	}
	
	protected List<Map<String, Object>> processSearchHits(SearchHits hits, Map<String, Object> params) {
		//使用数组遍历的目的：是不是数据遍历更快
		SearchHit[] hitsArray = hits.hits();
		List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < hitsArray.length; i++) {
			Map<String, Object> map = hitsArray[i].sourceAsMap();
			list.add(map);
		}
		return list;
	}
	
	protected void putHighlightedFields(Map<String, Object> result, SearchHit hit, Map<String, Object> params) {
		Map<String, HighlightField> highlightFields = hit.getHighlightFields();
		if(highlightFields.size() > 0) {
			//添加了高亮的字段，重新赋值
			for (Entry<String, HighlightField> highlightField : highlightFields.entrySet()) {
				result.put(highlightField.getKey(), ((Text[])highlightField.getValue().getFragments())[0].toString());
			}
		}
	}
	
	/*
	 * 排序的值不是字段的值，比如geo_point类型
	 */
	protected void putSortValues(Map<String, Object> result, SearchHit hit, Map<String, Object> params) {
		Object[] sortValues = hit.getSortValues();
		if (sortValues != null) {
			for (int i = 0; i< sortValues.length; i++) {
//				result.put("key", sortValues[i]);
			}
		}
	}


}
