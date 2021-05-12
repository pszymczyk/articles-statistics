package com.pszymczyk.articles.stats.advertorial;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdvertorialsController {

    private final PresentAdvertorialsReadModel presentAdvertorialsReadModel;

    public AdvertorialsController(PresentAdvertorialsReadModel presentAdvertorialsReadModel) {
        this.presentAdvertorialsReadModel = presentAdvertorialsReadModel;
    }

    @GetMapping(value = "/advertorials/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    PresentAdvertorial getPresentAdvertorial(@PathVariable("category") String category) {
        return presentAdvertorialsReadModel.get(category);
    }
}
