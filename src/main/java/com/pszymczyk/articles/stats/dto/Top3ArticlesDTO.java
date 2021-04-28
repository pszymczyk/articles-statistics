package com.pszymczyk.articles.stats.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Top3ArticlesDTO {

    private final ArticleWithVisitsDTO gold;
    private final ArticleWithVisitsDTO silver;
    private final ArticleWithVisitsDTO bronze;

    @JsonCreator
    public Top3ArticlesDTO(
        @JsonProperty("gold") ArticleWithVisitsDTO gold,
        @JsonProperty("silver") ArticleWithVisitsDTO silver,
        @JsonProperty("bronze") ArticleWithVisitsDTO bronze) {
        this.gold = gold;
        this.silver = silver;
        this.bronze = bronze;
    }

    public static Top3ArticlesDTO empty() {
        return new Top3ArticlesDTO(null, null, null);
    }

    public ArticleWithVisitsDTO getGold() {
        return gold;
    }

    public ArticleWithVisitsDTO getSilver() {
        return silver;
    }

    public ArticleWithVisitsDTO getBronze() {
        return bronze;
    }
}
