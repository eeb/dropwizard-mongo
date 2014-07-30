package com.eeb.dropwizardmongo.test;

import com.codahale.metrics.health.HealthCheck;
import com.eeb.dropwizardmongo.factory.MongoClientFactory;
import com.eeb.dropwizardmongo.factory.MongoConnectionFactory;
import com.eeb.dropwizardmongo.factory.MongoDBFactory;
import com.eeb.dropwizardmongo.health.MongoHealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration test for the factory classes. The test will require an available Mongo instance.
 */
public class FactoryIntegrationTest {

    private final MongoClientFactory clientFactory = new MongoClientFactory();
    private final LifecycleEnvironment lEnv = mock(LifecycleEnvironment.class);
    private final Environment env = mock(Environment.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final String port = "27017";


    @Before
    public void before() {
        when(env.lifecycle()).thenReturn(lEnv);
    }


    @Test
    public void singleAddressTest() throws IOException {
        final MongoClient client = createSingleAddress();
        assert client != null : "Mongo client is null";
        assert client.getAddress().toString().equals("localhost:"+port+"") : "Client does not contain an address";

    }

    //@Test
    //TODO:Setup a replica
    public void multipleAddressTest() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final ArrayList<MongoConnectionFactory> connFactoryList = new ArrayList<>();
        connFactoryList.add(mapper.readValue("{\"host\":\"localhost\",\"port\":"+port+"}", MongoConnectionFactory.class));
        connFactoryList.add(mapper.readValue("{\"host\":\"192.168.0.25\",\"port\":"+port+"}", MongoConnectionFactory.class));

        clientFactory.setConnections(connFactoryList);
        final MongoClient client = clientFactory.build(env);
        assert client.getAddress().toString().equals("localhost:"+port+"") : "Client does not contain an address";

    }

    @Test
    public void testHealthCheck() throws IOException {
        final MongoClient client = createSingleAddress();
        assert client != null : "Mongo client is null";

        final MongoHealthCheck hc = new MongoHealthCheck(client);
        final HealthCheck.Result res = hc.execute();
        assert res.isHealthy() : "Mongo is not connected";
    }

    @Test
    public void testDBFactory() throws IOException {
        final MongoClient client = createSingleAddress();
        assert client != null : "Mongo client is null";

        final MongoDBFactory dbFactory = new MongoDBFactory();
        dbFactory.setDbName();
        final DB db = dbFactory.build(client);
        assert db != null : "DB object is null";
    }



    private MongoClient createSingleAddress() throws IOException {
        final ArrayList<MongoConnectionFactory> connFactoryList = new ArrayList<>();
        connFactoryList.add(mapper.readValue("{\"host\":\"localhost\",\"port\":"+port+"}", MongoConnectionFactory.class));
        clientFactory.setConnections(connFactoryList);

        return clientFactory.build(env);
    }

}
 
