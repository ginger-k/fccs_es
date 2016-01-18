package com.fccs.es_api.service;


import com.fccs.es_api.bean.NhFloorIssue;
import com.fccs.es_api.vo.EsPageBean;

public interface NhFloorGeoSearchService {

	public EsPageBean<NhFloorIssue> getFloorSearchList(int pageNow, int pageSize, int distance, double latitude, double longitude);
}
