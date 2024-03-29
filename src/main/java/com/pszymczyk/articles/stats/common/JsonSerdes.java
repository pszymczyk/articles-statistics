package com.pszymczyk.articles.stats.common;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serde;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static org.apache.kafka.common.serialization.Serdes.serdeFrom;

public class JsonSerdes {

    final static ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new Jdk8Module())
        .registerModule(new JavaTimeModule())
        .setVisibility(PropertyAccessor.CREATOR, ANY)
        .setVisibility(PropertyAccessor.FIELD, NONE)
        .setVisibility(PropertyAccessor.GETTER, ANY)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
        .configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true)
        .configure(MapperFeature.AUTO_DETECT_FIELDS, true);

    public static <T> Serde<T> forA(Class<T> aClass) {
        JsonSerializer<T> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(aClass, objectMapper);
        deserializer.ignoreTypeHeaders();
        return serdeFrom(serializer, deserializer);
    }
}
