package com.github.sst.mem;

import java.util.TreeMap;

public class MemCache {
    public TreeMap<String, String> avlTree = new TreeMap<>();
    private final static int MAX = 160000;

    public String read(String key) {
        return avlTree.get(key);
    }

    public boolean write(String key, String value) {
        if (avlTree.size() == MAX) {
            return false;
        }
        avlTree.put(key, value);
        return true;
    }

    public TreeMap<String, String> getAvlTree() {
        return new TreeMap<>(avlTree);
    }
}
