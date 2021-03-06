package com.owenou.redis.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.owenou.redis.model.Keyword;

public class KeywordDaoTest {
	@Rule
	public TestRule benchmarkRun = new BenchmarkRule();

	private static JedisPool pool;

	private static KeywordDao keywordDao;

	@BeforeClass
	public static void setUp() {
		pool = new JedisPool(new JedisPoolConfig(), "localhost",
				Protocol.DEFAULT_PORT, 10000);
		keywordDao = new KeywordDao(pool);

		flushDB();
		createTestData(1000000);
	}

	private static void flushDB() {
		try (Jedis jedis = pool.getResource()) {
			System.out.println(jedis.info("memory"));
			jedis.flushDB();
		}
	}

	private static void createTestData(int size) {
		List<Keyword> keywords = KeywordGenerator.generate(size);
		keywordDao.save(keywords);
	}

	@AfterClass
	public static void tearDown() {
		try {
			flushDB();
		} finally {
			pool.close();
		}
	}

	// KeywordDaoTest.paginate: [measured 10 out of 15 rounds, threads: 1
	// (sequential)]
	// round: 1.98 [+- 0.07], round.block: 0.00 [+- 0.00], round.gc: 0.00 [+-
	// 0.00], GC.calls: 0, GC.time: 0.00, time.total: 30.24, time.warmup: 10.49,
	// time.bench: 19.75
	//
	// used_memory:344707632
	// used_memory_human:328.74M
	// used_memory_rss:546852864
	// used_memory_peak:2232386720
	// used_memory_peak_human:2.08G
	// used_memory_lua:35840
	// mem_fragmentation_ratio:1.59
	// mem_allocator:libc
	@Test
	public void paginate() {
		List<Keyword> keywords = keywordDao.paginate("1", "1", "impression",
				false, 0, 25);
		assertEquals(25, keywords.size());
	}
}
