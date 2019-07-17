package io.vertx.backend;

import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Single<String> dbVerticleDeployment = vertx.rxDeployVerticle(
      "io.vertx.backend.http.HttpServerVerticle");
    dbVerticleDeployment
//      .flatMap(id -> {
//        Single<String> httpVerticleDeployment = vertx.rxDeployVerticle("io.vertx.backend.http.HttpServerVerticle");
//        return httpVerticleDeployment;
//      })
      .subscribe(id -> {
        System.out.println("Mongo");
        startFuture.complete();
      }, err -> {
        startFuture.fail(err);
      });
  }
}
