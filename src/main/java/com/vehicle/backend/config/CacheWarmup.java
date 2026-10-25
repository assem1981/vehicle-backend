package com.vehicle.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheWarmup implements CommandLineRunner {

    private final CacheManager cacheManager;

    public CacheWarmup(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void run(String... args) {
        clear("vehicles");
        clear("search_vehicles");
        clear("analytics_summary");
    }

    private void clear(String name) {
        try {
            org.springframework.cache.Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        } catch (Exception ignored) {
        }
    }
}
