package com.fccs.es_api.service;

import java.util.Map;

import com.fccs.es_api.vo.EsPageBean;

public interface NhModelElasticSearchService {

	public EsPageBean<Map<String, Object>> getFloorSearchList(Map<String, Object> map, int pageNow, int pageSize);

}
