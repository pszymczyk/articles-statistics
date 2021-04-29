package com.pszymczyk.articles.stats.advertorial.interactivequeries;

import com.pszymczyk.articles.stats.advertorial.PresentAdvertorial;
import com.pszymczyk.articles.stats.common.JsonSerdes;
import com.pszymczyk.articles.stats.events.SetAdvertorial;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.kafka.config.KafkaStreamsInfrastructureCustomizer;

import java.time.Clock;

public class AdvertorialsAggregator implements KafkaStreamsInfrastructureCustomizer {

    public static final String SET_ADVERTORIAL = "set-advertorial";
    public static final String ADVERTORIALS_LOCAL_TABLE = "advertorials-local-table";

    private final Clock clock;

    public AdvertorialsAggregator(Clock clock) {
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
                Materialized.<String, PresentAdvertorial, KeyValueStore<Bytes, byte[]>>as(ADVERTORIALS_LOCAL_TABLE)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(JsonSerdes.forA(PresentAdvertorial.class))
            );
    }

}
