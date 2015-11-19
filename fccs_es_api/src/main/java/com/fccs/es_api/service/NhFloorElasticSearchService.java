package com.fccs.es_api.service;

import java.util.Map;

import com.fccs.es_api.bean.NhFloorIssue;
import com.fccs.es_api.exception.EsException;
import com.fccs.es_api.vo.EsPageBean;

public interface NhFloorElasticSearchService {

	public EsPageBean<NhFloorIssue> getFloorSearchList(Map<String, Object> conditions, int pageNow, int pageSize) throws EsException;

}
