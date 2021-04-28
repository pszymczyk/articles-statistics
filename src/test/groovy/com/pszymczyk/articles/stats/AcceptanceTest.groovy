package com.pszymczyk.articles.stats

import com.jayway.jsonpath.JsonPath
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.kafka.core.KafkaTemplate
import spock.lang.Specification

import java.time.Instant
import java.util.concurrent.TimeUnit

import static GlobalTopThreeArticlesAggregator.ARTICLES_VISITS
import static org.awaitility.Awaitility.await

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceTest extends Specification {

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

        when: "simulate day before yesterday clicks"
            2.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$monzoArticle",
                            "time": $dayBeforeYesterday
                        }
                        """.toString()).get()
            }
            10.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$spacexArticle",
                            "time": $dayBeforeYesterday
                        }
                        """.toString()).get()
            }
            3.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$cloudKitchensArticle",
                            "time": $dayBeforeYesterday
                        }
                        """.toString()).get()
            }
            7.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$academyAwardsArticle",
                            "time": $dayBeforeYesterday
                        }
                        """.toString()).get()
            }
            1.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$superscriptArticle",
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
                            "time": $yesterday
                        }
                        """.toString()).get()
            }
            3.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$spacexArticle",
                            "time": $yesterday
                        }
                        """.toString()).get()
            }
            3.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$cloudKitchensArticle",
                            "time": $yesterday
                        }
                        """.toString()).get()
            }
            3.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$academyAwardsArticle",
                            "time": $yesterday
                        }
                        """.toString()).get()
            }
            2.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$superscriptArticle",
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
                            "time": $today
                        }
                        """.toString()).get()
            }
            7.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$spacexArticle",
                            "time": $today
                        }
                        """.toString()).get()
            }
            2.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$cloudKitchensArticle",
                            "time": $today
                        }
                        """.toString()).get()
            }
            5.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$academyAwardsArticle",
                            "time": $today
                        }
                        """.toString()).get()
            }
            1.times {
                kafkaTemplate.send(ARTICLES_VISITS,
                        """
                        {
                            "articleTitle": "$superscriptArticle",
                            "time": $today
                        }
                        """.toString()).get()
            }

        then: "today's ranking"
            await().atMost(10, TimeUnit.SECONDS).ignoreExceptions().untilAsserted {
                def response = restTemplate.getForEntity("/stats/top3Articles", String.class)
                assert response.statusCode.is2xxSuccessful()
                JsonPath.parse(response.body).with {
                    assert it.read('$.gold.title', String) == monzoArticle
                    assert it.read('$.gold.views', Long) == 21
                    assert it.read('$.silver.title', String) == spacexArticle
                    assert it.read('$.silver.views', Long) == 20
                    assert it.read('$.bronze.title', String) == academyAwardsArticle
                    assert it.read('$.bronze.views', Long) == 15
                }
            }
    }
}
