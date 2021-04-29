package com.pszymczyk.articles.stats;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class ArticleVisited {

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

    String getArticleTitle() {
        return articleTitle;
    }

    public String getCategory() {
        return category;
    }

    Long getTime() {
        return time;
    }
}
