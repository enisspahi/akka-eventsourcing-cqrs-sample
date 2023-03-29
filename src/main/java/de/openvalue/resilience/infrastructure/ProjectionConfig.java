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
                InvoiceProcessor::new,
                system);

        // Make sure that projection runs only once
        ClusterSingleton.get(system)
                .init(SingletonActor
                        .of(ProjectionBehavior.create(projection), projection.projectionId().id())
                        .withStopMessage(ProjectionBehavior.stopMessage()));
    }
}
