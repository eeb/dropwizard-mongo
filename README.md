#Connect dropwizard to MongoDB 


*dropwizard-mongo is a set of factories and health checks to be used with [dropwizard](http://dropwizard.github.io/dropwizard/) for connecting to MongoDB.*

## Disclaimer
Update 8/1: Major refactor that simplifies usage. Thanks to [kgilmer](https://github.com/kgilmer) for the review.  
This project is brand new, so I have a lot of additional work to support the multiple options that can be passed to the MongoClient, DB, and DBCollection objects.

##Usage

### Installation

The project artifacts are not currently hosted in a maven repository, so you will need to clone the repo and build the dropwizard-mongo project at a minimum.

I recommend you use dropwizard-mongo in combination with the [MongoJack](http://mongojack.org/) project for quickest implementation. The dropwizard-mongo-example project shows an example of using MongoJack with dropwizard-mongo.

### Updating Your yaml

If you want the factory to build a [MongoClient](https://api.mongodb.org/java/current/com/mongodb/MongoClient.html) object you can specify the connections in you config file.

    mongoDB:
      connections:
        - host: localhost
          port: 27017
        - host: 192.168.1.12
          port: 27017
              
If you want to the factory to build a [DB](https://api.mongodb.org/java/current/com/mongodb/DB.html) object you will need to further specify the db name in the config.

    mongoDB:
        dbName: unittest
        connections:
            - host: localhost
              port: 27017
            - host: 192.168.1.12
              port: 27017

Finally, the factory can return a [DBCollection](https://api.mongodb.org/java/current/com/mongodb/DBCollection.html) object
 you will need to further specify the collection name in the config.

    mongoDB:
        dbName: unittest
        collName: test
        connections:
            - host: localhost
              port: 27017
            - host: 192.168.1.12
              port: 27017
                
      
### Updating you configuration
You will need to add a reference to MongoFactory in your class that extends Configuration or 
you can extend from com.eeb.dropwizardmongo.configuration.DropwizardMongoConfiguration.

    @Valid
    @NotNull
    private MongoFactory mongoFactory = new MongoFactory();
      
    @JsonProperty("mongoDB")
    public MongoFactory getMongoFactory() {
        return this.mongoFactory;
    }
      
    @JsonProperty("mongoDB")
    public void setMongoFactory(MongoFactory MongoFactory) {
        this.mongoFactory = MongoFactory;
    }         
      
      

### Update your applications run method
Your class that extends from Application needs to be updated to register the health checks and call the 
factory method you wish to use. Once you have the object you want you can pass it to your dropwizard resources.

#### Registering the health checks
     final MongoClient mongoClient = config.getMongoFactory().buildClient(environment);
     environment.healthChecks().register("mongo",new MongoHealthCheck(mongoClient));

#### Building a MongoClient object
     final MongoClient mongoClient = config.getMongoFactory().buildClient(environment); 
     //Register Resources
     environment.jersey().register(new CollectionIdsResource(mongoClient));
      
#### Building a DB object
     final DB db = config.getMongoFactory().buildDB(environment); 
     //Register Resources
     environment.jersey().register(new CollectionIdsResource(db));
    
#### Building a DBCollection object    
    final DBCollection coll = config.getMongoFactory().buildColl(environment); 
    //Register Resources
    environment.jersey().register(new CollectionIdsResource(coll));
         
### Using MongoJack with dropwizard-mongo

The secret sauce here is MongoJack. It lets me pass my Jackson API objects directly to the MongoDB API as well as returning the results of queries as my API objects.

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

My Resource's GET handler.

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

MongoJack provides wrappers for the standard MongoDB api calls that will parse MongoDB documents into objects that are Jackson annotated. 

