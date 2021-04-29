package com.pszymczyk.articles.stats.advertorial;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pszymczyk.articles.stats.events.SetAdvertorial;

import java.time.Clock;
import java.time.Instant;

public class PresentAdvertorial {
    private final String category;
    private final String header;
    private final String article;
    private final Long presenceStartTime;

    public static PresentAdvertorial create() {
        return new PresentAdvertorial(null, null, null, null);
    }

    @JsonCreator
    public PresentAdvertorial(
        @JsonProperty("category") String category,
        @JsonProperty("header") String header,
        @JsonProperty("article") String article,
        @JsonProperty("presenceStartTime") Long presenceStartTime) {
        this.category = category;
        this.header = header;
        this.article = article;
        this.presenceStartTime = presenceStartTime;
    }

    public PresentAdvertorial apply(SetAdvertorial setAdvertorial, Clock clock) {
        return new PresentAdvertorial(
            setAdvertorial.getCategory(),
            setAdvertorial.getHeader(),
            setAdvertorial.getArticle(),
            Instant.now(clock).toEpochMilli()
        );
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

    public Long getPresenceStartTime() {
        return presenceStartTime;
    }
}
