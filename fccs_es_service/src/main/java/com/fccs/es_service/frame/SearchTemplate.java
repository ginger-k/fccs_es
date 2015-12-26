package com.fccs.es_service.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.fccs.es_api.exception.EsException;
import com.fccs.es_api.vo.EsPageBean;
import com.fccs.es_service.util.ElasticSearchUtil;

public class SearchTemplate {
	
	public EsPageBean<Map<String, Object>> doSearch(String index, String type, int pageNow, int pageSize, Map<String, Object> map) throws EsException {
		if (StringUtils.isBlank(index) || StringUtils.isBlank(type) || pageNow <= 0 || pageSize <= 0) {
			throw new EsException("-------> 参数有错误 <-------");
		}
		Client client = ElasticSearchUtil.getClient();
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type);
		
		this.setQuery(searchRequestBuilder, map);
		this.setFilter(searchRequestBuilder, map);
		this.addSort(searchRequestBuilder, map);
		this.addHighlighted(searchRequestBuilder);
		
		int esFrom = pageSize * (pageNow - 1);
		SearchResponse response = searchRequestBuilder.setFrom(esFrom).setSize(pageSize).setExplain(true).execute().actionGet();
		SearchHits hits = response.getHits();
		
		List<Map<String, Object>> items = this.processResponse(hits);
		
		long totalHits = hits.getTotalHits();
		int totalRecord = Integer.valueOf(String.valueOf(totalHits));
		int totalPage = (totalRecord - 1)/pageSize + 1;
		return new EsPageBean<Map<String, Object>>(pageSize, pageNow, totalPage, totalRecord, items);
	}
	
	protected void setQuery(SearchRequestBuilder searchRequestBuilder, Map<String, Object> map) {
	}

	protected void setFilter(SearchRequestBuilder searchRequestBuilder, Map<String, Object> map) {
	}
	
	protected void addSort(SearchRequestBuilder searchRequestBuilder, Map<String, Object> map) {
	}
	
	protected void addHighlighted(SearchRequestBuilder searchRequestBuilder) {
	}
	
	//如果要处理高亮，请重写次方法
	protected List<Map<String, Object>> processResponse(SearchHits hits) throws EsException {
		//使用数组遍历的目的：是不是数据遍历更快
		SearchHit[] hitsArray = hits.hits();
		List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < hitsArray.length; i++) {
			Map<String, Object> map = hitsArray[i].sourceAsMap();
			list.add(map);
		}
		return list;
	}


}
