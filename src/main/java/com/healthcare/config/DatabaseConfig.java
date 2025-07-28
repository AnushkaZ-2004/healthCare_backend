// config/DatabaseConfig.java
package com.healthcare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.healthcare.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    // This class can be empty - just enables JPA repositories
}