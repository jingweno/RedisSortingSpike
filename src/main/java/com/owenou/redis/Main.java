package com.owenou.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.SortingParams;

public class Main {
	public static void main(String[] args) {
		try (JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost")) {
			try (Jedis jedis = pool.getResource()) {
				jedis.flushDB();

				Pipeline pipelined = jedis.pipelined();

				for (int i = 0; i < 100; i++) {
					Map<String, String> data = new HashMap<>();
					data.put("name", "name" + i);
					data.put("status", i % 2 == 0 ? "running" : "suspended");
					data.put("bid", Float.valueOf(i).toString());
					data.put("impression", String.valueOf(i));
					data.put("clicks", String.valueOf(i));
					data.put("cpc", String.valueOf(i));
					data.put("spend", Float.valueOf(i).toString());
					data.put("sales", Float.valueOf(i).toString());
					data.put("acos", Float.valueOf(i).toString());

					pipelined.hmset("keyword:" + i, data);
					pipelined.sadd("keywords", String.valueOf(i));
				}

				SortingParams sortingParams = new SortingParams();
				sortingParams.by("keyword:*->impression");
				sortingParams.limit(0, 25);

				Response<List<String>> result = pipelined.sort("keywords",
						sortingParams);

				pipelined.sync();

				for (String r : result.get()) {
					System.out.println(r);
				}
			}
		}
	}
}
