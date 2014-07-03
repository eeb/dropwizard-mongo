Connect dropwizard to MongoDB 
=============================

*dropwizard-mongo is a set of factories and health checks to be used with [dropwizard](http://dropwizard.github.io/dropwizard/) for connecting to MongoDB.*

### Disclaimer
This project is brand new, so I have a lot of additional work to support the multiple options that can be passed to the MongoClient and DB objects.

Usage
-----

### Installation

The project artifacts are not currently hosted in a maven repository, so you will need to clone the repo and build the dropwizard-mongo project at a minimum.

I recommend you use dropwizard-mongo in combination with the [MongoJack](http://mongojack.org/) project for quickest implementation. The dropwizard-mongo-example project shows an example of using MonoJack with dropwizard-mongo.

### Updating Your yaml

If you are planning on just passing around a MongoClient object you can specify the connections in you config file.

    mongoClient:
      connections:
        - host: localhost
          port: 27017
        - host: 192.168.1.12
          port: 27017
              
If you want to pass around the DB object you will need to further specify the db name in the config.

    mongoDB:
      dbName: test        
      
### Updating you configuration
You will need to add references to the factories you wish to use in your class that extends Configuration or 
you can extend from com.eeb.dropwizardmongo.configuration.DropwizardMongoConfiguration.

      @Valid
      @NotNull
      private MongoClientFactory mongoClientFactory = new MongoClientFactory();
  
      @Valid
      private MongoDBFactory mongoDBFactory = new MongoDBFactory();
  
      @JsonProperty("mongoDB")
      public MongoDBFactory getMongoDBFactory() {
          return mongoDBFactory;
      }
  
      @JsonProperty("mongoDB")
      public void setMongoDBFactory(MongoDBFactory mongoDBFactory) {
          this.mongoDBFactory = mongoDBFactory;
      }
  
      @JsonProperty("mongoClient")
      public MongoClientFactory getMongoClientFactory() {
          return mongoClientFactory;
      }
  
      @JsonProperty("mongoClient")
      public void setMongoClientFactory(MongoClientFactory mongoClientFactory) {
          this.mongoClientFactory = mongoClientFactory;
      }
      
      

### Update your applications run method
Your class that extends from Application needs to updated to build the MongoClient and DB.

        MongoClient mongoClient = config.getMongoClientFactory().build(environment);
        DB db = config.getMongoDBFactory().build(mongoClient);

        //Register health checks
        environment.healthChecks().register("mongo",new MongoHealthCheck(mongoClient));

        //Register Resources
        environment.jersey().register(new CollectionIdsResource(db));
      
You can see in this case I am passing the DB object to a Resources. You can also pass the MongoClient object and set the DB in the Resource itself. Both objects are stated to be thread safe.

### Using MongoJack
The secret sauce here is MongoJack. It lets me pass my Jackson API objects directly to the MongoDB API as well as returning the results of querries as my API objects.


Super basic API object representing a MongoDB document:

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

My Resources GET handler.

    @GET
    public List<MongoDocument> fetch(@PathParam("collection") String collection) {
        JacksonDBCollection<MongoDocument, String> coll = JacksonDBCollection.wrap(mongoDB.getCollection(collection), MongoDocument.class,
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

MongoJack provides wrappers for the standard MongoDB api calls that will marshall MongoDB documents into objects that are Jackson anotated. 

