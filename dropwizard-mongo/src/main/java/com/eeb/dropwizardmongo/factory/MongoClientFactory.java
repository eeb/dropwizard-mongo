package com.eeb.dropwizardmongo.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.constraints.NotEmpty;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class creates a single instance of the <code>MongoClient</code> object.
 */
public class MongoClientFactory {


    @NotEmpty
    private List<MongoConnectionFactory> connections = new ArrayList<>();

    @JsonProperty
    public List<MongoConnectionFactory> getConnections() {
        return connections;
    }

    @JsonProperty
    public void setConnections(List<MongoConnectionFactory> connections) {
        this.connections = connections;
    }

    /**
     * Builds the MongoClient from a set of connections specified in a
     * configuration file.
     * @param env Dropwizard environment.
     * @return <code>MongoClient</code>
     * @throws <code>UnknownHostException</code>
     */
    public MongoClient build(Environment env) throws UnknownHostException {

        MongoClient client = new MongoClient(buildServerAddresses(getConnections(),env));

                env.lifecycle().manage(new Managed() {
                    @Override
                    public void start() throws Exception {

                    }

                    @Override
                    public void stop() throws Exception {
                        client.close();
                    }
                });


        return client;

    }


    private List<ServerAddress> buildServerAddresses(List<MongoConnectionFactory> conns, Environment env) throws UnknownHostException {
        List<ServerAddress> sal = new ArrayList<>(conns.size());
        for(MongoConnectionFactory factory : conns) {
            sal.add(factory.build(env));
        }

        return sal;
    }
}
