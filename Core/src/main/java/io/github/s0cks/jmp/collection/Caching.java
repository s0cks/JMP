package io.github.s0cks.jmp.collection;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Caching {
    public static final int MAX_SIZE = Integer.valueOf(System.getProperty("jmp.cacheSize", "127"));

    private Caching() {
    }

    public static interface Cache<K, V> extends Iterable<Map.Entry<K, V>> {
        public V get(K key);
        public V put(K key, V value);
        public int size();
        public boolean contains(K key);
    }

    public static <K, V> Cache<K, V> newLRU() {
        return new LRUCache<>(MAX_SIZE);
    }

    public static <K, V> Cache<K, V> newLRU(int size) {
        return new LRUCache<>(size);
    }

    private static final class LRUCache<K, V> extends LinkedHashMap<K, V> implements Cache<K, V> {
        private final int cap;

        private LRUCache(int cap) {
            super(cap + 1, 0.75F, true);
            this.cap = cap;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return this.size() > this.cap;
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return this.entrySet().iterator();
        }

        @Override
        public boolean contains(K key) {
            return this.containsKey(key);
        }
    }
}