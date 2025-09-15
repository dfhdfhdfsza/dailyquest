package com.dailyquest.dailyquest.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class FlywayRepairConfig {

    @Bean
    public FlywayMigrationStrategy repairThenMigrate(){
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
