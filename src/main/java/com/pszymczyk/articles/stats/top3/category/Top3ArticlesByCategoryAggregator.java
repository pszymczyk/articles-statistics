package com.pszymczyk.articles.stats.top3.category;

import com.pszymczyk.articles.stats.common.JsonSerdes;
import com.pszymczyk.articles.stats.events.ArticleVisited;
import com.pszymczyk.articles.stats.top3.ArticlesRanking;
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

public class Top3ArticlesByCategoryAggregator implements KafkaStreamsInfrastructureCustomizer {

    public static final String ARTICLES_VISITS = "articles-visit";
    public static final String ARTICLES_VISITS_TOP_THREE_WINDOW_STORE = "articles-visit-top-three-by-category-window-store";

    @Override
    public void configureBuilder(StreamsBuilder builder) {
        KStream<String, ArticleVisited> allArticleVisitedEvents = builder
            .stream(ARTICLES_VISITS, Consumed.with(Serdes.String(), JsonSerdes.forA(ArticleVisited.class)));

        allArticleVisitedEvents
            .groupBy((k, articleVisited) -> articleVisited.getCategory())
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