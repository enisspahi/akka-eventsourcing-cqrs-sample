package de.openvalue.resilience.domain;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import akka.pattern.StatusReply;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import de.openvalue.resilience.domain.Order.Command;
import de.openvalue.resilience.domain.Order.Event;
import de.openvalue.resilience.domain.Order.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public final class OrderEntity extends EventSourcedBehavior<Command, Event, State> {

    private static final Logger logger = LoggerFactory.getLogger(OrderEntity.class);

    private final String id;

    private OrderEntity(String id) {
        super(PersistenceId.ofUniqueId(id));
        this.id = id;
    }

    public static Behavior<Command> create(String id) {
        return Behaviors.setup(
                ctx -> EventSourcedBehavior.start(new OrderEntity(id), ctx));
    }

    @Override
    public State emptyState() {
        return new State(id);
    }

    @Override
    public CommandHandler<Command, Event, State> commandHandler() {
        return newCommandHandlerBuilder()
                .forAnyState()
                .onCommand(Command.ProcessOrder.class, this::onProcessOrder)
                .build();
    }

    private Effect<Event, State> onProcessOrder(Command.ProcessOrder command) {
        logger.info("Handling command {}", command);
        return Effect()
                .persist(Event.OrderReceived.from(command))
                .thenReply(command.replyTo(), state -> StatusReply.success(state));
    }

    @Override
    public EventHandler<State, Event> eventHandler() {
        return newEventHandlerBuilder()
                .forAnyState()
                .onEvent(Event.OrderReceived.class, (state, event) -> state.mutate(event))
                .build();
    }

    public static final EntityTypeKey<Order.Command> ENTITY_KEY = EntityTypeKey.create(Order.Command.class, "Order");

    public static final String TAG = "ORDER_TAG";

    @Override
    public Set<String> tagsFor(Event event) {
        return Collections.singleton(TAG);
    }

}
