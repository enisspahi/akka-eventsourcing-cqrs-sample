package de.openvalue.resilience.adapters.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.openvalue.resilience.domain.Order;

import java.util.List;
import java.util.stream.Collectors;

// TODO named??
public record OrderResource(@JsonProperty("email") String email,
                            @JsonProperty("items") List<OrderItem> items,
                            @JsonProperty("id") String id) {

    public record OrderItem(@JsonProperty("name") String name, @JsonProperty("count") Integer count) {
        Order.OrderItem toDomain() {
            return new Order.OrderItem(name, count);
        }

        static OrderItem fromDomain(Order.OrderItem orderItem) {
            return new OrderItem(orderItem.name(), orderItem.count());
        }
    }


    public Order.Command.ProcessOrder toCommand() {
        return new Order.Command.ProcessOrder(
                email,
                items.stream().map(OrderItem::toDomain).collect(Collectors.toList()),
                null);
    }

    public static OrderResource fromState(Order.State state) {
        return new OrderResource(
                state.email(),
                state.items().stream().map(OrderItem::fromDomain).collect(Collectors.toList()),
                state.id());
    }

}
