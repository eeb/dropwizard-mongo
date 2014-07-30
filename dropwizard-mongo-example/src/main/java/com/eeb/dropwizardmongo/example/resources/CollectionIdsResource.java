package com.eeb.dropwizardmongo.example.resources;

import com.eeb.dropwizardmongo.example.api.MongoDocument;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Returns a list of Ids for the specified collection.
 */
@Path("/{collection}/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CollectionIdsResource {


    private DB mongoDB;

    public CollectionIdsResource(DB mongoDB) {
        this.mongoDB = mongoDB;
    }

    @GET
    public List<MongoDocument> fetch(@PathParam("collection") String collection) {
        final JacksonDBCollection<MongoDocument, String> coll = JacksonDBCollection.wrap(mongoDB.getCollection(collection), MongoDocument.class,
                String.class);
        final DBCursor<MongoDocument> cursor = coll.find();
        final List<MongoDocument> l = new ArrayList<>();

        try {
            while(cursor.hasNext()) {
                l.add(cursor.next());
            }
        }finally {
            cursor.close();
        }

        return l;
    }


}
 
