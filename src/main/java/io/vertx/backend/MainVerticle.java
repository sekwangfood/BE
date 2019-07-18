package io.vertx.backend;

import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Vertx;
import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> future) throws Exception {

    CompositeFuture
      .all(deployHelper("io.vertx.backend.database.DatabaseVerticle"),
        deployHelper("io.vertx.backend.http.HttpServerVerticle"))
      .setHandler(result -> { 
        if(result.succeeded()){
          future.complete();
        } else {
          future.fail(result.cause());
        }
      });

  }

  private Future<Void> deployHelper(String name){

    final Future<Void> future = Future.future();
    vertx.deployVerticle(name, res -> {
      if(res.failed()){
         future.fail(res.cause());
      } else {
         future.complete();
      }
    });

    return future;

  }

}
