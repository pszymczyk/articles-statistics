package com.pszymczyk.articles.stats.commands;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class SetAdvertorial {
    private final String category;
    private final String header;
    private final String article;

    @JsonCreator
    public SetAdvertorial(
        @JsonProperty("category") String category,
        @JsonProperty("header") String header,
        @JsonProperty("article") String article) {
        this.category = category;
        this.header = header;
        this.article = article;
    }
}
