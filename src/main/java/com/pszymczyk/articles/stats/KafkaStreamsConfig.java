package com.pszymczyk.articles.stats;

import com.pszymczyk.articles.stats.top3.ArticleEventTimeExtractor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.state.HostInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.CleanupConfig;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public
class KafkaStreamsConfig {

    public static KafkaStreamsConfiguration addApplicationIdConfig(KafkaStreamsConfiguration kafkaStreamsConfig, String applicationId) {
        Map<String, Object> newProperties = new HashMap<>();
        kafkaStreamsConfig.asProperties().forEach((s, o) -> newProperties.put((String) s, o));
        newProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        return new KafkaStreamsConfiguration(newProperties);
    }

    @Bean
    KafkaStreamsConfiguration defaultKafkaStreamsConfig(KafkaProperties kafkaProperties, HostInfo hostInfo) {
        HashMap<String, Object> configs = new HashMap<>(kafkaProperties.buildStreamsProperties());
        configs.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        configs.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        configs.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        configs.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, ArticleEventTimeExtractor.class);
        configs.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class);
        configs.put(StreamsConfig.APPLICATION_SERVER_CONFIG, hostInfo.host() + ":" + hostInfo.port());
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaStreamsConfiguration(configs);
    }

    @Bean
    CleanupConfig cleanupConfig() {
        return new CleanupConfig(true, true);
    }

    @Bean
    HostInfo hostInfo(@Value("${server.port}") Integer port) throws UnknownHostException {
        String host = getHost();
        return new HostInfo(host, port);
    }

    /**
     * Warning!!!
     * This naive method will not work on Cloud environment like Kubernetes or OpenStack, on this kind of deployment you need to use environment
     * variables set by cloud provider.
     */
    private String getHost() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
