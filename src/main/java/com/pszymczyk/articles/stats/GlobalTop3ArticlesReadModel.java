package com.pszymczyk.articles.stats;

import com.pszymczyk.articles.stats.dto.Top3ArticlesDTO;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

import static com.pszymczyk.articles.stats.GlobalTop3ArticlesAggregator.ARTICLES_VISITS_TOP_THREE_WINDOW_STORE;
import static com.pszymczyk.articles.stats.GlobalTop3ArticlesAggregator.GLOBAL_RANKING_KEY;

@Component
class GlobalTop3ArticlesReadModel {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    private final Clock clock;

    public GlobalTop3ArticlesReadModel(@GlobalTop3ArticlesStreams StreamsBuilderFactoryBean streamsBuilderFactoryBean,
                                       Clock clock) {
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
        this.clock = clock;
    }

    public Top3ArticlesDTO get() {
        ReadOnlyWindowStore<String, ArticlesRanking> store = streamsBuilderFactoryBean.getKafkaStreams().store(
            StoreQueryParameters.fromNameAndType(ARTICLES_VISITS_TOP_THREE_WINDOW_STORE, QueryableStoreTypes.windowStore()));

        ArticlesRanking fetch = store.fetch(GLOBAL_RANKING_KEY, twoDaysBackAtStartOfDay());

        return fetch != null ? fetch.top3() : Top3ArticlesDTO.empty();
    }

    private long twoDaysBackAtStartOfDay() {
        return LocalDate.now(clock).minusDays(2).atStartOfDay().atZone(clock.getZone()).toInstant().toEpochMilli();
    }
}
