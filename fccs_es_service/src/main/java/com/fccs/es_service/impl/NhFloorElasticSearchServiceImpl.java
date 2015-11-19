package com.fccs.es_service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Type;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.fccs.es_api.bean.NhFloorIssue;
import com.fccs.es_api.exception.EsException;
import com.fccs.es_api.service.NhFloorElasticSearchService;
import com.fccs.es_api.vo.EsPageBean;
import com.fccs.es_service.util.ElasticSearchUtil;
import com.fccs.es_service.util.MapUtil;
import com.fccs.es_service.util.StringUtil;

public class NhFloorElasticSearchServiceImpl implements NhFloorElasticSearchService {

	@Override
	public EsPageBean<NhFloorIssue> getFloorSearchList(Map<String, Object> conditions, int pageNow, int pageSize) throws EsException {
		if (pageNow <= 0 || pageSize <= 0) {
			throw new EsException("-------> 参数有错误 <-------");
		}
		Client client = ElasticSearchUtil.getClient();
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch("oracle_fccs").setTypes("floor");
		try {
			this.setQuery(searchRequestBuilder, conditions);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.addSort(searchRequestBuilder, conditions);
		int esFrom = pageSize * (pageNow - 1);
		//添加高亮
		searchRequestBuilder.addHighlightedField("floor");
		searchRequestBuilder.setHighlighterPreTags("<font color='red'>");
		searchRequestBuilder.setHighlighterPostTags("</font>");
		SearchResponse response = searchRequestBuilder.setFrom(esFrom).setSize(pageSize)
									.setExplain(true).execute().actionGet();
		SearchHits hits = response.getHits();
		List<NhFloorIssue> list = this.processBean(hits);
		long totalHits = hits.getTotalHits();
		int totalRecord = Integer.valueOf(String.valueOf(totalHits));
		int totalPage = (totalRecord - 1)/pageSize + 1;
		return new EsPageBean<NhFloorIssue>(pageSize, pageNow, totalPage, totalRecord, list);
	}
	
	
	private void setQuery(SearchRequestBuilder searchRequestBuilder, Map<String, Object> conditions) throws ParseException {
		int siteId = MapUtil.toInt(conditions, "siteId");
		String areaId = MapUtil.toString(conditions, "areaId");
		String feature = MapUtil.toString(conditions, "feature");
		String spell = MapUtil.toString(conditions, "spell");
		String school = MapUtil.toString(conditions, "school");
		int gradeSchoolId = MapUtil.toInt(conditions, "gradeSchoolId");
		int highSchoolId = MapUtil.toInt(conditions, "highSchoolId");
		int houseUseId = MapUtil.toInt(conditions, "houseUseId");
		int buildingTypeId = MapUtil.toInt(conditions, "buildingTypeId");
		int priceLow = MapUtil.toInt(conditions, "priceLow", -1);
		int priceHigh = MapUtil.toInt(conditions, "priceHigh", -1);
		int estate = MapUtil.toInt(conditions, "estate");
		String keyword = MapUtil.toString(conditions, "keyword");
		int fccsMoney = MapUtil.toInt(conditions, "fccsMoney", -1);
		String openQuotationBegin = (String) conditions.get("openQuotationBegin");
		String openQuotationEnd = (String) conditions.get("openQuotationEnd");
		String schoolUpdateYear = (String) conditions.get("schoolUpdateYear");
		double mapX1 = MapUtil.toDouble(conditions, "mapX1", -1);
		double mapX2 = MapUtil.toDouble(conditions, "mapX2", -1);
		double mapY1 = MapUtil.toDouble(conditions, "mapY1", -1);
		double mapY2 = MapUtil.toDouble(conditions, "mapY2", -1);
		int sellSchedule = MapUtil.toInt(conditions, "sellSchedule");
		String sellSchedules = MapUtil.toString(conditions, "sellSchedules");
		int issueId = MapUtil.toInt(conditions, "issueId");
		String issueIds = MapUtil.toString(conditions, "issueIds");
		String fletter = MapUtil.toString(conditions, "fletter");
		String subwayId = MapUtil.toString(conditions, "subwayId");
		int sale = MapUtil.toInt(conditions, "sale", -1);
		int houseAreaBegin = MapUtil.toInt(conditions, "houseAreaBegin", -1);
		int houseAreaEnd = MapUtil.toInt(conditions, "houseAreaEnd", -1);
		int mapExist = MapUtil.toInt(conditions, "mapExist", -1);
		 
		BoolQueryBuilder queryTotal = QueryBuilders.boolQuery();

		// siteId
		QueryBuilder querySiteId = QueryBuilders.termQuery("siteId", siteId);
		queryTotal.must(querySiteId);

		// areaId
		if (StringUtil.hasText(areaId)) {
			areaId = StringUtils.substringBeforeLast(areaId, ".");
			QueryBuilder queryAreaId = QueryBuilders.regexpQuery("areaId", areaId + "[0-9]");
			queryTotal.mustNot(queryAreaId);
			QueryBuilder queryAreaId2 = QueryBuilders.matchQuery("areaId", areaId).type(Type.PHRASE_PREFIX);
			queryTotal.must(queryAreaId2);
		}

		// feature
		if (StringUtil.hasText(feature)) {
			QueryBuilder queryFeature = QueryBuilders.matchPhraseQuery("feature", feature);
			queryTotal.must(queryFeature);
		}

		// school
		if (StringUtil.hasText(school)) {
			BoolQueryBuilder querySchool = new BoolQueryBuilder();
			// gradeSchool
			QueryBuilder queryK1 = QueryBuilders.matchPhraseQuery("gradeSchool", school);
			querySchool.should(queryK1);
			// highSchool
			QueryBuilder queryK2 = QueryBuilders.matchPhraseQuery("highSchool", school);
			querySchool.should(queryK2);
			queryTotal.must(querySchool);
		}

		// gradeSchoolId
		if (gradeSchoolId > 0) {
			QueryBuilder queryGradeSchoolId = QueryBuilders.termQuery("gradeSchoolId", gradeSchoolId);
			queryTotal.must(queryGradeSchoolId);
		}
		
		// existMap
		if (mapExist == 0 || mapExist == 1) {
			BoolQueryBuilder existMapQuery = QueryBuilders.boolQuery();
			QueryBuilder mapxQuery = QueryBuilders.termQuery("mapX", 0);
			QueryBuilder mapyQuery = QueryBuilders.termQuery("mapY", 0);
			if (mapExist == 1) 	
				existMapQuery.mustNot(mapxQuery).mustNot(mapyQuery);
			else
				existMapQuery.must(mapxQuery).must(mapyQuery);
			queryTotal.must(existMapQuery);
		}

		// highSchoolId
		if (highSchoolId > 0) {
			QueryBuilder queryHighSchoolId = QueryBuilders.termQuery("highSchoolId", highSchoolId);
			queryTotal.must(queryHighSchoolId);
		}

		// houseUseId
		if (houseUseId > 0) {
			if (houseUseId == 9999) {
				TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("priceList.houseUseId", new int[]{10,11,12,13,208,1440});
				queryTotal.must(termsQuery);
			} else {
				QueryBuilder queryHouseUseId = QueryBuilders.termQuery("priceList.houseUseId", houseUseId);
				queryTotal.must(queryHouseUseId);
			}
		}

		// buildingTypeId
		if (buildingTypeId > 0) {
			QueryBuilder queryBuildingTypeId = QueryBuilders.termQuery("buildingTypeId", buildingTypeId);
			queryTotal.must(queryBuildingTypeId);
		}

		
		if (priceLow >= 0 || priceHigh >= 0) {
			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("priceList.priceSearch");
			if (priceLow >= 0) {
				rangeQueryBuilder.gte(priceLow);
			}
			if (priceHigh >= 0) {
				rangeQueryBuilder.lte(priceHigh);
				if (priceLow < 0)
					rangeQueryBuilder.gte(0);
			}
			queryTotal.must(rangeQueryBuilder);
		}

		// estate
		if (estate > 0) {
			QueryBuilder queryEstate = QueryBuilders.termQuery("estate", estate);
			queryTotal.must(queryEstate);
		}

		// keyword
		if (StringUtil.hasText(keyword)) {
			BoolQueryBuilder queryKeyword = new BoolQueryBuilder();
			// floor
			QueryBuilder queryK1 = QueryBuilders.matchPhraseQuery("floor", keyword);
			queryKeyword.should(queryK1);
			// address
			QueryBuilder queryK2 = QueryBuilders.matchPhraseQuery("address", keyword);
			queryKeyword.should(queryK2);
			// company
			QueryBuilder queryK3 = QueryBuilders.matchPhraseQuery("company", keyword);
			queryKeyword.should(queryK3);
			//spell
			String pinYin = StringUtil.getPinYin(keyword);
			RegexpQueryBuilder regexpQuery1 = QueryBuilders.regexpQuery("spell", "[a-zA-Z]*" + pinYin + "[a-zA-Z]*");
			queryKeyword.should(regexpQuery1);
			queryTotal.must(queryKeyword);
		}
		
		//fletter
		if (StringUtils.isNotBlank(fletter)) {
			QueryBuilder queryBuilder= QueryBuilders.regexpQuery("fletter", "[a-zA-Z]?" + fletter + "[a-zA-Z]?");
			queryTotal.must(queryBuilder);
		}
		
		//subwayId
		if (StringUtils.isNotBlank(subwayId)) {
			String sbid = subwayId.replace("-", "_");
			StringBuilder sb = new StringBuilder();
			if (StringUtils.endsWith(sbid, "_")) {
				sb.append(sbid).append("[0-9_,]*|").append("[0-9,_]*,").append(sbid).append("[0-9,_]*");
			} else {
				sb.append(sbid).append("|").append(sbid).append(",[0-9,_]*|[0-9,_]*,").append(sbid).append("|[0-9,_]*,").append(sbid).append(",[0-9,_]*");
			}
			QueryBuilder queryBuilder= QueryBuilders.regexpQuery("subwayId", sb.toString());
			queryTotal.must(queryBuilder);
		}

		//mapX,mapY
		if (mapX1 >= 0 || mapX2 >= 0) {
			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("mapX");
			if (mapX1 >= 0) {
				rangeQueryBuilder.gte(mapX1);
			}
			if (mapX2 >= 0) {
				rangeQueryBuilder.lte(mapX2);
			}
			queryTotal.must(rangeQueryBuilder);
		}
		if (mapY1 >= 0 || mapY2 >= 0) {
			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("mapY");
			if (mapY1 >= 0) {
				rangeQueryBuilder.gte(mapY1);
			}
			if (mapY2 >= 0) {
				rangeQueryBuilder.lte(mapY2);
			}
			queryTotal.must(rangeQueryBuilder);
		}
		
		
		//sale
		if (sale ==0 || sale ==1) {
			TermQueryBuilder termQuery = QueryBuilders.termQuery("sale", sale);
			queryTotal.must(termQuery);
		}
		
		//houseAreaBegin,houseAreaEnd
		if (houseAreaBegin >= 0 || houseAreaEnd >=0) {
			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			RangeQueryBuilder queryBuilder1 = QueryBuilders.rangeQuery("modelList.houseArea");
			RangeQueryBuilder queryBuilder2 = QueryBuilders.rangeQuery("modelList.houseArea1");
			if ((houseAreaBegin == 0 ? houseAreaBegin=houseAreaBegin+1 : houseAreaBegin) > 0) {
				queryBuilder1.gte(houseAreaBegin);
				queryBuilder2.gte(houseAreaBegin);
			}
			if ((houseAreaEnd == 0 ? houseAreaEnd=houseAreaEnd+1 : houseAreaEnd) > 0) {
				queryBuilder1.lt(houseAreaEnd);
				queryBuilder2.lt(houseAreaEnd);
			}
			boolQuery.should(queryBuilder1).should(queryBuilder2);
			queryTotal.must(boolQuery);
		}
		
		// spell
		if (StringUtil.hasText(spell)) {
			BoolQueryBuilder querySpell = new BoolQueryBuilder();
			// spell
			QueryBuilder queryK1 = QueryBuilders.regexpQuery("spell", "[a-zA-Z]*" + spell + "[a-zA-Z]*");
			querySpell.should(queryK1);
			// spellFull
			QueryBuilder queryK2 = QueryBuilders.regexpQuery("spellFull", "[a-zA-Z]*" + spell + "[a-zA-Z]*");
			querySpell.should(queryK2);
			queryTotal.must(querySpell);
		}
		
		//openQuotationBegin, openQuotationEnd
		if (openQuotationBegin != null || openQuotationEnd != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("openQuotationNumber");
			if (StringUtils.isNoneBlank(openQuotationBegin)) {
				String dateBegin = dateFormat.format((dateFormat.parse(openQuotationBegin)));
				rangeQueryBuilder.gte(dateBegin.replace("-", ""));
			}
			if (StringUtils.isNotBlank(openQuotationEnd)) {
				String dateEnd = dateFormat.format((dateFormat.parse(openQuotationEnd)));
				rangeQueryBuilder.lte(dateEnd.replace("-", ""));
			}
			queryTotal.must(rangeQueryBuilder);
		}
		
		//schoolUpdateYear
		if (StringUtils.isNoneBlank(schoolUpdateYear)) {
			TermQueryBuilder termQuery = QueryBuilders.termQuery("schoolUpdateYear", schoolUpdateYear);
			queryTotal.must(termQuery);
		}
		
		// fccsMoney
		if (fccsMoney == 0 || fccsMoney == 1) {
			QueryBuilder queryFccsMoney = QueryBuilders.termQuery("fccsMoney", fccsMoney);
			queryTotal.must(queryFccsMoney);
		}
		
		//sellSchedule
		if (sellSchedule > 0 && sellSchedule < 5) {
			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			TermQueryBuilder termQuery = QueryBuilders.termQuery("sellSchedule", sellSchedule);
			TermQueryBuilder termQuery1 = QueryBuilders.termQuery("sellSchedule1", sellSchedule);
			TermQueryBuilder termQuery2 = QueryBuilders.termQuery("sellSchedule2", sellSchedule);
			boolQuery.should(termQuery).should(termQuery1).should(termQuery2);
			queryTotal.must(boolQuery);
		}
		
		//sellSchedules
		if (StringUtils.isNotBlank(sellSchedules)) {
			String[] sellScheduleArr = sellSchedules.split(",");
			if (sellScheduleArr != null && sellScheduleArr.length>0) {
				BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
				TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("sellSchedule", sellScheduleArr);
				TermsQueryBuilder termsQuery1 = QueryBuilders.termsQuery("sellSchedule1", sellScheduleArr);
				TermsQueryBuilder termsQuery2 = QueryBuilders.termsQuery("sellSchedule2", sellScheduleArr);
				boolQuery.should(termsQuery).should(termsQuery1).should(termsQuery2);
				queryTotal.must(boolQuery);
			}
		}
		
		// issueId
		if (issueId > 0) {
			TermQueryBuilder termQuery = QueryBuilders.termQuery("issueId", issueId);
			queryTotal.must(termQuery);
		}

		//issueIds
		if (StringUtils.isNotBlank(issueIds)) {
			String[] issueIdsArr = issueIds.split(",");
			if (issueIdsArr != null && issueIdsArr.length>0) {
				TermsQueryBuilder termsQuery = QueryBuilders.termsQuery("issueId", issueIdsArr);
				queryTotal.must(termsQuery);
			}
		}
		searchRequestBuilder.setQuery(queryTotal);
	}
	
	/*private void setFilter(SearchRequestBuilder searchRequestBuilder, Map<String, Object> map) {
		int count = 0;
		BoolFilterBuilder totalFilter = FilterBuilders.boolFilter();
		
		if (count > 0) {
			searchRequestBuilder.setPostFilter(totalFilter);
		}
	}*/
	
	private void addSort(SearchRequestBuilder searchRequestBuilder, Map<String, Object> map) {
		int order = MapUtil.toInt(map, "order");
		switch (order) {
			case 1 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("orderNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;
			case 2 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("adMoney").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;
			case 3 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;
			case 4 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("hits").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;
			case 41 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("hits").order(SortOrder.ASC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;
			case 5 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("shellOutNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;
			case 51 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("shellOutNumber").order(SortOrder.ASC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;
			case 6 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("openQuotationNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;
			case 61 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("openQuotationNumber").order(SortOrder.ASC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;
			case 7 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("totalPriceOrder").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;
			case 71 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("totalPriceOrder").order(SortOrder.ASC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;	
			case 8 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("priceOrder").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
			case 81 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("estate").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("priceOrder").order(SortOrder.ASC))
					.addSort(SortBuilders.fieldSort("updateTimeNumber").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
			case 9 : 
				searchRequestBuilder.addSort(SortBuilders.fieldSort("fccsMoneyState").order(SortOrder.ASC))
					.addSort(SortBuilders.fieldSort("fccsMoneyUpdateTime").order(SortOrder.DESC))
					.addSort(SortBuilders.fieldSort("issueId").order(SortOrder.DESC));
				break;
		}
	}
	
	private List<NhFloorIssue> processBean(SearchHits hits) {
		List<NhFloorIssue> list1 = new ArrayList<NhFloorIssue>();
		List<NhFloorIssue> list2 = new ArrayList<NhFloorIssue>();
		//高亮处理，将每一页的数据，添加了高亮的，放到前面
		for (SearchHit hit : hits) {
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			if(highlightFields.size() > 0) {
				Map<String, Object> result = hit.sourceAsMap();
				//添加了高亮的字段，重新赋值
				for (Entry<String, HighlightField> highlightField : highlightFields.entrySet()) {
					result.put(highlightField.getKey(), ((Text[])highlightField.getValue().getFragments())[0].toString());
				}
				NhFloorIssue obj = this.innerProcess(result);
				list1.add(obj);
			} else {
				Map<String, Object> result = hit.sourceAsMap();
				NhFloorIssue obj = this.innerProcess(result);
				list2.add(obj);
			}
		}
		List<NhFloorIssue> list = new ArrayList<NhFloorIssue>();
		list.addAll(list1);
		list.addAll(list2);
		return list;
	}
	
	
	@SuppressWarnings("unchecked")
	private NhFloorIssue innerProcess(Map<String, Object> result) {
		NhFloorIssue obj = MapUtil.map2Bean(result, NhFloorIssue.class);
		// price list
		List<Map<String, Object>> priceList = (List<Map<String, Object>>) result.get("priceList");
		obj.setPriceList(priceList);
		// house list
		List<Map<String, Object>> houseList = (List<Map<String, Object>>) result.get("houseList");
		obj.setHouseList(houseList);
		// news list
		List<Map<String, Object>> newsList = (List<Map<String, Object>>) result.get("newsList");
		obj.setNewsList(newsList);
		//modelList
		List<Map<String, Object>> modelList = (List<Map<String, Object>>) result.get("modelList");
		obj.setModelList(modelList);
		// other
		Map<String, Object> other = (Map<String, Object>) result.get("other");
		if (other == null) {
			other = new HashMap<String, Object>();
		}
		// temp
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
		obj.setOther(other);
		return obj;
	}
}
