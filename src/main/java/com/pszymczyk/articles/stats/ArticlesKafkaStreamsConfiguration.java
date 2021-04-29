package com.pszymczyk.articles.stats;

import com.pszymczyk.articles.stats.advertorial.global.GlobalTableAdvertorialsAggregator;
import com.pszymczyk.articles.stats.advertorial.global.GlobalTableAdvertorialsStreams;
import com.pszymczyk.articles.stats.advertorial.interactivequeries.AdvertorialsAggregator;
import com.pszymczyk.articles.stats.advertorial.interactivequeries.AdvertorialsStreams;
import com.pszymczyk.articles.stats.top3.category.Top3ArticlesByCategoryAggregator;
import com.pszymczyk.articles.stats.top3.category.Top3ArticlesByCategoryStreams;
import com.pszymczyk.articles.stats.top3.global.GlobalTop3ArticlesAggregator;
import com.pszymczyk.articles.stats.top3.global.GlobalTop3ArticlesStreams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.CleanupConfig;

import java.time.Clock;

import static com.pszymczyk.articles.stats.KafkaStreamsConfig.addApplicationIdConfig;

@Configuration
class ArticlesKafkaStreamsConfiguration {

    @Bean
    @GlobalTableAdvertorialsStreams
    StreamsBuilderFactoryBean globalTableAdvertorialsStreams(KafkaStreamsConfiguration kafkaStreamsConfig, CleanupConfig cleanupConfig, Clock clock) {

        GlobalTableAdvertorialsAggregator infrastructureCustomizer = new GlobalTableAdvertorialsAggregator(clock);

        StreamsBuilderFactoryBean namedStreamsBuilderFactoryBean = new StreamsBuilderFactoryBean(
            addApplicationIdConfig(kafkaStreamsConfig, "global-table-advertorials-aggregator"),
            cleanupConfig);

        namedStreamsBuilderFactoryBean.setInfrastructureCustomizer(infrastructureCustomizer);
        return namedStreamsBuilderFactoryBean;
    }

    @Bean
    @AdvertorialsStreams
    StreamsBuilderFactoryBean advertorialsStreams(KafkaStreamsConfiguration kafkaStreamsConfig, CleanupConfig cleanupConfig) {

        AdvertorialsAggregator infrastructureCustomizer = new AdvertorialsAggregator();

        StreamsBuilderFactoryBean namedStreamsBuilderFactoryBean = new StreamsBuilderFactoryBean(
            addApplicationIdConfig(kafkaStreamsConfig, "advertorials-aggregator"),
            cleanupConfig);

        namedStreamsBuilderFactoryBean.setInfrastructureCustomizer(infrastructureCustomizer);
        return namedStreamsBuilderFactoryBean;
    }

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
