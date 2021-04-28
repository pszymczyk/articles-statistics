package com.pszymczyk.articles.stats

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import com.jayway.jsonpath.spi.json.JsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider
import com.jayway.jsonpath.spi.mapper.MappingProvider
import groovy.transform.CompileStatic

@CompileStatic
class JsonPathConfiguration {

    static void configure() {
        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new JacksonJsonProvider()
            private final MappingProvider mappingProvider = new JacksonMappingProvider()

            @Override
            JsonProvider jsonProvider() {
                return jsonProvider
            }

            @Override
            MappingProvider mappingProvider() {
                return mappingProvider
            }

            @Override
            Set<Option> options() {
                return EnumSet.noneOf(Option.class)
            }
        })
    }
}
