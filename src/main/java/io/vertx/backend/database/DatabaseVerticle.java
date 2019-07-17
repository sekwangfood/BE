package io.vertx.backend.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;

public class DatabaseVerticle extends AbstractVerticle {

  public static final String CONFIG_MONGO_CONNECTION_STRING = "db.mongo.connection_string";
  public static final String CONFIG_MONGO_DB_NAME = "db.mongo.db_name";
  public static final String CONFIG_QUEUE = "db.queue";

  @Override
  public void start(Future<Void> startFuture) throws Exception {

//    Future<String> httpServerDeployment = Future.future();
//    vertx.deployVerticle("io.vertx.backend.http.HttpServerVerticle", httpServerDeployment);
//
//    httpServerDeployment.setHandler(ar -> {
//      if (ar.succeeded()) {
//        System.out.println("Server Verticle Deployed");
//      } else {
//        System.out.println("Server Verticle Failed");
//      }
//    });

    MongoClient dbClient = MongoClient.createShared(vertx, new JsonObject()
      .put("connection_string", config().getString(CONFIG_MONGO_CONNECTION_STRING, "mongodb://localhost:27017"))
      .put("db_name", config().getString(CONFIG_MONGO_DB_NAME, "kimchi")));

    DatabaseService.create(dbClient, ready -> {
      if (ready.succeeded()) {
        System.out.println("Database Verticle deployed");
        ServiceBinder binder = new ServiceBinder(vertx);
        binder.setAddress(CONFIG_QUEUE).register(DatabaseService.class, ready.result());
        startFuture.complete();
      } else {
        System.out.println("Database Verticle failed");
        startFuture.fail(ready.cause());
      }
    });
  }
}
