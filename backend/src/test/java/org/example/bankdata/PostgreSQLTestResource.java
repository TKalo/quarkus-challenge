package org.example.bankdata;

import java.util.HashMap;
import java.util.Map;

import org.testcontainers.containers.PostgreSQLContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class PostgreSQLTestResource implements QuarkusTestResourceLifecycleManager {

    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Override
    public Map<String, String> start() {
        POSTGRESQL_CONTAINER.start();
        Map<String, String> config = new HashMap<>();
        config.put("quarkus.datasource.jdbc.url", POSTGRESQL_CONTAINER.getJdbcUrl());
        config.put("quarkus.datasource.username", POSTGRESQL_CONTAINER.getUsername());
        config.put("quarkus.datasource.password", POSTGRESQL_CONTAINER.getPassword());
        return config;
    }

    @Override
    public void stop() {
        POSTGRESQL_CONTAINER.stop();
    }
}
