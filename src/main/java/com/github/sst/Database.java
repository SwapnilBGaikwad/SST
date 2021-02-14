package com.github.sst;

import com.github.sst.index.Index;
import com.github.sst.mem.MemCache;
import com.github.sst.mem.MigrationCache;

import java.util.List;

public class Database {
    private MemCache memCache;
    private final List<Index> indices;
    private final MigrationCache migrationCache;

    public Database(MigrationCache migrationCache, List<Index> indices) {
        this.migrationCache = migrationCache;
        this.memCache = new MemCache();
        this.indices = indices;
    }

    public void write(String key, String value) {
        boolean successful = memCache.write(key, value);
        if (successful) {
            return;
        }
        migrationCache.startMigration(memCache.getAvlTree(), (index -> {
            synchronized (indices) {
                indices.add(index);
            }
        }));
        memCache = new MemCache();
        memCache.write(key, value);
    }

    public String read(String key) {
        String val = memCache.read(key);
        if (val != null) {
            return val;
        }
        val = migrationCache.read(key);
        if (val != null) {
            return val;
        }
        return readIndex(key);
    }

    public void close() throws InterruptedException {
        for (int i = 0; i < indices.size(); i++) {
            indices.get(i).close();
        }
        migrationCache.shutDown();
    }

    private String readIndex(String key) {
        for (int i = 0; i < indices.size(); i++) {
            String value = indices.get(i).read(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
