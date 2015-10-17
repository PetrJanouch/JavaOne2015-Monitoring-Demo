package org.jersey.example.monitoring;

import java.net.URI;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import org.glassfish.grizzly.http.server.HttpServer;

public class MonitoredApp extends ResourceConfig {

    public MonitoredApp() {
        super(MonitoredResource1.class, MonitoredResource2.class, MonitoringResource.class, JacksonFeature.class);
        property(ServerProperties.MONITORING_STATISTICS_ENABLED, true);
    }

    public static void main(String[] args) throws Exception {
        HttpServer monitoredServer1 =
                GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:8080/app"), new MonitoredApp(), false);
        monitoredServer1.start();

        Thread.currentThread().join();
    }
}
