package tamseo;

import tamseo.utils.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.handler.StaticHandler;
import io.vertx.ext.apex.handler.sockjs.BridgeOptions;
import io.vertx.ext.apex.handler.sockjs.PermittedOptions;
import io.vertx.ext.apex.handler.sockjs.SockJSHandler;

public class RealtimeService extends AbstractVerticle {

	double rPrice = 100;

	// Convenience method so you can run it in your IDE
	public static void main(String[] args) {
		Runner.runExample(RealtimeService.class);
	}

	@Override
	public void start() throws Exception {

		Router router = Router.router(vertx);

		// Allow outbound traffic to the news-feed address

		BridgeOptions options = new BridgeOptions()
				.addOutboundPermitted(new PermittedOptions()
						.setAddress("news-feed"));

		router.route("/eventbus/*").handler(
				SockJSHandler.create(vertx).bridge(options));

		// Serve the static resources
		router.route().handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);

		// Publish a message to the address "news-feed" every second

		vertx.setPeriodic(1000,
				t -> {
					rPrice = 100 + Math.random()* 5;
					vertx.eventBus().publish("market-price", rPrice + " JPY");	
				});
	}
}
