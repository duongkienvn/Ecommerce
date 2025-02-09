package com.project.shopapp.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IBaseRedisService<K, F, V> {
    void set(K key, V value);
    void setTimeToLive(K key, long timeoutInDays);
    void hashSet(K key, F field, V value);
    boolean hasExists(K key, F field);
    V get(K key);
    Map<F, V> getField(K key);
    V hashGet(K key, F field);
    List<V> hashGetByFieldPrefix(K key, String fieldPrefix);
    Set<F> getFieldPrefixes(K key);
    void delete(K key);
    void delete(K key, F field);
    void delete(K key, List<F> fields);
}
