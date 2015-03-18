package tamseo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;

import tamseo.utils.Runner;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class SimpleRESTService extends AbstractVerticle {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    Runner.runExample(SimpleRESTService.class);
  }

  private SimpleRESTService that = this;
  private Map<String, JsonObject> products = new HashMap<>();

  @Override
  public void start() {

    setUpInitialData();

    Vertx vertx = Vertx.vertx();

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    router.get("/products/:productID").handler(that::handleGetProduct);
    router.put("/products/:productID").handler(that::handleAddProduct);
    router.get("/products").handler(that::handleListProducts);

    vertx.createHttpServer().requestHandler(router::accept).listen(8080);
  }

  private void handleGetProduct(RoutingContext routingContext) {
    String productID = routingContext.request().getParam("productID");
    HttpServerResponse response = routingContext.response();
    if (productID == null) {
      sendError(400, response);
    } else {
      JsonObject product = products.get(productID);
      if (product == null) {
        sendError(404, response);
      } else {
        response.putHeader("content-type", "application/json").end(product.encode());
      }
    }
  }

  private void handleAddProduct(RoutingContext routingContext) {
    String productID = routingContext.request().getParam("productID");
    HttpServerResponse response = routingContext.response();
    if (productID == null) {
      sendError(400, response);
    } else {
      JsonObject product = routingContext.getBodyAsJson();
      if (product == null) {
        sendError(400, response);
      } else {
        products.put(productID, product);
        response.end();
      }
    }
  }

  private void handleListProducts(RoutingContext routingContext) {
    JsonArray arr = new JsonArray();
    products.values().forEach(arr::add);
    routingContext.response().putHeader("content-type", "application/json").end(arr.encode());
  }

  private void sendError(int statusCode, HttpServerResponse response) {
    response.setStatusCode(statusCode).end();
  }

  private void setUpInitialData() {
    addProduct(new JsonObject().put("id", "prod3568").put("name", "Egg Whisk").put("price", 3.99).put("weight", 150));
    addProduct(new JsonObject().put("id", "prod7340").put("name", "Tea Cosy").put("price", 5.99).put("weight", 100));
    addProduct(new JsonObject().put("id", "prod8643").put("name", "Spatula").put("price", 1.00).put("weight", 80));
  }

  private void addProduct(JsonObject product) {
    products.put(product.getString("id"), product);
  }
}