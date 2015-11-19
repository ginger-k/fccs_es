package com.fccs.es_api.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NhFloorIssue implements Serializable {

	private static final long serialVersionUID = -5178539299340665151L;
	private int issueId;
	private String photo;
	private String url;
	private String floor;
	private String address;
	private String phone;
	private String area;
	private String areaId;
	private int sellSchedule;
	private int sellSchedule1;
	private int sellSchedule2;
	private String company;
	private int companyId;
	private double appraiseScore;
	private int appraiseCount;
	private int photoCount;
	private int modelCount;
	private int houseCount;
	private int groupBuyCount;
	private String feature;
	private String houseUse;
	private int houseUseId;
	private double mapX;
	private double mapY;
	private Date openQuotation;
	private Date shellOut;
	private Date updateTime;
	private List<Map<String, Object>> newsList;
	private List<Map<String, Object>> priceList;
	private List<Map<String, Object>> houseList;
	private List<Map<String, Object>> modelList;
	private String saleInfo;
	private String saleInfoUrl;
	private int fccsMoney;
	private double fccsMoneyPrice;
	private String fccsMoneyUnit;
	private int fccsMoneyProjectId;
	private Map<String, Object> other;
	

	public List<Map<String, Object>> getModelList() {
		return modelList;
	}

	public void setModelList(List<Map<String, Object>> modelList) {
		this.modelList = modelList;
	}

	public String getSaleInfo() {
		return saleInfo;
	}

	public void setSaleInfo(String saleInfo) {
		this.saleInfo = saleInfo;
	}

	public String getSaleInfoUrl() {
		return saleInfoUrl;
	}

	public void setSaleInfoUrl(String saleInfoUrl) {
		this.saleInfoUrl = saleInfoUrl;
	}

	public int getFccsMoney() {
		return fccsMoney;
	}

	public void setFccsMoney(int fccsMoney) {
		this.fccsMoney = fccsMoney;
	}

	public double getFccsMoneyPrice() {
		return fccsMoneyPrice;
	}

	public void setFccsMoneyPrice(double fccsMoneyPrice) {
		this.fccsMoneyPrice = fccsMoneyPrice;
	}

	public String getFccsMoneyUnit() {
		return fccsMoneyUnit;
	}

	public void setFccsMoneyUnit(String fccsMoneyUnit) {
		this.fccsMoneyUnit = fccsMoneyUnit;
	}

	public int getFccsMoneyProjectId() {
		return fccsMoneyProjectId;
	}

	public void setFccsMoneyProjectId(int fccsMoneyProjectId) {
		this.fccsMoneyProjectId = fccsMoneyProjectId;
	}

	public List<Map<String, Object>> getHouseList() {
		return houseList;
	}

	public void setHouseList(List<Map<String, Object>> houseList) {
		this.houseList = houseList;
	}

	public Map<String, Object> getOther() {
		return other;
	}

	public void setOther(Map<String, Object> other) {
		this.other = other;
	}

	public List<Map<String, Object>> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<Map<String, Object>> priceList) {
		this.priceList = priceList;
	}

	public int getHouseUseId() {
		return houseUseId;
	}

	public void setHouseUseId(int houseUseId) {
		this.houseUseId = houseUseId;
	}

	public String getHouseUse() {
		return houseUse;
	}

	public void setHouseUse(String houseUse) {
		this.houseUse = houseUse;
	}

	public List<Map<String, Object>> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<Map<String, Object>> newsList) {
		this.newsList = newsList;
	}

	public int getIssueId() {
		return issueId;
	}

	public void setIssueId(int issueId) {
		this.issueId = issueId;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public int getSellSchedule() {
		return sellSchedule;
	}

	public void setSellSchedule(int sellSchedule) {
		this.sellSchedule = sellSchedule;
	}

	public int getSellSchedule1() {
		return sellSchedule1;
	}

	public void setSellSchedule1(int sellSchedule1) {
		this.sellSchedule1 = sellSchedule1;
	}

	public int getSellSchedule2() {
		return sellSchedule2;
	}

	public void setSellSchedule2(int sellSchedule2) {
		this.sellSchedule2 = sellSchedule2;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public double getAppraiseScore() {
		return appraiseScore;
	}

	public void setAppraiseScore(double appraiseScore) {
		this.appraiseScore = appraiseScore;
	}

	public int getAppraiseCount() {
		return appraiseCount;
	}

	public void setAppraiseCount(int appraiseCount) {
		this.appraiseCount = appraiseCount;
	}

	public int getPhotoCount() {
		return photoCount;
	}

	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}

	public int getModelCount() {
		return modelCount;
	}

	public void setModelCount(int modelCount) {
		this.modelCount = modelCount;
	}

	public int getHouseCount() {
		return houseCount;
	}

	public void setHouseCount(int houseCount) {
		this.houseCount = houseCount;
	}

	public int getGroupBuyCount() {
		return groupBuyCount;
	}

	public void setGroupBuyCount(int groupBuyCount) {
		this.groupBuyCount = groupBuyCount;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public double getMapX() {
		return mapX;
	}

	public void setMapX(double mapX) {
		this.mapX = mapX;
	}

	public double getMapY() {
		return mapY;
	}

	public void setMapY(double mapY) {
		this.mapY = mapY;
	}

	public Date getOpenQuotation() {
		return openQuotation;
	}

	public void setOpenQuotation(Date openQuotation) {
		this.openQuotation = openQuotation;
	}

	public Date getShellOut() {
		return shellOut;
	}

	public void setShellOut(Date shellOut) {
		this.shellOut = shellOut;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
