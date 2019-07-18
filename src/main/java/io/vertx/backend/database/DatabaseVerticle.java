package io.vertx.backend.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.backend.database.DatabaseService;

public class DatabaseVerticle extends AbstractVerticle {

  public static final String CONFIG_MONGO_CONNECTION_STRING = "db.mongo.connection_string";
  public static final String CONFIG_MONGO_DB_NAME = "db.mongo.db_name";
  public static final String CONFIG_QUEUE = "db.queue";

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    MongoClient dbClient = MongoClient.createShared(vertx, new JsonObject()
      .put("connection_string", config().getString(CONFIG_MONGO_CONNECTION_STRING, "mongodb://localhost:27017"))
      .put("db_name", config().getString(CONFIG_MONGO_DB_NAME, "kimchi")));

    DatabaseService.create(dbClient, ready -> {
      if (ready.succeeded()) {
        ServiceBinder binder = new ServiceBinder(vertx);
        binder.setAddress(CONFIG_QUEUE).register(DatabaseService.class, ready.result());
        startFuture.complete();
      } else {
        startFuture.fail(ready.cause());
      }
    });

  }

}
