package com.pszymczyk.articles.stats.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticleVisited {

    private final String articleTitle;
    private final String category;
    private final Long time;

    @JsonCreator
    ArticleVisited(
        @JsonProperty("articleTitle") String articleTitle,
        @JsonProperty("category") String category,
        @JsonProperty("time") Long time) {
        this.articleTitle = articleTitle;
        this.category = category;
        this.time = time;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getCategory() {
        return category;
    }

    public Long getTime() {
        return time;
    }
}
