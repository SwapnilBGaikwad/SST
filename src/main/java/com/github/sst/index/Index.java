package com.github.sst.index;

import com.github.sst.file.FileHandler;
import javafx.util.Pair;

import java.util.TreeMap;

public class Index {
    private final TreeMap<String, Long> map = new TreeMap<>();
    private final FileHandler fileHandler;

    public Index(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        for (Pair<String, Long> record : fileHandler) {
            map.put(record.getKey(), record.getValue());
        }
    }

    public void add(String key, long offset) {
        System.out.println("Key " + key + " offset : " + offset + " File " + fileHandler);
        map.put(key, offset);
    }

    public String read(String key) {
        Long offset = map.get(key);
        if (offset == null) {
            return null;
        }
        System.out.println();
        String line = fileHandler.readLine(offset);
        if (line == null) {
            System.out.println();
        }
        return line.split(",")[1];
    }

    public void close() {
        fileHandler.close();
    }
}
