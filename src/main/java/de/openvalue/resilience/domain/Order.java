package de.openvalue.resilience.domain;

import akka.actor.typed.ActorRef;
import akka.pattern.StatusReply;

import java.util.List;

public interface Order {

    sealed interface Command {
        record ProcessOrder(String email, List<OrderItem> items, ActorRef<StatusReply<State>> replyTo) implements Command {
            public ProcessOrder withReplyTo(ActorRef<StatusReply<State>> replyTo) {
                return new ProcessOrder(email, items, replyTo);
            }
        }
    }

    sealed interface Event {
        record OrderReceived(String email, List<OrderItem> items) implements Event {
            static OrderReceived from(Command.ProcessOrder command) {
                return new OrderReceived(command.email(), command.items());
            }
        }
    }

    record OrderItem(String name, Integer count) { }

    record State(String id, String email, List<OrderItem> items) {
        State(String id) {
            this(id, null, null);
        }
        public
        State mutate(Order.Event.OrderReceived event) {
            return new State(id, event.email, event.items);
        }
    }

}
