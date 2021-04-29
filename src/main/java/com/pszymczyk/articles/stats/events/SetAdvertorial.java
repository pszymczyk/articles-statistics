package com.pszymczyk.articles.stats.events;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SetAdvertorial {
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

    public String getCategory() {
        return category;
    }

    public String getHeader() {
        return header;
    }

    public String getArticle() {
        return article;
    }
}
