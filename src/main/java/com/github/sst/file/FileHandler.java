package com.github.sst.file;


import javafx.util.Pair;

import java.io.*;
import java.util.Iterator;

public class FileHandler implements Iterable<Pair<String, Long>> {
    private final static String dataFolder = "/Users/swapnil.gaikwad/projects/SST/data/";
    private final RandomAccessFile fileWriter;
    private final RandomAccessFile fileReader;
    private static int counter = 0;
    private final String fileName;
    private final static String DELIMITER = ",";

    public FileHandler() {
        try {
            this.fileName = getFileName();
            fileWriter = new RandomAccessFile(this.fileName, "rw");
            fileWriter.seek(fileWriter.length());
            System.out.println("Created reader for ** " + (this.fileName));
            fileReader = new RandomAccessFile(this.fileName, "r");
            counter++;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getFileName() {
        File file = new File(dataFolder + "db" + counter + ".csv");
        while (file.exists()) {
            counter++;
            file = new File(dataFolder + "db" + counter + ".csv");
        }
        return file.getAbsolutePath();
    }

    public FileHandler(String fileName) {
        try {
            fileName = dataFolder + fileName;
            this.fileName = fileName;
            fileWriter = new RandomAccessFile(fileName, "rw");
            fileWriter.seek(fileWriter.length());
            System.out.println("Created reader for " + fileName);
            fileReader = new RandomAccessFile(fileName, "r");
            counter++;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public long write(String key, String value) throws IOException {
        String join = String.join(DELIMITER, key, value, "\n");
        long startPtr = fileWriter.getFilePointer();
        fileWriter.write(join.getBytes());
        return startPtr;
    }

    public String readLine(Long offset) {
        try {
            fileReader.seek(offset);
            return fileReader.readLine();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return fileName;
    }

    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<Pair<String, Long>> iterator() {
        try {
            return new RecordIterator();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File not found", e);
        }
    }

    class RecordIterator implements Iterator<Pair<String, Long>> {
        private final RandomAccessFile fileReader;
        private String recordKey = null;
        private long offset;

        public RecordIterator() throws FileNotFoundException {
            fileReader = new RandomAccessFile(fileName, "r");
        }

        @Override
        public boolean hasNext() {
            try {
                offset = fileReader.getFilePointer();
                String line = fileReader.readLine();
                if (line == null) {
                    return false;
                }
                recordKey = line.split(",")[0];
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public Pair<String, Long> next() {
            return new Pair<>(recordKey, offset);
        }
    }
}
