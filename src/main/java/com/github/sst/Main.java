package com.github.sst;

import com.github.sst.config.DbModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Injector injector = Guice.createInjector(new DbModule());
        Database db = injector.getInstance(Database.class);

        long t0 = System.currentTimeMillis();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < 500000; i++) {
            int suffix = (int) (Math.random() * 1000000);
            String key = "key-" + suffix;
            set.add(key);
            db.write(key, "value-" + suffix);
            int suffix1 = (int) (Math.random() * 1000000);
            String key1 = "key-" + suffix1;
            if (set.contains(key)) {
                String value = db.read(key1);
                System.out.println(value);
            }
        }
        long t1 = System.currentTimeMillis();
        System.out.println("Time : " + (t1 - t0));
        db.close();
    }
}