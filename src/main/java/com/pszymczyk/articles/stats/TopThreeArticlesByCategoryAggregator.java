package com.pszymczyk.articles.stats;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.kafka.config.KafkaStreamsInfrastructureCustomizer;

class TopThreeArticlesByCategoryAggregator implements KafkaStreamsInfrastructureCustomizer {

    static final String ARTICLES_VISITS = "articles-visit";
    static final String ARTICLES_VISITS_TOP_THREE_WINDOW_STORE = "articles-visit-top-three-by-category-window-store";

    @Override
    public void configureBuilder(StreamsBuilder builder) {
        KStream<String, ArticleVisited> allArticleVisitedEvents = builder
            .stream(ARTICLES_VISITS, Consumed.with(Serdes.String(), JsonSerdes.forA(ArticleVisited.class)));

//        allArticleVisitedEvents
//            .groupBy((k, articleVisited) -> "ranking")
//            .windowedBy(TimeWindows.of(Duration.ofDays(3)).advanceBy(Duration.ofDays(1)))
//            .aggregate(
//                ArticlesRanking::create,
//                (key, articleVisited, articlesRanking) -> articlesRanking.apply(articleVisited),
//                Materialized.<String, ArticlesRanking, WindowStore<Bytes, byte[]>>as(ARTICLES_VISITS_TOP_THREE_WINDOW_STORE)
//                    .withKeySerde(Serdes.String())
//                    .withValueSerde(JsonSerdes.forA(ArticlesRanking.class))
//            );
    }
}