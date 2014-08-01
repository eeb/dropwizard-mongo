package com.eeb.dropwizardmongo.factory;

import com.eeb.dropwizardmongo.exceptions.NullCollectionNameException;
import com.eeb.dropwizardmongo.exceptions.NullDBNameException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.DB;
import com.mongodb.DBCollection;
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
 *     To use this class add it as a field with getters and setters to your Configuration class and call the buildClient
 *     method in your applications run method. The resulting MongoClient can then be passed to your Resources<br/>
 *     <p>
 *     An example of the yaml configuration:<br/>
 *     <br/>
 *     <pre><code>
 *     mongoClient:
 *         dbname: unittest
 *         collName: test1
 *         connections:
 *            - host: localhost
 *              port: 27017
 *            - host: 192.168.1.12
 *              port: 27017
 *     </code></pre>
 *     </p>
 * </p>
 */
public class MongoFactory {

    /**
     * List of server addresses
     */
    @NotEmpty
    private List<ServerAddressBuilder> connections = new ArrayList<>();

    /**
     * Optional name of the database. This property is required to use the dbBuild method.
     */
    private String dbName;

    /**
     * Optional name of the collection to be set. The property is required to use the collBuild method.
     */
    private String collName;

    /**
     * The mongo API documentation for <a href="https://api.mongodb.org/java/current/com/mongodb/MongoClient.html">
     * MongoClient</a> states that there should only be one object per JVM, so this property is only set once.
     */
    private MongoClient mongoClient;

    @JsonProperty
    public String getCollName() {
        return collName;
    }

    @JsonProperty
    public void setCollName(String collName) {
        this.collName = collName;
    }

    @JsonProperty
    public String getDbName() {
        return dbName;
    }

    @JsonProperty
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @JsonProperty
    public List<ServerAddressBuilder> getConnections() {
        return connections;
    }

    @JsonProperty
    public void setConnections(List<ServerAddressBuilder> connections) {
        this.connections = connections;
    }

    /**
     * Builds the MongoClient from a set of connections specified in the
     * configuration file.
     * @param env Dropwizard environment.
     * @return A Mongo API {@code MongoClient} object.
     * @throws {@link UnknownHostException} Thrown if the server can not be found.
     */
    public MongoClient buildClient(Environment env) throws UnknownHostException {

        if(this.mongoClient != null)
            return mongoClient;

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

        this.mongoClient = client;

        return client;

    }

    /**
     * Builds a Mongo {@code DB} object from connection and db info set in a configuration file.
     * @param env The dropwizard environment.
     * @return A Mongo Java API {@code DB} object.
     * @throws {@link UnknownHostException} Thrown if the server can not be found.
     * @throws {@link com.eeb.dropwizardmongo.exceptions.NullDBNameException} Throw in the db name is null.
     */
    public DB buildDB(Environment env) throws UnknownHostException, NullDBNameException {
        if(this.dbName == null)
            throw new NullDBNameException();

        final MongoClient client = buildClient(env);
        return client.getDB(this.dbName);
    }

    /**
     * Builds a Mongo {@code DBCollection} object from connection, db, and collection information set in a
     * configuration file
     * @param env The dropwizard environment.
     * @return A Mongo Java API {@code DBCollection} object.
     * @throws {@link UnknownHostException} Thrown if the server can not be found.
     * @throws {@link com.eeb.dropwizardmongo.exceptions.NullDBNameException} Throw in the db name is null.
     * @throws {@link NullCollectionNameException} Thrown if the collection name is null.
     */
    public DBCollection buildColl(Environment env) throws UnknownHostException,NullDBNameException, NullCollectionNameException {
        if(this.dbName == null)
            throw new NullDBNameException();

        if(this.collName == null)
            throw new NullCollectionNameException();

        final DB db = buildDB(env);
        return db.getCollection(this.collName);

    }


    private List<ServerAddress> buildServerAddresses(List<ServerAddressBuilder> conns, Environment env) throws UnknownHostException {
        final List<ServerAddress> sal = new ArrayList<>(conns.size());
        for(ServerAddressBuilder factory : conns) {
            sal.add(factory.build(env));
        }

        return sal;
    }
}
