package com.owenou.redis.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;

import com.google.common.collect.Lists;
import com.owenou.redis.model.Keyword;
import com.owenou.redis.model.KeywordKey;

public class KeywordDao {
	private JedisPool pool;

	public KeywordDao(JedisPool pool) {
		this.pool = pool;
	}

	public void save(List<Keyword> keywords) {
		try (Jedis jedis = pool.getResource()) {
			List<List<Keyword>> partition = Lists.partition(keywords, 1000);
			for (List<Keyword> p : partition) {
				Transaction multi = jedis.multi();

				for (Keyword k : p) {
					Map<String, String> data = new HashMap<>();
					data.put("name", k.getName());
					data.put("status", k.getStatus());
					data.put("bid", Float.valueOf(k.getBid()).toString());
					data.put("impression", String.valueOf(k.getImpression()));
					data.put("clicks", String.valueOf(k.getClicks()));
					data.put("cpc", String.valueOf(k.getCpc()));
					data.put("spend", Float.valueOf(k.getSpend()).toString());
					data.put("sales", Float.valueOf(k.getSales()).toString());
					data.put("acos", Float.valueOf(k.getAcos()).toString());

					multi.hmset(k.getKey().getKey(), data);
					multi.sadd(k.getKey().getParentKey(), k.getKey().getId());
				}

				multi.exec();
			}
		}
	}

	public List<Keyword> paginate(String marketplaceId, String advertiserId,
			String sortByField, boolean asc, int start, int length) {
		List<Keyword> result = new ArrayList<>(length);

		try (Jedis jedis = pool.getResource()) {
			// sort and paginate
			KeywordKey key = new KeywordKey(marketplaceId, advertiserId, "*");
			SortingParams sortingParams = new SortingParams();
			String sortBy = key.getKey() + "->" + sortByField;
			sortingParams.by(sortBy);
			if (asc) {
				sortingParams.asc();
			} else {
				sortingParams.desc();
			}
			sortingParams.limit(start, length);
			List<String> ids = jedis.sort(key.getParentKey(), sortingParams);

			// get back result
			Map<String, Response<Map<String, String>>> respMap = new HashMap<>(
					length);
			Pipeline pipelined = jedis.pipelined();
			for (String id : ids) {
				KeywordKey k = new KeywordKey(marketplaceId, advertiserId, id);
				Response<Map<String, String>> resp = pipelined.hgetAll(k
						.getKey());
				respMap.put(id, resp);
			}
			pipelined.sync();

			// construct model
			for (Entry<String, Response<Map<String, String>>> entry : respMap
					.entrySet()) {
				String id = entry.getKey();
				Map<String, String> data = entry.getValue().get();

				Keyword keyword = new Keyword(marketplaceId, advertiserId, id);
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
		}

		return result;
	}
}
