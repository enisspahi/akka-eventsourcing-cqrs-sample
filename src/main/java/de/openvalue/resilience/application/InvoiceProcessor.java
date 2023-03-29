package de.openvalue.resilience.application;

import akka.projection.eventsourced.EventEnvelope;
import akka.projection.jdbc.JdbcSession;
import akka.projection.jdbc.javadsl.JdbcHandler;
import de.openvalue.resilience.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public final class InvoiceProcessor extends JdbcHandler<EventEnvelope<Order.Event>, JdbcSession> {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceProcessor.class);

    private final JavaMailSender mailSender;

    public InvoiceProcessor(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void process(JdbcSession session, EventEnvelope<Order.Event> envelope) {
        switch (envelope.event()) {
            case Order.Event.OrderReceived event -> sendEmailFor(event);
        }
    }

    private void sendEmailFor(Order.Event.OrderReceived event) {
        logger.info("Sending email for received order {}", event);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.email());
        message.setSubject("Invoice of your order");
        message.setText(String.format("The invoice for your order %s. Ignore this email if you have already received before.", event.items()));
        mailSender.send(message);
    }

}
