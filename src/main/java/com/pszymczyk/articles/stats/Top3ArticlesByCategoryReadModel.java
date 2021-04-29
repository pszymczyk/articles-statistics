package com.pszymczyk.articles.stats;

import com.pszymczyk.articles.stats.dto.Top3ArticlesDTO;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

import static com.pszymczyk.articles.stats.Top3ArticlesByCategoryAggregator.ARTICLES_VISITS_TOP_THREE_WINDOW_STORE;

@Component
class Top3ArticlesByCategoryReadModel {

    private final KafkaStreams kafkaStreams;
    private final Clock clock;

    public Top3ArticlesByCategoryReadModel(
        @GlobalTop3ArticlesStreams StreamsBuilderFactoryBean streamsBuilderFactoryBean,
        Clock clock) {
        this.kafkaStreams = streamsBuilderFactoryBean.getKafkaStreams();
        this.clock = clock;
    }

    public Top3ArticlesDTO get(String category) {
        try {

            ReadOnlyWindowStore<String, ArticlesRanking> store = kafkaStreams.store(
                StoreQueryParameters.fromNameAndType(ARTICLES_VISITS_TOP_THREE_WINDOW_STORE, QueryableStoreTypes.windowStore()));



            return store.fetch(category, Instant.now(clock).toEpochMilli()).top3();
        } catch (Exception e) {
            return null;
        }
    }
}
