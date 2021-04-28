package com.pszymczyk.articles.stats

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.CleanupConfig

@Configuration
class KafkaStreamsTestConfiguration {

    @Bean
    @Primary
    CleanupConfig testCleanupConfig() {
        return new CleanupConfig(true, true)
    }
}
