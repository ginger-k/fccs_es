package com.fccs.es_api.service;

import java.util.Map;

import com.fccs.es_api.bean.NhFloorIssue;
import com.fccs.es_api.vo.EsPageBean;

public interface NhFloorElasticSearchService {

	/*
	 * 返回null，表示服务有异常
	 */
	public EsPageBean<NhFloorIssue> getFloorSearchList(Map<String, Object> conditions, int pageNow, int pageSize);

}
