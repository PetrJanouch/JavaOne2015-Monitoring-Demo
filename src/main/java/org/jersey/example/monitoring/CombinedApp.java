package org.jersey.example.monitoring;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;

public class CombinedApp {

    private static final URI App1URI = URI.create("http://localhost:8081/app");
    private static final URI App2URI = URI.create("http://localhost:8082/app");
    private static final URI App3URI = URI.create("http://localhost:8083/app");
    private static final URI MonitorUri = URI.create("http://localhost:8080/app");

    public static void main(String[] args) throws Exception {

        HttpServer monitoredServer1 = GrizzlyHttpServerFactory.createHttpServer(App1URI, new MonitoredApp(), false);
        HttpServer monitoredServer2 = GrizzlyHttpServerFactory.createHttpServer(App2URI, new MonitoredApp(), false);
        HttpServer monitoredServer3 = GrizzlyHttpServerFactory.createHttpServer(App3URI, new MonitoredApp(), false);

        monitoredServer1.start();
        monitoredServer2.start();
        monitoredServer3.start();

        List<URI> monitoredApps = Arrays.asList(App1URI, App2URI, App3URI);
        HttpServer monitoringServer = GrizzlyHttpServerFactory
                .createHttpServer(MonitorUri, new MonitoringApp(monitoredApps), false);

        HttpHandler httpHandler = new CLStaticHttpHandler(HttpServer.class.getClassLoader(), "/web/");
        monitoringServer.getServerConfiguration().addHttpHandler(httpHandler, "/");
        monitoringServer.start();

        System.out.println("Hit any key to stop");
        System.in.read();
        monitoringServer.shutdown();
        monitoredServer1.shutdown();
        monitoredServer2.shutdown();
        monitoredServer3.shutdown();
    }
}
