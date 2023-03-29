package de.openvalue.resilience;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.management.cluster.bootstrap.ClusterBootstrap;
import akka.management.javadsl.AkkaManagement;
import de.openvalue.resilience.application.OrderService;
import de.openvalue.resilience.infrastructure.HttpServer;
import de.openvalue.resilience.infrastructure.OrderEntitySharding;
import de.openvalue.resilience.infrastructure.ProjectionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResilienceSampleApplication {
  private static final Logger logger = LoggerFactory.getLogger(ResilienceSampleApplication.class);
  public static void main(String[] args) {
    var system = ActorSystem.create(Behaviors.empty(), "ResilienceSampleApplication");

    try {
      init(system);
    } catch (Exception e) {
      logger.error("Terminating due to initialization failure.", e);
      system.terminate();
    }

  }

  public static void init(ActorSystem<?> system) {
    AkkaManagement.get(system).start();
    ClusterBootstrap.get(system).start();
    OrderEntitySharding.init(system);
    HttpServer.init(system, new OrderService(system));
    ProjectionConfig.init(system);
  }

}
