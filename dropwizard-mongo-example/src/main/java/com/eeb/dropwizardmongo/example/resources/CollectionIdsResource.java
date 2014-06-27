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


    private MongoClient mongoClient;

    public CollectionIdsResource(MongoClient mongoClient) {

        this.mongoClient = mongoClient;
    }

    @GET
    public List<MongoDocument> fetch(@PathParam("collection") String collection) {
        DB db = mongoClient.getDB("test");
        JacksonDBCollection<MongoDocument, String> coll = JacksonDBCollection.wrap(db.getCollection(collection), MongoDocument.class,
                String.class);
        DBCursor<MongoDocument> cursor = coll.find();
        List<MongoDocument> l = new ArrayList<>();

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
