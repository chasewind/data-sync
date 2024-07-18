package org.example.common.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 注意：为了降低依赖以及引用，该类是从javafx.util.Pair直接拷贝的
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> implements Serializable {

    private final K key;

    private final V value;


    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }


    public K getKey() {
        return key;
    }


    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }


    @Override
    public int hashCode() {

        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Pair) {
            Pair pair = (Pair) o;
            if (!Objects.equals(key, pair.key)) return false;
            return Objects.equals(value, pair.value);
        }
        return false;
    }
}

