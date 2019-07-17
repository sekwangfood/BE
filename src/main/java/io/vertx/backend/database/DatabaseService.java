package io.vertx.backend.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

@ProxyGen
@VertxGen
public interface DatabaseService {

  @GenIgnore
  static DatabaseService create(MongoClient dbClient, Handler<AsyncResult<DatabaseService>> readyHandler) {
    return new DatabaseServiceImpl(dbClient, readyHandler);
  }

  @GenIgnore
  static io.vertx.backend.database.reactivex.DatabaseService createProxy(Vertx vertx, String address) {
    return new io.vertx.backend.database.reactivex.DatabaseService(new DatabaseServiceVertxEBProxy(vertx, address));
  }

  @Fluent
  DatabaseService fetchBusiness(String name, Handler<AsyncResult<JsonObject>> resultHandler);
}
