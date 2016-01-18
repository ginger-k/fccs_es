package com.fccs.es_service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fccs.es_api.bean.NhFloorIssue;
import com.fccs.es_api.service.NhModelElasticSearchService;
import com.fccs.es_api.vo.EsPageBean;
import com.fccs.es_service.frame.SearchTemplate;
import com.fccs.es_service.util.MapUtil;



public class NhModelElasticSearchServiceImpl extends SearchTemplate implements NhModelElasticSearchService {

	private Logger log = Logger.getLogger(NhModelElasticSearchServiceImpl.class);
	
	@Override
	public EsPageBean<Map<String, Object>> getFloorSearchList(Map<String, Object> map, int pageNow, int pageSize) {
		try {
			return super.doSearch("oracle_fccs", "model", pageNow, pageSize, map);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}
	

	
	@Override
	protected void setQuery(SearchRequestBuilder searchRequestBuilder, Map<String, Object> map) {
		BoolQueryBuilder totalQuery = QueryBuilders.boolQuery();
		// issueIds
		String issueIdss = MapUtil.toString(map, "issueIds");
		if (StringUtils.isNotBlank(issueIdss)) {
			String[] issueIdsArr = issueIdss.split(",");
			if (issueIdsArr != null && issueIdsArr.length > 0) {
				TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("issueId", issueIdsArr);
				totalQuery.must(termsQuery);
			}
		}
		// floor,houseframe,houseuse,buildingtype
		String keyWords = (String) map.get("keyWords");
		if (StringUtils.isNoneBlank(keyWords)) {
			NhFloorElasticSearchServiceImpl floorService = new NhFloorElasticSearchServiceImpl();
			Integer siteId = (Integer) map.get("siteId");
			Map<String, Object> param = new HashMap<String, Object>();
			if (siteId != null) {
				param.put("siteId", siteId);
			}
			param.put("floor", keyWords);
			EsPageBean<NhFloorIssue> esPageBean = floorService.getFloorSearchList(param, 1, 9999);
			List<NhFloorIssue> items = esPageBean.getItems();
			int[] issueIds = null;
			if (items != null && items.size() > 0) {
				issueIds = new int[items.size()];
				for (int i = 0; i < items.size(); i++) {
					issueIds[i] = items.get(i).getIssueId();
				}
			}
			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			if (issueIds != null && issueIds.length > 0) {
				QueryBuilder termQuery1 = QueryBuilders.termsQuery("issueId", issueIds);
				boolQuery.should(termQuery1);
			}
			QueryBuilder termQuery2 = QueryBuilders.matchPhraseQuery("houseFrame", keyWords);
			QueryBuilder termQuery3 = QueryBuilders.matchPhraseQuery("houseUse", keyWords);
			QueryBuilder termQuery4 = QueryBuilders.matchPhraseQuery("buildingType", keyWords);
			boolQuery.should(termQuery2);
			boolQuery.should(termQuery3);
			boolQuery.should(termQuery4);
			totalQuery.must(boolQuery);
		}
		// houseUseId
		int houseUseId = MapUtil.toInt(map, "houseUseId", -1);
		if (houseUseId > 0 && houseUseId != 9999) {
			TermQueryBuilder termQuery = QueryBuilders.termQuery("houseUseId", houseUseId);
			totalQuery.must(termQuery);
		} else if (houseUseId == 9999) {
			RegexpQueryBuilder regexpQuery = QueryBuilders.regexpQuery("taskHouseUse", "(.*)(,10,|,11,|,12,|,13,|,208,|,1440,)+(.*)");
			totalQuery.must(regexpQuery);
		}
		// featrue
		String feature = (String) map.get("feature");
		if (StringUtils.isNoneBlank(feature)) {
			QueryBuilder termQuery = QueryBuilders.matchPhraseQuery("feature", feature);
			totalQuery.must(termQuery);
		}
		searchRequestBuilder.setQuery(totalQuery);
	}

	@Override
	protected void setFilter(SearchRequestBuilder searchRequestBuilder, Map<String, Object> map) {
		int count = 0;
		BoolFilterBuilder totalFilter = FilterBuilders.boolFilter();
		// model.siteid
		int siteId = MapUtil.toInt(map, "siteId", 0);
		if (siteId > 0) {
			TermFilterBuilder termFilter = FilterBuilders.termFilter("siteId", siteId);
			totalFilter.must(termFilter);
			count++;
		}
		// model.room
		int room = MapUtil.toInt(map, "room", 0);
		if (room > 0) {
			if (room < 6) {
				TermFilterBuilder termFilter = FilterBuilders.termFilter("room", room);
				totalFilter.must(termFilter);
				count++;
			}
			if (room == 6) {
				RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter("room").gte(room);
				totalFilter.must(rangeFilter);
				count++;
			}
		}
		// housemodelid
		String houseModelIds = (String) map.get("houseModelIds");
		if (StringUtils.isNotBlank(houseModelIds)) {
			String[] arr = houseModelIds.split(",");
			if (arr != null && arr.length > 0) {
				TermsFilterBuilder termsFilter = FilterBuilders.termsFilter("houseModelId", arr);
				totalFilter.must(termsFilter);
				count++;
			}
		}
		// templeturl
		int templetUrl = MapUtil.toInt(map, "templetUrl");
		if (templetUrl == 1) {
			ExistsFilterBuilder existsFilter = FilterBuilders.existsFilter("templetUrl");
			totalFilter.must(existsFilter);
			count++;
		}
		// price
		double priceBegin = MapUtil.toDouble(map, "priceBegin");
		double priceEnd = MapUtil.toDouble(map, "priceEnd");
		if (priceBegin > 0 || priceEnd > 0) {
			RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter("price");
			if (priceBegin > 0) {
				rangeFilter.gte(priceBegin);
			}
			if (priceEnd > 0) {
				rangeFilter.lte(priceEnd);
			}
			totalFilter.must(rangeFilter);
			count++;
		}
		// totalPrice
		double totalPriceBegin = MapUtil.toDouble(map, "totalPriceBegin");
		double totalPriceEnd = MapUtil.toDouble(map, "totalPriceEnd");
		if (totalPriceBegin > 0 || totalPriceEnd > 0) {
			RangeFilterBuilder rangeFilter = FilterBuilders.rangeFilter("totalPrice");
			if (totalPriceBegin > 0) {
				rangeFilter.gte(totalPriceBegin);
			}
			if (totalPriceEnd > 0) {
				rangeFilter.lte(totalPriceEnd);
			}
			totalFilter.must(rangeFilter);
			count++;
		}

		// 面积: model.houseArea,houseArea1,houseAreaHigh
		double houseAreaBegin = MapUtil.toDouble(map, "houseAreaBegin", -1d);
		double houseAreaEnd = MapUtil.toDouble(map, "houseAreaEnd", -1d);
		if (houseAreaBegin >= 0 || houseAreaEnd >= 0) {
			BoolFilterBuilder boolFilter = FilterBuilders.boolFilter();
			RangeFilterBuilder rangeFilter1 = FilterBuilders.rangeFilter("model.houseArea");
			if (houseAreaBegin >= 0)
				rangeFilter1.gte(houseAreaBegin);
			if (houseAreaEnd >= 0)
				rangeFilter1.lte(houseAreaEnd);
			RangeFilterBuilder rangeFilter2 = FilterBuilders.rangeFilter("model.houseArea1");
			if (houseAreaBegin >= 0)
				rangeFilter2.gte(houseAreaBegin);
			if (houseAreaEnd >= 0)
				rangeFilter2.lte(houseAreaEnd);
			RangeFilterBuilder rangeFilter3 = FilterBuilders.rangeFilter("model.houseAreaHigh");
			if (houseAreaBegin >= 0)
				rangeFilter3.gt(houseAreaBegin);
			else
				rangeFilter3.gt(0);
			if (houseAreaEnd >= 0)
				rangeFilter3.lte(houseAreaEnd);
			boolFilter.should(rangeFilter1);
			boolFilter.should(rangeFilter2);
			boolFilter.should(rangeFilter3);
			totalFilter.must(boolFilter);
			count++;
		}
		// areaId
		String areaId = (String) map.get("areaId");
		if (StringUtils.isNotBlank(areaId)) {
			if (areaId.endsWith(".")) {
				areaId = StringUtils.substringBeforeLast(areaId, ".");
			}
			TermFilterBuilder termFilter = FilterBuilders.termFilter("areaId", areaId);
			totalFilter.must(termFilter);
			count++;
		}
		if (count > 0) {
			searchRequestBuilder.setPostFilter(totalFilter);
		}
	}
	
	@Override
	protected void addSort(SearchRequestBuilder searchRequestBuilder, Map<String, Object> map) {
		int order = MapUtil.toInt(map, "order");
		switch (order) {
			case 1:
				searchRequestBuilder.addSort(SortBuilders.fieldSort("price").order(
						SortOrder.ASC));
				break;
			case 2:
				searchRequestBuilder.addSort(SortBuilders.fieldSort("price").order(
						SortOrder.DESC));
				break;
			case 3:
				searchRequestBuilder.addSort(SortBuilders.fieldSort("totalPrice")
						.order(SortOrder.ASC));
				break;
			case 4:
				searchRequestBuilder.addSort(SortBuilders.fieldSort("totalPrice")
						.order(SortOrder.DESC));
				break;
			case 5:
				searchRequestBuilder.addSort(SortBuilders.fieldSort("houseArea")
						.order(SortOrder.ASC));
				break;
			case 6:
				searchRequestBuilder.addSort(SortBuilders.fieldSort("houseArea")
						.order(SortOrder.DESC));
				break;
			case 7:
				searchRequestBuilder.addSort(SortBuilders.fieldSort("hits").order(
						SortOrder.ASC));
				break;
			case 8:
				searchRequestBuilder.addSort(SortBuilders.fieldSort("hits").order(
						SortOrder.DESC));
				break;
			case 9:
				searchRequestBuilder.addSort(SortBuilders.fieldSort("sellEstate")
						.order(SortOrder.ASC));
				break;
			default:
				searchRequestBuilder.addSort(SortBuilders.fieldSort("updateTime")
						.order(SortOrder.DESC));
				break;
		}
	}
	
	@Override
	protected List<Map<String, Object>> processSearchHits(SearchHits hits, Map<String, Object> params) {
		Integer siteId = null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hits.hits().length; i++) {
			Map<String, Object> map = hits.getAt(i).sourceAsMap();
			sb.append((Integer) map.get("issueId") + ",");
			siteId = (Integer) map.get("siteId");
		}
		String issueIds = StringUtils.substringBeforeLast(sb.toString(), ",");
		List<NhFloorIssue> dataFromFloor = this.getDataFromFloor(siteId, issueIds);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < hits.hits().length; i++) {
			Map<String, Object> map = hits.getAt(i).sourceAsMap();
			if (dataFromFloor != null && dataFromFloor.size() > 0) {
				for (NhFloorIssue nhFloorIssue : dataFromFloor) {
					if ((Integer)map.get("issueId") == nhFloorIssue.getIssueId()) {
						String floor = nhFloorIssue.getFloor();
						String phone = nhFloorIssue.getPhone();
						String url = nhFloorIssue.getUrl();
						map.put("floor", floor);
						map.put("phone", phone);
						map.put("url", url);
						break;
					}
				}
			}
			list.add(map);
		}
		return list;
	}
	
	private List<NhFloorIssue> getDataFromFloor(Integer siteId, String issueIds) {
		NhFloorElasticSearchServiceImpl floorService = new NhFloorElasticSearchServiceImpl();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("issueIds", issueIds);
		param.put("siteId", siteId);
		EsPageBean<NhFloorIssue> esPageBean = floorService.getFloorSearchList(param, 1, 999999);
		return esPageBean.getItems();
	}
	

}
