package com.eeb.dropwizardmongo.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.ServerAddress;
import io.dropwizard.setup.Environment;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.UnknownHostException;

/**
 * This class builds a Mongo ServerAddress object from a host and port specified in a configuration file.
 *
 */
public class MongoConnectionFactory {

    @NotNull
    private String host;

    @Min(1)
    @Max(65535)
    private int port;

    @JsonProperty
    public String getHost() {
        return host;
    }

    @JsonProperty
    public void setHost(String host) {
        this.host = host;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Builds a Monog ServerAddress object for the MongoClient
     * @param env - The dropwizard Environment
     * @return A Mongo <code>ServerAddress</code>
     *
     */
    public ServerAddress build(Environment env) throws UnknownHostException {

        return new ServerAddress(host,port);
    }





}
 
