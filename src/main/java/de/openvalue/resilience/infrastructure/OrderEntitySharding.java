package de.openvalue.resilience.infrastructure;

import akka.actor.typed.ActorSystem;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import de.openvalue.resilience.application.OrderService;
import de.openvalue.resilience.domain.OrderEntity;

import static de.openvalue.resilience.domain.OrderEntity.ENTITY_KEY;

public class OrderEntitySharding {

    public static void init(ActorSystem<?> system) {
        ClusterSharding.get(system)
                .init(
                        Entity.of(
                                ENTITY_KEY,
                                entityContext -> OrderEntity.create(entityContext.getEntityId())
                        )
                );
    }
}
