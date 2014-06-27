package com.eeb.dropwizardmongo.example;

import com.eeb.dropwizardmongo.factory.MongoClientFactory;
import com.eeb.dropwizardmongo.factory.MongoDBFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by eeb on 6/11/14.
 */
public class DropwizardMongoConfiguration extends Configuration {


    @Valid
    @NotNull
    private MongoClientFactory mongoClientFactory = new MongoClientFactory();

    @Valid
    private MongoDBFactory mongoDBFactory = new MongoDBFactory();

    @JsonProperty("mongoDB")
    public MongoDBFactory getMongoDBFactory() {
        return mongoDBFactory;
    }

    @JsonProperty("mongoDB")
    public void setMongoDBFactory(MongoDBFactory mongoDBFactory) {
        this.mongoDBFactory = mongoDBFactory;
    }

    @JsonProperty("mongoClient")
    public MongoClientFactory getMongoClientFactory() {
        return mongoClientFactory;
    }

    @JsonProperty("mongoClient")
    public void setMongoClientFactory(MongoClientFactory mongoClientFactory) {
        this.mongoClientFactory = mongoClientFactory;
    }
}
