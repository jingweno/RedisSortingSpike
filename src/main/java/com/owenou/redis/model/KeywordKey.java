package com.owenou.redis.model;

public class KeywordKey {
	private String marketplaceId;
	private String advertiserId;
	private String id;

	public KeywordKey(String marketplaceId, String advertiserId, String id) {
		this.marketplaceId = marketplaceId;
		this.advertiserId = advertiserId;
		this.id = id;
	}

	public String getKey() {
		return "marketplace:" + marketplaceId + ":advertiser:" + advertiserId
				+ ":keyword:" + id;
	}

	public String getParentKey() {
		return "marketplace:" + marketplaceId + ":advertiser:" + advertiserId
				+ ":keywords";
	}

	public String getMarketplaceId() {
		return marketplaceId;
	}

	public String getAdvertiserId() {
		return advertiserId;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return getKey();
	}
}
