package com.pszymczyk.articles.stats

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@Configuration
class IntegrationTestsConfiguration {

    @Bean
    @Primary
    Clock fixedClock() {
        return Clock.fixed(Instant.parse("2007-12-15T10:15:30.00Z"), ZoneOffset.UTC);
    }
}
