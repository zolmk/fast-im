package com.feiyu.core.cache.impl;

import com.feiyu.core.cache.Cache;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.HostAndPortMapper;
import redis.clients.jedis.JedisCluster;


import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhufeifei 2023/11/11
 **/

public class RedisCacheImpl implements Cache {

    public RedisCacheImpl(Properties properties) {
        Set<HostAndPort> hostAndPortSet = new HashSet<>();

        hostAndPortSet.add(HostAndPort.from("111.230.15.9:6379"));
        hostAndPortSet.add(HostAndPort.from("111.230.15.9:6380"));
        hostAndPortSet.add(HostAndPort.from("111.230.15.9:6381"));
        DefaultJedisClientConfig.Builder builder = DefaultJedisClientConfig.builder().password("123456").hostAndPortMapper(new HostAndPortMapper() {
            @Override
            public HostAndPort getHostAndPort(HostAndPort hostAndPort) {
                return new HostAndPort("111.230.15.9", hostAndPort.getPort());
            }
        });
        this.cluster = new JedisCluster(hostAndPortSet, builder.build());
    }

    private final JedisCluster cluster;
    private final Map<String, BloomFilter> bfMap = new ConcurrentHashMap<>();


    @Override
    public boolean hasRecord(String key, String val) {
        BloomFilter bf = getBF(key);
        return bf.hasRecord(val);
    }

    @Override
    public void record(String key, String val) {
        BloomFilter bf = getBF(key);
        bf.record(val);
    }

    @Override
    public String getVal(String key) {
        return this.cluster.get(key);
    }

    @Override
    public void setVal(String key, String val) {
        this.cluster.set(key, val);
    }

    @Override
    public void close() throws IOException {
        this.cluster.close();
        this.bfMap.clear();
    }

    private BloomFilter createDefaultBF(String key) {
        return new BloomFilter(100000, key);
    }

    private BloomFilter getBF(String key) {
        BloomFilter bf = this.bfMap.getOrDefault(key, null);
        if (bf == null) {
            bf = createDefaultBF(key);
            this.bfMap.putIfAbsent(key, bf);
            bf = this.bfMap.get(key);
        }
        return bf;
    }

    public class BloomFilter {
        private static final String PREFIX = "bf_";
        private static final int MAXIMUM_CAPACITY = 2 << 20;
        private final int capacity;
        private final String key;
        private final String resolveKey;
        private final RedisCacheImpl self;

        public BloomFilter(int capacity, String key) {
            // 计算容量，取2的倍数
            this.capacity = tableSizeFor(capacity);
            this.key = key;
            this.self = RedisCacheImpl.this;
            this.resolveKey = resolveKey(key);
            init();
        }

        private void init() {
            JedisCluster cluster = self.cluster;
            if (!cluster.exists(this.resolveKey)) {
                cluster.setbit(this.resolveKey, this.capacity, false);
            }
        }

        private String resolveKey(String key) {
            return PREFIX + key;
        }


        boolean hasRecord(String val) {
            int offset = calculatePos(val);
            JedisCluster cluster = self.cluster;
            return cluster.getbit(this.resolveKey, offset);
        }

        void record(String val) {
            int offset = calculatePos(val);
            JedisCluster cluster = self.cluster;
            cluster.setbit(this.resolveKey, offset, true);
        }


        private int tableSizeFor(int c) {
            int n = c - 1;
            n |= n >>> 1;
            n |= n >>> 2;
            n |= n >>> 4;
            n |= n >>> 8;
            n |= n >>> 16;
            return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
        }

        private int calculatePos(String val) {
            int hash = val.hashCode();
            hash = (hash >>> 16) ^ hash;
            return hash & (this.capacity - 1);
        }
    }
}
