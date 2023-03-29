package de.openvalue.resilience.infrastructure;

import akka.actor.typed.ActorSystem;
import akka.cluster.typed.ClusterSingleton;
import akka.cluster.typed.SingletonActor;
import akka.persistence.jdbc.query.javadsl.JdbcReadJournal;
import akka.persistence.query.Offset;
import akka.projection.ProjectionBehavior;
import akka.projection.ProjectionId;
import akka.projection.eventsourced.EventEnvelope;
import akka.projection.eventsourced.javadsl.EventSourcedProvider;
import akka.projection.javadsl.SourceProvider;
import akka.projection.jdbc.javadsl.JdbcProjection;
import de.openvalue.resilience.application.InvoiceProcessor;
import de.openvalue.resilience.domain.Order;
import de.openvalue.resilience.domain.OrderEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class ProjectionConfig {

    public static void init(ActorSystem<?> system) {
        SourceProvider<Offset, EventEnvelope<Order.Event>> sourceProvider =
                EventSourcedProvider.eventsByTag(
                        system,
                        JdbcReadJournal.Identifier(),
                        OrderEntity.TAG);

        var projection = JdbcProjection.exactlyOnce(
                ProjectionId.of(InvoiceProcessor.class.getName(), OrderEntity.TAG),
                sourceProvider,
                () -> new PlainJdbcSession(),
                () -> new InvoiceProcessor(javaMailSender()),
                system);

        // Make sure that projection runs only once
        ClusterSingleton.get(system)
                .init(SingletonActor
                        .of(ProjectionBehavior.create(projection), projection.projectionId().id())
                        .withStopMessage(ProjectionBehavior.stopMessage()));
    }

    private static JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("");
        mailSender.setPassword("");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return mailSender;
    }
}
