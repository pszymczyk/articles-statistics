package com.pszymczyk.articles.stats;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.kafka.config.KafkaStreamsInfrastructureCustomizer;

import java.time.Duration;

class GlobalTop3ArticlesAggregator implements KafkaStreamsInfrastructureCustomizer {

    static final String ARTICLES_VISITS = "articles-visit";
    static final String ARTICLES_VISITS_TOP_THREE_WINDOW_STORE = "articles-visit-top-three-window-store";
    static final String GLOBAL_RANKING_KEY = "global-ranking";

    @Override
    public void configureBuilder(StreamsBuilder builder) {
        KStream<String, ArticleVisited> allArticleVisitedEvents = builder
            .stream(ARTICLES_VISITS, Consumed.with(Serdes.String(), JsonSerdes.forA(ArticleVisited.class)));

        allArticleVisitedEvents
            .groupBy((k, articleVisited) -> GLOBAL_RANKING_KEY)
            .windowedBy(TimeWindows.of(Duration.ofDays(3)).advanceBy(Duration.ofDays(1)))
            .aggregate(
                ArticlesRanking::create,
                (key, articleVisited, articlesRanking) -> articlesRanking.apply(articleVisited),
                Materialized.<String, ArticlesRanking, WindowStore<Bytes, byte[]>>as(ARTICLES_VISITS_TOP_THREE_WINDOW_STORE)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(JsonSerdes.forA(ArticlesRanking.class))
            );
    }
}