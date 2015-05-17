package com.eeb.dropwizardmongo.test;

import com.eeb.dropwizardmongo.configuration.DropwizardMongoConfiguration;
import com.eeb.dropwizardmongo.example.DropwizardMongoApplication;
import com.eeb.dropwizardmongo.example.api.MongoDocument;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.UnknownHostException;
import java.util.List;

/**
 * End to end tests for the example. The configuration settings for
 * the MongoClient can be found in resources/config.yaml.
 *
 *
 */
public class ServerIntegrationUriTest {

    @ClassRule
    public static final DropwizardAppRule<DropwizardMongoConfiguration> rule =
            new DropwizardAppRule<>(DropwizardMongoApplication.class,
                    ClassLoader.getSystemClassLoader().getResource("config-uri.yaml").getPath());

    protected static MongoClient mongoClient;

    @BeforeClass
    public static void setup() throws UnknownHostException {
        mongoClient = rule.getConfiguration().getMongoFactory().buildClient(rule.getEnvironment());
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
    public void getIds() throws Exception {
        final Client client = ClientBuilder.newClient();

        final Response response = client.target(
                "http://localhost:8080/").path("test/").request(MediaType.APPLICATION_JSON_TYPE).get();

        List<MongoDocument> mList = response.readEntity(List.class);

        assert mList.size() == 2 : "There are only " + mList.size() + " documents in the collection. Expected 2";


    }


}
