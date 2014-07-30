package com.eeb.dropwizardmongo.example;

import com.eeb.dropwizardmongo.configuration.DropwizardMongoConfiguration;
import com.eeb.dropwizardmongo.health.MongoHealthCheck;
import com.eeb.dropwizardmongo.example.resources.CollectionIdsResource;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Dropwizard application with DropwizardMongoConfiguration type.
 */
public class DropwizardMongoApplication extends Application<DropwizardMongoConfiguration> {

    @Override
    public void initialize(Bootstrap<DropwizardMongoConfiguration> bootstrap) {

    }

    @Override
    public void run(DropwizardMongoConfiguration config, Environment environment) throws Exception {

        final MongoClient mongoClient = config.getMongoClientFactory().build(environment);
        final DB db = config.getMongoDBFactory().build(mongoClient);

        //Register health checks
        environment.healthChecks().register("mongo",new MongoHealthCheck(mongoClient));

        //Register Resources
        environment.jersey().register(new CollectionIdsResource(db));


    }
}
 
