package com.eeb.dropwizardmongo.test;

import com.eeb.dropwizardmongo.example.DropwizardMongoApplication;
import com.eeb.dropwizardmongo.configuration.DropwizardMongoConfiguration;
import com.eeb.dropwizardmongo.example.api.MongoDocument;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * End to end tests for the example. The configuration settings for
 * the MongoClient can be found in resources/config.yaml.
 *
 *
 */
public class ServerIntegrationTest {

    @ClassRule
    public static final DropwizardAppRule<DropwizardMongoConfiguration> rule =
            new DropwizardAppRule<>(DropwizardMongoApplication.class,
                    ClassLoader.getSystemClassLoader().getResource("config.yaml").getPath());

    protected static MongoClient mongoClient;

    @BeforeClass
    public static void setup() throws UnknownHostException {
        mongoClient = rule.getConfiguration().getMongoClientFactory().build(rule.getEnvironment());
    }

    @Before
    public void before() {
        final DB test = mongoClient.getDB("test");
        final DBCollection coll = test.getCollection("test");
        coll.remove(new BasicDBObject());

        coll.insert(new BasicDBObject());
        coll.insert(new BasicDBObject());

    }


    @Test
    public void getIds() throws IOException {

        final Client client = new Client();

        final ClientResponse response = client.resource(
                "http://localhost:8080/test").get(ClientResponse.class);


        final ObjectMapper mapper = new ObjectMapper();
        List<MongoDocument> mList = mapper.readValue(response.getEntity(String.class), List.class);

        assert mList.size() == 2 : "There are only " + mList.size() + " documents in the collection. Expected 2";


    }


}
 
