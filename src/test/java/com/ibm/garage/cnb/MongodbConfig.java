package com.ibm.garage.cnb;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import de.flapdoodle.embed.mongo.config.MongoCommonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MongodbConfig {
    @Autowired
    private MongoCommonConfig mongoConfig;

    @Bean
    @Primary
    public MongoClient mongoClient() {
        return MongoClients.create(String.format("mongodb://127.0.0.1:%d", mongoConfig.net().getPort()));
    }
}
