package com.eeb.dropwizardmongo.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.mongojack.ObjectId;

/**
 * This class is a Representation of the most basic parts of a Mongodb document.
 */
public class MongoDocument {

    private String id;

    @ObjectId
    @JsonProperty("_id")
    public String getId() {
        return this.id;
    }

    @ObjectId
    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

}
 
