package com.pszymczyk.articles.stats.top3.category

import com.jayway.jsonpath.JsonPath
import com.pszymczyk.articles.stats.JsonPathConfiguration
import com.pszymczyk.articles.stats.KafkaContainerStarter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.kafka.core.KafkaTemplate
import spock.lang.Specification

import java.time.Instant
import java.util.concurrent.TimeUnit

import static com.pszymczyk.articles.stats.top3.global.GlobalTop3ArticlesAggregator.ARTICLES_VISITS
import static org.awaitility.Awaitility.await

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Top3ArticlesByCategoryAcceptanceTest extends Specification {

    static {
        JsonPathConfiguration.configure()
        KafkaContainerStarter.start()
    }

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate

    @Autowired
    TestRestTemplate restTemplate

    def "Should count top three articles ranking in last three days"() {
        given:
            def today = Instant.parse("2007-12-15T10:15:30.00Z").toEpochMilli()
            def yesterday = Instant.parse("2007-12-14T10:15:30.00Z").toEpochMilli()
            def dayBeforeYesterday = Instant.parse("2007-12-13T10:15:30.00Z").toEpochMilli()

            def monzoArticle = "Monzo and Innocent Drinks founders among high-profile execs floated for new Amazon TV show about startups"
            def spacexArticle = "Watch SpaceX launch 4 astronauts aboard a recycled Crew Dragon spaceship for NASA on Friday"
            def cloudKitchensArticle = "Travis Kalanick's stealth 5 billion startup, CloudKitchens, is Uber all over again, ruled by a 'temple of bros,' insiders say"
            def superscriptArticle = "Insurance startup Superscript used this pitch deck to raise 10 million in a funding round backed by Seedcamp"
            def academyAwardsArticle = "The 93rd Academy Awards will honor the best films of the year here's how to watch live this Sunday to see all the winners"

            def businessCategory = "business"
            def newsCategory = "news"

        when: "simulate day before yesterday clicks"
            2.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$monzoArticle",
                            "category": "$businessCategory",
                            "time": $dayBeforeYesterday
                        }
                        """.toString()).get()
            }
            10.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$spacexArticle",
                            "category": "$newsCategory",
                            "time": $dayBeforeYesterday
                        }
                        """.toString()).get()
            }
            3.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$cloudKitchensArticle",
                            "category": "$businessCategory",
                            "time": $dayBeforeYesterday
                        }
                        """.toString()).get()
            }
            7.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$academyAwardsArticle",
                            "category": "$newsCategory",
                            "time": $dayBeforeYesterday
                        }
                        """.toString()).get()
            }
            1.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$superscriptArticle",  
                            "category": "$businessCategory",
                            "time": $dayBeforeYesterday
                        }
                        """.toString()).get()
            }
        and: "simulate yesterday clicks"
            4.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$monzoArticle",
                            "category": "$businessCategory",
                            "time": $yesterday
                        }
                        """.toString()).get()
            }
            3.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$spacexArticle",
                            "category": "$newsCategory",
                            "time": $yesterday
                        }
                        """.toString()).get()
            }
            3.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$cloudKitchensArticle",
                            "category": "$businessCategory",
                            "time": $yesterday
                        }
                        """.toString()).get()
            }
            3.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$academyAwardsArticle",
                            "category": "$newsCategory",
                            "time": $yesterday
                        }
                        """.toString()).get()
            }
            2.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$superscriptArticle",
                            "category": "$businessCategory",
                            "time": $yesterday
                        }
                        """.toString()).get()
            }
        and: "simulate today clicks"
            15.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$monzoArticle",
                            "category": "$businessCategory",
                            "time": $today
                        }
                        """.toString()).get()
            }
            7.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$spacexArticle",
                            "category": "$newsCategory",
                            "time": $today
                        }
                        """.toString()).get()
            }
            2.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$cloudKitchensArticle",
                            "category": "$businessCategory",
                            "time": $today
                        }
                        """.toString()).get()
            }
            5.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$academyAwardsArticle",
                            "category": "$newsCategory",
                            "time": $today
                        }
                        """.toString()).get()
            }
            1.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$superscriptArticle",
                            "category": "$businessCategory",
                            "time": $today
                        }
                        """.toString()).get()
            }

        then: "today's ranking for $newsCategory"
            await().atMost(10, TimeUnit.SECONDS).ignoreExceptions().untilAsserted {
                def response = restTemplate.getForEntity("/stats/top3ArticlesByCategory/$newsCategory", String.class)
                assert response.statusCode.is2xxSuccessful()
                JsonPath.parse(response.body).with {
                    assert it.read('$.gold.title', String) == spacexArticle
                    assert it.read('$.gold.views', Long) == 20
                    assert it.read('$.silver.title', String) == academyAwardsArticle
                    assert it.read('$.silver.views', Long) == 15
                    assert it.read('$.bronze', String) == null
                }
            }
        and: "today's ranking for $businessCategory"
            await().atMost(10, TimeUnit.SECONDS).ignoreExceptions().untilAsserted {
                def response = restTemplate.getForEntity("/stats/top3ArticlesByCategory/$businessCategory", String.class)
                assert response.statusCode.is2xxSuccessful()
                JsonPath.parse(response.body).with {
                    assert it.read('$.gold.title', String) == monzoArticle
                    assert it.read('$.gold.views', Long) == 21
                    assert it.read('$.silver.title', String) == cloudKitchensArticle
                    assert it.read('$.silver.views', Long) == 8
                    assert it.read('$.bronze.title', String) == superscriptArticle
                    assert it.read('$.bronze.views', Long) == 4
                }
            }
    }
}
