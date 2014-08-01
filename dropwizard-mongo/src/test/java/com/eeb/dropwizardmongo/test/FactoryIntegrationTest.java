package com.eeb.dropwizardmongo.test;

import com.codahale.metrics.health.HealthCheck;
import com.eeb.dropwizardmongo.exceptions.NullCollectionNameException;
import com.eeb.dropwizardmongo.exceptions.NullDBNameException;
import com.eeb.dropwizardmongo.factory.MongoFactory;
import com.eeb.dropwizardmongo.factory.ServerAddressBuilder;
import com.eeb.dropwizardmongo.health.MongoHealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration test for the factory classes. The test will require an available Mongo instance.
 */
public class FactoryIntegrationTest {

    private final LifecycleEnvironment lEnv = mock(LifecycleEnvironment.class);
    private final Environment env = mock(Environment.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final String port = "27017";
    private final String config = "{" +
                                    "\"connections\":[{\"host\":\"localhost\",\"port\":"+port+"}]," +
                                    "\"dbName\":\"unittest\"," +
                                    "\"collName\":\"test1\""   +
                                "}";


    @Before
    public void before() {
        when(env.lifecycle()).thenReturn(lEnv);
    }


    @Test
    public void singleAddressClientTest() throws IOException {
        final MongoFactory mongoFactory = mapper.readValue(config, MongoFactory.class);
        final MongoClient client = mongoFactory.buildClient(env);
        assert client != null : "Mongo client is null";
        assert client.getAddress().toString().equals("localhost:"+port+"") : "Client does not contain an address";

    }

    //@Test
    //TODO:Setup a replica
    public void multipleAddressTest() throws IOException {


    }

    @Test
    public void testHealthCheck() throws IOException {
        final MongoFactory mongoFactory = mapper.readValue(config, MongoFactory.class);
        final MongoClient client = mongoFactory.buildClient(env);
        assert client != null : "Mongo client is null";

        final MongoHealthCheck hc = new MongoHealthCheck(client);
        final HealthCheck.Result res = hc.execute();
        assert res.isHealthy() : "Mongo is not connected";
    }

    @Test
    public void testDBFactory() throws IOException, NullDBNameException {

        final MongoFactory mongoFactory = mapper.readValue(config, MongoFactory.class);
        final DB db = mongoFactory.buildDB(env);
        assert db != null : "Database object was not created";

    }

    @Test
    public void testCollectionFactory() throws IOException, NullDBNameException, NullCollectionNameException {

        final MongoFactory mongoFactory = mapper.readValue(config, MongoFactory.class);
        final DBCollection coll = mongoFactory.buildColl(env);
        assert coll != null : "Database object was not created";

    }





}
