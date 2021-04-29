package com.pszymczyk.articles.stats.rest;

import com.pszymczyk.articles.stats.advertorial.PresentAdvertorial;
import com.pszymczyk.articles.stats.advertorial.PresentAdvertorialsReadModel;
import com.pszymczyk.articles.stats.dto.Top3ArticlesDTO;
import com.pszymczyk.articles.stats.top3.category.Top3ArticlesByCategoryReadModel;
import com.pszymczyk.articles.stats.top3.global.GlobalTop3ArticlesReadModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ArticlesStatisticsController {

    private final GlobalTop3ArticlesReadModel globalTop3ArticlesReadModel;
    private final Top3ArticlesByCategoryReadModel top3ArticlesByCategoryReadModel;
    private final PresentAdvertorialsReadModel presentAdvertorialsReadModel;


    public ArticlesStatisticsController(GlobalTop3ArticlesReadModel globalTop3ArticlesReadModel,
                                        Top3ArticlesByCategoryReadModel top3ArticlesByCategoryReadModel,
                                        PresentAdvertorialsReadModel presentAdvertorialsReadModel) {
        this.globalTop3ArticlesReadModel = globalTop3ArticlesReadModel;
        this.top3ArticlesByCategoryReadModel = top3ArticlesByCategoryReadModel;
        this.presentAdvertorialsReadModel = presentAdvertorialsReadModel;
    }

    @GetMapping(value = "/stats/top3Articles", produces = MediaType.APPLICATION_JSON_VALUE)
    Top3ArticlesDTO getGlobalTop3Articles() {
        return globalTop3ArticlesReadModel.get();
    }

    @GetMapping(value = "/stats/top3ArticlesByCategory/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    Top3ArticlesDTO getGlobalTop3ArticlesByCategory(@PathVariable("category") String category) {
        return top3ArticlesByCategoryReadModel.get(category);
    }

    @GetMapping(value = "/advertorials/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    PresentAdvertorial getPresentAdvertorial(@PathVariable("category") String category) {
        return presentAdvertorialsReadModel.get(category);
    }
}
