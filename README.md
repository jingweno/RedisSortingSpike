# RedisSortingSpike

A spike to investigate the performance of sorting and paging with [Redis](http://redis.io/).

## Requirement

* Redis 2.8.x
* Maven

## Running Test

```
$ mvn test
```

## Size of testing data

The goal is to being able to sort and paginate 1,000,000 entities in Redis with reasonable performance.

## Approach 1: set and `SORT`

[Approach 1](https://github.com/jingweno/RedisSortingSpike/blob/master/src/main/java/com/owenou/redis/dao/KeywordDao.java) stores IDs in a [set](http://redis.io/commands#set) and use [sort](http://redis.io/commands/sort) for sorting and paging.
This approach uses less memory (328MB) but is quite slow (30.24s).
The slowness comes from sorting 1 million records in memory.
The test is [here](https://github.com/jingweno/RedisSortingSpike/blob/master/src/test/java/com/owenou/redis/dao/KeywordDaoTest.java#L75).

## Approach 2: sorted set

[Approach 2](https://github.com/jingweno/RedisSortingSpike/blob/master/src/main/java/com/owenou/redis/dao/KeywordDaoImproved.java) builds and stores indexes in a [sorted set](http://redis.io/commands#sorted_set) for each entity field.
This approach uses more memory (484MB with selected fields) but is super fast (0.03s).
The extra memory consumption comes from storing indexes for each entity field.
The test is [here](https://github.com/jingweno/RedisSortingSpike/blob/master/src/test/java/com/owenou/redis/dao/KeywordDaoImprovedTest.java#L76).

## Conclusion

Approach 2 will use almost double the memory as approach 1 in production but it offers excellent latency that end users will expect
This is a typical example of [space-time tradeoff](http://en.wikipedia.org/wiki/Space%E2%80%93time_tradeoff).
In production, Redis can be used as [an LRU cache](http://redis.io/topics/lru-cache) so that memory consumption is under control.
