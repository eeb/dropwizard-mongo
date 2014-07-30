package com.eeb.dropwizardmongo.factory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * An object of this class will return a MongoDB DB object for the dbName property specified in the configuration
 * file.
 * <p>
 *     This class is used in combination with {@link MongoClientFactory} when it is desired to pass a DB object instead
 *     of a MongoClient to your Resources. To use this class add it as a field with getters and setters
 *     to your Configuration class and call the build method in your applications run method.
 *     The resulting DB object can then be passed to your Resources<br/>
 *     <p>
 *     An example yaml configuration:
 *     <pre><code>
 *         mongoDB:
 *            dbName: test
 *     </code></pre>
 *     </p>
 *
 * </p>
 */
public class MongoDBFactory {

    private String dbName;

    @JsonProperty
    public String getDbName() {
        return dbName;
    }

    @JsonProperty
    public void setDbName() {
        this.dbName = "test";
    }



    public DB build(MongoClient client) {
        return client.getDB(getDbName());
    }

}
 
