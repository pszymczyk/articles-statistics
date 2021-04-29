package com.pszymczyk.articles.stats;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.CleanupConfig;

import static com.pszymczyk.articles.stats.KafkaStreamsConfig.addApplicationIdConfig;

@Configuration
class ArticlesKafkaStreamsConfiguration {

    @Bean
    @GlobalTop3ArticlesStreams
    StreamsBuilderFactoryBean top3ArticlesRankingStreams(KafkaStreamsConfiguration kafkaStreamsConfig, CleanupConfig cleanupConfig) {

        GlobalTop3ArticlesAggregator infrastructureCustomizer = new GlobalTop3ArticlesAggregator();

        StreamsBuilderFactoryBean namedStreamsBuilderFactoryBean = new StreamsBuilderFactoryBean(
            addApplicationIdConfig(kafkaStreamsConfig, "global-top-three-articles"),
            cleanupConfig);

        namedStreamsBuilderFactoryBean.setInfrastructureCustomizer(infrastructureCustomizer);
        return namedStreamsBuilderFactoryBean;
    }

    @Bean
    @Top3ArticlesByCategoryStreams
    StreamsBuilderFactoryBean top3ArticlesByCategoryRankingStreams(KafkaStreamsConfiguration kafkaStreamsConfig, CleanupConfig cleanupConfig) {

        Top3ArticlesByCategoryAggregator top3ArticlesByCategoryAggregator =
            new Top3ArticlesByCategoryAggregator();

        StreamsBuilderFactoryBean namedStreamsBuilderFactoryBean = new StreamsBuilderFactoryBean(
            addApplicationIdConfig(kafkaStreamsConfig, "top-three-articles-grouped-by-category"),
            cleanupConfig);
        namedStreamsBuilderFactoryBean.setInfrastructureCustomizer(top3ArticlesByCategoryAggregator);
        return namedStreamsBuilderFactoryBean;
    }
}
