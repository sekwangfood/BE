package io.vertx.backend.database;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.MaybeHelper;
import io.vertx.reactivex.SingleHelper;
import io.vertx.reactivex.ext.mongo.MongoClient;
import io.vertx.reactivex.ext.sql.SQLClientHelper;

public class DatabaseServiceImpl implements DatabaseService {

  private final MongoClient dbClient;

  DatabaseServiceImpl(io.vertx.ext.mongo.MongoClient dbClient, Handler<AsyncResult<DatabaseService>> readyHandler) {

    this.dbClient = new MongoClient(dbClient);
    readyHandler.handle(Future.succeededFuture(this));

  }

  @Override
  public DatabaseService fetchBusiness(String name, Handler<AsyncResult<JsonObject>> resultHandler) {

    dbClient.rxFindOne("price", new JsonObject().put("business_name", name), null)
      .map(result -> {
        return result;
      })
      .subscribe(MaybeHelper.toObserver(resultHandler));
    return this;

  }

}
