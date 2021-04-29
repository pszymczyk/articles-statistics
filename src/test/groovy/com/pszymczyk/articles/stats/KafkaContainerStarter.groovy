package com.pszymczyk.articles.stats

import com.pszymczyk.articles.stats.top3.category.Top3ArticlesByCategoryAggregator
import com.pszymczyk.articles.stats.top3.global.GlobalTop3ArticlesAggregator
import groovy.transform.CompileStatic
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.testcontainers.containers.KafkaContainer

@CompileStatic
class KafkaContainerStarter {

    private static KafkaContainer kafkaContainer = new KafkaContainer()

    static void start() {
        kafkaContainer.start()
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers())
        setupTopics(kafkaContainer)
    }

    static void stop() {
        kafkaContainer.stop()
    }

    static void setupTopics(KafkaContainer kafkaContainer) {
        String bootstrapServers = kafkaContainer.getBootstrapServers()
        AdminClient adminClient = AdminClient.create(
                [
                        "bootstrap.servers": bootstrapServers,
                        "group.id=test"    : "test",
                ] as Map<String, Object>
        )

        def newTopics = [
                GlobalTop3ArticlesAggregator.ARTICLES_VISITS,
                Top3ArticlesByCategoryAggregator.ARTICLES_VISITS,
        ].collect { topicName -> new NewTopic(topicName, 1, (short) 1) }

        adminClient.createTopics(newTopics)
        adminClient.close()
    }
}
