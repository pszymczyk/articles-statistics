package com.pszymczyk.articles.stats.top3.category;

import com.pszymczyk.articles.stats.dto.Top3ArticlesDTO;
import com.pszymczyk.articles.stats.top3.ArticlesRanking;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Component
public
class Top3ArticlesByCategoryReadModel {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    private final Clock clock;

    public Top3ArticlesByCategoryReadModel(
        @Top3ArticlesByCategoryStreams StreamsBuilderFactoryBean streamsBuilderFactoryBean,
        Clock clock) {
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
        this.clock = clock;
    }

    public Top3ArticlesDTO get(String category) {
        ReadOnlyWindowStore<String, ArticlesRanking> store = streamsBuilderFactoryBean.getKafkaStreams().store(
            StoreQueryParameters.fromNameAndType(Top3ArticlesByCategoryAggregator.ARTICLES_VISITS_TOP_THREE_WINDOW_STORE, QueryableStoreTypes.windowStore()));

        ArticlesRanking fetch = store.fetch(category, twoDaysBackAtStartOfDay());

        return fetch != null ? fetch.top3() : Top3ArticlesDTO.empty();
    }

    private long twoDaysBackAtStartOfDay() {
        return LocalDate.now(clock).minusDays(2).atStartOfDay().atZone(clock.getZone()).toInstant().toEpochMilli();
    }
}
