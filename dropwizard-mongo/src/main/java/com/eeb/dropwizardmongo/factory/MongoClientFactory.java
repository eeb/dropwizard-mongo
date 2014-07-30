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
 * An object of this class creates a single instance of the <code>MongoClient</code> object.
 * <p>
 *     To use this class add it as a field with getters and setters to your Configuration class and call the build
 *     method in your applications run method. The resulting MongoClient can then be passed to your Resources<br/>
 *     <p>
 *     An example of the yaml configuration:<br/>
 *     <br/>
 *     <pre><code>
 *     mongoClient:
 *         connections:
 *            - host: localhost
 *              port: 27017
 *            - host: 192.168.1.12
 *              port: 27017
 *     </code></pre>
 *     </p>
 * </p>
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
     * Builds the MongoClient from a set of connections specified in the
     * configuration file.
     * @param env Dropwizard environment.
     * @return <code>MongoClient</code>
     * @throws <code>UnknownHostException</code>
     */
    public MongoClient build(Environment env) throws UnknownHostException {

        final MongoClient client = new MongoClient(buildServerAddresses(getConnections(),env));

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
        final List<ServerAddress> sal = new ArrayList<>(conns.size());
        for(MongoConnectionFactory factory : conns) {
            sal.add(factory.build(env));
        }

        return sal;
    }
}
 
