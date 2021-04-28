package com.pszymczyk.articles.stats.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticleWithVisitsDTO {
    private final String title;
    private final Long views;

    @JsonCreator
    public ArticleWithVisitsDTO(
        @JsonProperty("title") String title,
        @JsonProperty("views") Long views) {
        this.title = title;
        this.views = views;
    }

    public String getTitle() {
        return title;
    }

    public Long getViews() {
        return views;
    }
}
