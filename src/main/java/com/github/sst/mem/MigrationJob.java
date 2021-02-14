package com.github.sst.mem;

import com.github.sst.file.FileHandler;

import java.util.TreeMap;

public class MigrationJob {
    private final String jobId;
    private TreeMap<String, String> cachedData;
    private final FileHandler fileHandler;

    public MigrationJob(String jobId, TreeMap<String, String> cachedData, FileHandler fileHandler) {
        this.jobId = jobId;
        this.cachedData = cachedData;
        this.fileHandler = fileHandler;
    }

    public String getJobId() {
        return jobId;
    }

    public TreeMap<String, String> getCachedData() {
        return cachedData;
    }

    public FileHandler getFileHandler() {
        return fileHandler;
    }
}
