package com.owenou.redis.dao;

import java.util.ArrayList;
import java.util.List;

import com.owenou.redis.model.Keyword;

public class KeywordGenerator {
	public static List<Keyword> generate(int size) {
		List<Keyword> keywords = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			Keyword keyword = new Keyword("1", "1", String.valueOf(i));
			keyword.setName("name" + i);
			keyword.setStatus(i % 2 == 0 ? "running" : "suspended");
			keyword.setBid(Float.valueOf(i));
			keyword.setImpression(i);
			keyword.setClicks(i);
			keyword.setCpc(i);
			keyword.setSpend(Float.valueOf(i));
			keyword.setSales(Float.valueOf(i));
			keyword.setAcos(Float.valueOf(i));

			keywords.add(keyword);
		}

		return keywords;
	}
}
