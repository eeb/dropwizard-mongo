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
public class FactoryTest {

    private final MongoClientFactory clientFactory = new MongoClientFactory();
    private final LifecycleEnvironment lEnv = mock(LifecycleEnvironment.class);
    private final Environment env = mock(Environment.class);
    private final ObjectMapper mapper = new ObjectMapper();


    @Before
    public void before() {
        when(env.lifecycle()).thenReturn(lEnv);
    }


    @Test
    public void singleAddressTest() throws IOException {
        MongoClient client = createSingleAddress();
        assert client != null : "Mongo client is null";
        assert client.getAddress().toString().equals("localhost:27017") : "Client does not contain an address";

    }

    //@Test
    //TODO:Setup a replica
    public void multipleAddressTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<MongoConnectionFactory> connFactoryList = new ArrayList<>();
        connFactoryList.add(mapper.readValue("{\"host\":\"localhost\",\"port\":27017}", MongoConnectionFactory.class));
        connFactoryList.add(mapper.readValue("{\"host\":\"192.168.0.25\",\"port\":27017}", MongoConnectionFactory.class));

        clientFactory.setConnections(connFactoryList);
        MongoClient client = clientFactory.build(env);
        assert client.getAddress().toString().equals("localhost:27017") : "Client does not contain an address";

    }

    @Test
    public void testHealthCheck() throws IOException {
        MongoClient client = createSingleAddress();
        assert client != null : "Mongo client is null";

        MongoHealthCheck hc = new MongoHealthCheck(client);
        HealthCheck.Result res = hc.execute();
        assert res.isHealthy() : "Mongo is not connected";
    }

    @Test
    public void testDBFactory() throws IOException {
        MongoClient client = createSingleAddress();
        assert client != null : "Mongo client is null";

        MongoDBFactory dbFactory = new MongoDBFactory();
        dbFactory.setDbName();
        DB db = dbFactory.build(client);
        assert db != null : "DB object is null";
    }



    private MongoClient createSingleAddress() throws IOException {
        ArrayList<MongoConnectionFactory> connFactoryList = new ArrayList<>();
        connFactoryList.add(mapper.readValue("{\"host\":\"localhost\",\"port\":27017}", MongoConnectionFactory.class));
        clientFactory.setConnections(connFactoryList);

        return clientFactory.build(env);
    }

}
