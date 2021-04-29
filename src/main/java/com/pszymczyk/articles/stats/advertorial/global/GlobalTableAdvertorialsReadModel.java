package com.pszymczyk.articles.stats.advertorial.global;

import com.pszymczyk.articles.stats.advertorial.PresentAdvertorial;
import com.pszymczyk.articles.stats.advertorial.PresentAdvertorialsReadModel;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Primary
public class GlobalTableAdvertorialsReadModel implements PresentAdvertorialsReadModel {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    public GlobalTableAdvertorialsReadModel(@GlobalTableAdvertorialsStreams StreamsBuilderFactoryBean streamsBuilderFactoryBean) {
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
    }

    @Override
    public PresentAdvertorial get(String category) {
        ReadOnlyKeyValueStore<String, PresentAdvertorial> store = streamsBuilderFactoryBean.getKafkaStreams().store(
            StoreQueryParameters.fromNameAndType(GlobalTableAdvertorialsAggregator.ADVERTORIALS_GLOBAL_TABLE, QueryableStoreTypes.keyValueStore()));

        return store.get(category);
    }

    Collection<PresentAdvertorial> all() {
        ReadOnlyKeyValueStore<String, PresentAdvertorial> store = streamsBuilderFactoryBean.getKafkaStreams().store(
            StoreQueryParameters.fromNameAndType(GlobalTableAdvertorialsAggregator.ADVERTORIALS_GLOBAL_TABLE, QueryableStoreTypes.keyValueStore()));

        List<PresentAdvertorial> actualList = new ArrayList<>();
        store.all().forEachRemaining(keyValue -> actualList.add(keyValue.value));
        return actualList;
    }
}
