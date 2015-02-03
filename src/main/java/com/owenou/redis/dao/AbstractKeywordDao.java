package com.owenou.redis.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import com.owenou.redis.model.Keyword;
import com.owenou.redis.model.KeywordKey;

public abstract class AbstractKeywordDao {
	protected JedisPool pool;

	public AbstractKeywordDao(JedisPool pool) {
		this.pool = pool;
	}

	public abstract void save(List<Keyword> keywords);

	public abstract List<Keyword> paginate(String marketplaceId,
			String advertiserId, String sortByField, boolean asc, int start,
			int length);

	public List<Keyword> getAll(String marketplaceId, String advertiserId,
			Collection<String> ids) {
		List<Response<Map<String, String>>> respList = new ArrayList<>(
				ids.size());
		try (Jedis jedis = pool.getResource()) {
			// get all by ids
			Pipeline pipelined = jedis.pipelined();
			for (String id : ids) {
				KeywordKey k = new KeywordKey(marketplaceId, advertiserId, id);
				Response<Map<String, String>> resp = pipelined.hgetAll(k
						.getKey());
				respList.add(resp);
			}
			pipelined.sync();
		}

		// construct models
		List<Keyword> result = new ArrayList<>(ids.size());
		for (Response<Map<String, String>> resp : respList) {
			Map<String, String> data = resp.get();

			Keyword keyword = new Keyword(marketplaceId, advertiserId,
					data.get("id"));
			keyword.setName(data.get("name"));
			keyword.setStatus(data.get("status"));
			keyword.setBid(Float.valueOf(data.get("bid")));
			keyword.setImpression(Integer.valueOf(data.get("impression")));
			keyword.setClicks(Integer.valueOf(data.get("clicks")));
			keyword.setCpc(Integer.valueOf(data.get("cpc")));
			keyword.setSpend(Float.valueOf(data.get("spend")));
			keyword.setSales(Float.valueOf(data.get("sales")));
			keyword.setAcos(Float.valueOf(data.get("acos")));

			result.add(keyword);
		}

		return result;
	}
}
