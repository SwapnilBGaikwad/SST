package com.github.sst.mem;

import com.github.sst.file.FileHandler;
import com.github.sst.index.Index;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MigrationCache {
    private final Map<String, MigrationJob> jobCache = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void startMigration(TreeMap<String, String> avlTree, Consumer<Index> consumer) {
        String jobId = UUID.randomUUID().toString();
        MigrationJob migrationJob = new MigrationJob(jobId, avlTree, new FileHandler());
        jobCache.put(jobId, migrationJob);

        executor.execute(() -> {
            FileHandler fileHandler = migrationJob.getFileHandler();
            Index index = new Index(fileHandler);
            for (Map.Entry<String, String> entry : migrationJob.getCachedData().entrySet()) {
                try {
                    long offset = fileHandler.write(entry.getKey(), entry.getValue());
                    index.add(entry.getKey(), offset);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
            consumer.accept(index);
            synchronized (jobCache) {
                jobCache.remove(jobId);
            }
        });
    }

    public String read(String key) {
        for (MigrationJob job : jobCache.values()) {
            if (job.getCachedData().containsKey(key)) {
                return job.getCachedData().get(key);
            }
        }
        return null;
    }

    public void shutDown() throws InterruptedException {
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
