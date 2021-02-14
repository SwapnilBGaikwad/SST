package com.github.sst.config;

import com.github.sst.Database;
import com.github.sst.file.FileHandler;
import com.github.sst.index.Index;
import com.github.sst.mem.MigrationCache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.io.File;
import java.util.*;

public class DbModule extends AbstractModule {
    private final static String dataFolder = "/Users/swapnil.gaikwad/projects/SST/data/";

    protected void configure() {
    }

    @Provides
    public Database getDB(MigrationCache migrationCache) {
        try {
            return new Database(migrationCache, loadIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Index> loadIndex() {
        File folder = new File(dataFolder);
        File[] files = folder.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        Arrays.sort(files, (a,b) -> {
            return a.getName().compareTo(b.getName());
        });
        LinkedList<Index> indices = new LinkedList<>();
        for (File file : files) {
            if (file.isFile()) {
                FileHandler fileHandler = new FileHandler(file.getName());
                indices.addFirst(new Index(fileHandler));
                fileHandler.close();
            }
        }
        return indices;
    }
}
