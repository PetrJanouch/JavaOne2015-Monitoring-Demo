package org.jersey.example.monitoring;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.server.ResourceConfig;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;

public class MonitoringApp extends ResourceConfig {

    private final List<URI> monitoredApps;

    public MonitoringApp(List<URI> monitoredApps) {
        super(StatisticsResource.class, SseFeature.class);

        this.monitoredApps = monitoredApps;

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(MonitoringApp.this);
            }
        });
    }

    public List<URI> getMonitoredApps() {
        return monitoredApps;
    }

    public static void main(String[] args) throws Exception {
        Arrays.asList(args).forEach(System.out::println);
        List<URI> monitoredApps = Arrays.asList(args).stream().map(URI::create)
                .map(uri -> URI.create("http://" + uri.getHost() + ":" + uri.getPort() + "/app")).collect(Collectors.toList());

        HttpServer monitoringServer = GrizzlyHttpServerFactory
                .createHttpServer(URI.create("http://0.0.0.0:8080/app"), new MonitoringApp(monitoredApps), false);

        HttpHandler httpHandler = new CLStaticHttpHandler(HttpServer.class.getClassLoader(), "/web/");
        monitoringServer.getServerConfiguration().addHttpHandler(httpHandler, "/");
        monitoringServer.start();

        Thread.currentThread().join();
    }
}
