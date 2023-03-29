package de.openvalue.resilience.adapters.api;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import de.openvalue.resilience.adapters.api.model.OrderResource;
import de.openvalue.resilience.application.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderApi extends AllDirectives {

    private static final Logger logger = LoggerFactory.getLogger(OrderApi.class);

    private final OrderService orderService;

    public OrderApi(OrderService orderService) {
        this.orderService = orderService;
    }

    public Route createRoute() {
        return concat(
                path("orders", () ->
                        post(() ->
                                entity(Jackson.unmarshaller(OrderResource.class), request -> {
                                    logger.info("Order request: {}", request);
                                    return onSuccess(orderService.handle(request.toCommand()),
                                            state -> complete(StatusCodes.CREATED, OrderResource.fromState(state), Jackson.marshaller())
                                    );
                                }))
                )
        );
    }
}
