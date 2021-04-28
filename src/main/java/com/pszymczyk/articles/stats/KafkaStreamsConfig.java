package com.pszymczyk.articles.stats;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.CleanupConfig;

import java.util.HashMap;
import java.util.Map;

@Configuration
class KafkaStreamsConfig {

    static KafkaStreamsConfiguration addApplicationIdConfig(KafkaStreamsConfiguration kafkaStreamsConfig, String applicationId) {
        Map<String, Object> newProperties = new HashMap<>();
        kafkaStreamsConfig.asProperties().forEach((s, o) -> newProperties.put((String) s, o));
        newProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        return new KafkaStreamsConfiguration(newProperties);
    }

    @Bean
    KafkaStreamsConfiguration defaultKafkaStreamsConfig(KafkaProperties kafkaProperties) {
        HashMap<String, Object> configs = new HashMap<>(kafkaProperties.buildStreamsProperties());
        configs.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        configs.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);
        configs.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        configs.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        configs.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, ArticleEventTimeExtractor.class);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaStreamsConfiguration(configs);
    }

    @Bean
    CleanupConfig cleanupConfig() {
        return new CleanupConfig(false, false);
    }
}