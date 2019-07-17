package io.vertx.backend.http;

import io.vertx.backend.database.reactivex.DatabaseService;
import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.*;

public class HttpServerVerticle extends AbstractVerticle {

  public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
  public static final String CONFIG_DB_QUEUE = "db.queue";

  private DatabaseService dbService;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    
    String dbQueue = config().getString(CONFIG_DB_QUEUE, "db.queue");
    dbService = io.vertx.backend.database.DatabaseService.createProxy(vertx.getDelegate(), dbQueue);

    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    router.get("/business/:business_name").handler(this::apiGetBusiness);
    router.get("/").handler(context -> {
      context.response().putHeader("content-type", "text/html").end("Hello");
    });


    int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
    server
      .requestHandler(router)
      .rxListen(portNumber)
      .subscribe(s -> {
        System.out.println("yes");
        startFuture.complete();
      }, t -> {
        startFuture.fail(t);
      });
  }

  private void apiGetBusiness(RoutingContext context) {
    String business = context.request().getParam("business_name");
    dbService.rxFetchBusiness(business)
      .subscribe(dbObject -> {
        context.response().setStatusCode(200);
        context.response().putHeader("Content-Type", "application/json");
        JsonObject wrapped = new JsonObject().put("success", true);
        wrapped.put("business", dbObject.getString("business"));
        context.response().end(wrapped.encode());
      }, t -> {
        context.response().setStatusCode(404);
        context.response().putHeader("Content-Type", "application/json");
        context.response().end(new JsonObject()
          .put("success", false)
          .put("error", t).encode());
      });
  }
}
