package com.owenou.redis.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;

import com.google.common.collect.Lists;
import com.owenou.redis.model.Keyword;
import com.owenou.redis.model.KeywordKey;

public class KeywordDao extends AbstractKeywordDao {
	public KeywordDao(JedisPool pool) {
		super(pool);
	}

	@Override
	public void save(List<Keyword> keywords) {
		try (Jedis jedis = pool.getResource()) {
			List<List<Keyword>> partition = Lists.partition(keywords, 1000);
			for (List<Keyword> p : partition) {
				Transaction multi = jedis.multi();

				for (Keyword k : p) {
					Map<String, String> data = new HashMap<>();
					data.put("id", k.getId());
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

	@Override
	public List<Keyword> paginate(String marketplaceId, String advertiserId,
			String sortByField, boolean asc, int start, int length) {
		List<String> ids = null;

		try (Jedis jedis = pool.getResource()) {
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
			ids = jedis.sort(key.getParentKey(), sortingParams);
		}

		return getAll(marketplaceId, advertiserId, ids);
	}
}
