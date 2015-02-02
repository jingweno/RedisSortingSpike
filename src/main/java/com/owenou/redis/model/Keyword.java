package com.owenou.redis.model;

public class Keyword {
	private String name;
	private String status;
	private float bid;
	private int impression;
	private int clicks;
	private int cpc;
	private float spend;
	private float sales;
	private float acos;

	private KeywordKey key;

	public Keyword(String marketplaceId, String advertiserId, String id) {
		key = new KeywordKey(marketplaceId, advertiserId, id);
	}

	@Override
	public String toString() {
		return key.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public float getBid() {
		return bid;
	}

	public void setBid(float bid) {
		this.bid = bid;
	}

	public int getImpression() {
		return impression;
	}

	public void setImpression(int impression) {
		this.impression = impression;
	}

	public int getClicks() {
		return clicks;
	}

	public void setClicks(int clicks) {
		this.clicks = clicks;
	}

	public int getCpc() {
		return cpc;
	}

	public void setCpc(int cpc) {
		this.cpc = cpc;
	}

	public float getSpend() {
		return spend;
	}

	public void setSpend(float spend) {
		this.spend = spend;
	}

	public float getSales() {
		return sales;
	}

	public void setSales(float sales) {
		this.sales = sales;
	}

	public float getAcos() {
		return acos;
	}

	public void setAcos(float acos) {
		this.acos = acos;
	}
	
	public KeywordKey getKey() {
		return key;
	}
}
