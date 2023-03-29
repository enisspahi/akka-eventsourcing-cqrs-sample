package de.openvalue.resilience.application;

import akka.actor.typed.ActorSystem;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import de.openvalue.resilience.domain.Order;
import de.openvalue.resilience.domain.OrderEntity;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import static de.openvalue.resilience.domain.OrderEntity.ENTITY_KEY;

public class OrderService {

    private final ClusterSharding sharding;
    private final Duration TIMEOUT = Duration.ofSeconds(30);

    public OrderService(ActorSystem<?> system) {
        this.sharding = ClusterSharding.get(system);
    }

    public CompletionStage<Order.State> handle(Order.Command.ProcessOrder command) {
        var entityRef = sharding.entityRefFor(ENTITY_KEY, UUID.randomUUID().toString());
        return entityRef.askWithStatus(replyTo -> command.withReplyTo(replyTo), TIMEOUT);
    }
}
