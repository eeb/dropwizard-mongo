package com.eeb.dropwizardmongo.health;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientException;

/**
 * Created by eeb on 6/23/14.
 */
public class MongoHealthCheck extends HealthCheck {

    private final MongoClient mongoClient;

    public MongoHealthCheck(MongoClient mongoClient) {
        super();
        this.mongoClient = mongoClient;
    }

    @Override
    protected Result check() throws Exception {

        try {
            mongoClient.getDB("system").getStats();
        }catch(MongoClientException ex) {
            return Result.unhealthy(ex.getMessage());
        }


        return Result.healthy();
    }

}
