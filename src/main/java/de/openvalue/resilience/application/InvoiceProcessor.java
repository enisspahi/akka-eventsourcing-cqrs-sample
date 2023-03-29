package de.openvalue.resilience.application;

import akka.projection.eventsourced.EventEnvelope;
import akka.projection.jdbc.JdbcSession;
import akka.projection.jdbc.javadsl.JdbcHandler;
import de.openvalue.resilience.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InvoiceProcessor extends JdbcHandler<EventEnvelope<Order.Event>, JdbcSession> {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceProcessor.class);

    @Override
    public void process(JdbcSession session, EventEnvelope<Order.Event> envelope) {
        switch (envelope.event()) {
            case Order.Event.OrderReceived event -> {
                var orderReceived = (Order.Event.OrderReceived) event;
                logger.info("Consumed order {}", orderReceived);
            }
        }
    }

}
