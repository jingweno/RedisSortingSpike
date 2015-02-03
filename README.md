# RedisSortingSpike

A spike to investigate the performance of sorting and paging with [Redis](http://redis.io/).

## Size of testing data

The goal is to being able to sort and paginate 1,000,000 entities in Redis with reasonable performance.

## Approach 1: set and `SORT`

Approach 1 stores IDs in a [set](http://redis.io/commands#set) and use [sort](http://redis.io/commands/sort) for sorting and paging.
This approach uses less memory (328MB) but is quite slow (30.24s).
The slowness comes from sorting 1 million records in memory.

## Approach 2: sorted set

Approach 2 builds and stores indexes in a [sorted set](http://redis.io/commands#sorted_set) for each entity field.
This approach uses more memory (484MB with selected fields) but is super fast (0.03s).
The extra memory consumption comes from storing indexes for each entity field.

## Conclusion

Approach 2 will use almost double the memory as approach 1 in production but it offers reasonable latency that end users will expect.
This is a typical example of [space-time tradeoff](http://en.wikipedia.org/wiki/Space%E2%80%93time_tradeoff).
In production, we can use Redis as [an LRU cache](http://redis.io/topics/lru-cache) so that we control its memory consumption.
