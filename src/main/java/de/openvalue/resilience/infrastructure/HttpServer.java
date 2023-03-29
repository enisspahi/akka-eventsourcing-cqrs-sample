package de.openvalue.resilience.infrastructure;

import akka.actor.typed.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import de.openvalue.resilience.adapters.api.OrderApi;
import de.openvalue.resilience.application.OrderService;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

public class HttpServer {

    public static void init(ActorSystem<?> system, OrderService orderService) {
        // Routes
        var routes = new OrderApi(orderService);

        CompletionStage<ServerBinding> futureBinding =
                Http.get(system).newServerAt("localhost", 8080).bind(routes.createRoute());

        futureBinding.whenComplete((binding, exception) -> {
            if (binding != null) {
                InetSocketAddress address = binding.localAddress();
                system.log().info("Server online at http://{}:{}/",
                        address.getHostString(),
                        address.getPort());
            } else {
                system.log().error("Failed to bind HTTP endpoint, terminating system", exception);
                system.terminate();
            }
        });
    }
}
