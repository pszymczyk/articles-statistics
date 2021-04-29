package com.pszymczyk.articles.stats.advertorial.interactivequeries;

import com.pszymczyk.articles.stats.advertorial.PresentAdvertorial;
import com.pszymczyk.articles.stats.advertorial.PresentAdvertorialsReadModel;
import com.pszymczyk.articles.stats.advertorial.global.GlobalTableAdvertorialsAggregator;
import com.pszymczyk.articles.stats.common.HostStoreInfo;
import com.pszymczyk.articles.stats.common.MetadataService;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AdvertorialsReadModel implements PresentAdvertorialsReadModel {


    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;
    private final HostInfo hostInfo;
    private final MetadataService metadataService;
    private final RestTemplate restTemplate;

    public AdvertorialsReadModel(@AdvertorialsStreams StreamsBuilderFactoryBean streamsBuilderFactoryBean,
                                 HostInfo hostInfo,
                                 RestTemplate restTemplate) {
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
        this.hostInfo = hostInfo;
        this.metadataService = new MetadataService(streamsBuilderFactoryBean);
        this.restTemplate = restTemplate;
    }


    @Override
    public PresentAdvertorial get(String category) {
        HostStoreInfo host = metadataService.streamsMetadataForStoreAndKey(AdvertorialsAggregator.ADVERTORIALS_LOCAL_TABLE, category, Serdes.String().serializer());

        if (!thisHost(host)) {
            return getPresentAdvertorialFrom(host, category);
        }

        // category is on this instance
        ReadOnlyKeyValueStore<String, PresentAdvertorial> store = streamsBuilderFactoryBean.getKafkaStreams().store(
            StoreQueryParameters.fromNameAndType(GlobalTableAdvertorialsAggregator.ADVERTORIALS_GLOBAL_TABLE, QueryableStoreTypes.keyValueStore()));

        return store.get(category);
    }

    private PresentAdvertorial getPresentAdvertorialFrom(HostStoreInfo host, String category) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> httpEntity = new HttpEntity<>(requestHeaders);

        return restTemplate.exchange(
            String.format("http://%s:%d/advertorials/%s", host.getHost(), host.getPort(), category),
            HttpMethod.GET,
            httpEntity,
            PresentAdvertorial.class).getBody();
    }

    private boolean thisHost(final HostStoreInfo host) {
        return host.getHost().equals(hostInfo.host()) &&
            host.getPort() == hostInfo.port();
    }
}
