package com.pszymczyk.articles.stats.advertorial.global;

import com.pszymczyk.articles.stats.advertorial.PresentAdvertorial;
import com.pszymczyk.articles.stats.common.JsonSerdes;
import com.pszymczyk.articles.stats.events.SetAdvertorial;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.kafka.config.KafkaStreamsInfrastructureCustomizer;

import java.time.Clock;

public class GlobalTableAdvertorialsAggregator implements KafkaStreamsInfrastructureCustomizer {

    public static final String SET_ADVERTORIAL = "set-advertorial";
    public static final String ADVERTORIALS_GLOBAL_TABLE = "advertorials-global-table";
    public static final String ADVERTORIALS_GLOBAL_STATE = "advertorials-global-state";

    private final Clock clock;

    public GlobalTableAdvertorialsAggregator(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void configureBuilder(StreamsBuilder builder) {
        KStream<String, SetAdvertorial> allSetAdvertorials = builder
            .stream(SET_ADVERTORIAL, Consumed.with(Serdes.String(), JsonSerdes.forA(SetAdvertorial.class)));

        allSetAdvertorials
            .groupBy((k, setAdvertorial) -> setAdvertorial.getCategory())
            .aggregate(
                PresentAdvertorial::create,
                (key, setAdvertorial, presentAdvertorial) -> presentAdvertorial.apply(setAdvertorial, clock),
                Materialized.with(Serdes.String(), JsonSerdes.forA(PresentAdvertorial.class))
            )
            .toStream()
            .to(ADVERTORIALS_GLOBAL_STATE, Produced.with(Serdes.String(), JsonSerdes.forA(PresentAdvertorial.class)));

        builder.globalTable(ADVERTORIALS_GLOBAL_STATE,
                Materialized.<String, PresentAdvertorial, KeyValueStore<Bytes, byte[]>>as(ADVERTORIALS_GLOBAL_TABLE)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(JsonSerdes.forA(PresentAdvertorial.class)));
    }

}
