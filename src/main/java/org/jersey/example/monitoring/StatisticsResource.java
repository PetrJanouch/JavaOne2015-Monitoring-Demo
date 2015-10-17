package org.jersey.example.monitoring;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.inject.Inject;

import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

@Path("statistics")
public class StatisticsResource {

    private static final SseBroadcaster broadcaster = new SseBroadcaster();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final Client client = ClientBuilder.newClient();

    @Inject
    private MonitoringApp monitoringApp;

    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput get() {
        EventOutput output = new EventOutput();
        broadcaster.add(output);
        scheduler.scheduleAtFixedRate(this::broadcastStatistics, 0, 1000, TimeUnit.MILLISECONDS);
        return output;
    }

    private void broadcastStatistics() {
        List<URI> monitoringEndpoints = monitoringApp.getMonitoredApps();
        List<MonitoringData> monitoringData = monitoringEndpoints.stream().map((endpointUri) -> {
            Response response = client.target(endpointUri).path("monitoring").request().get();
            MonitoringData data = response.readEntity(MonitoringData.class);
            data.setNode(endpointUri.getHost() + ":" + endpointUri.getPort());
            return data;
        }).collect(Collectors.toList());

        OutboundEvent event = new OutboundEvent.Builder().mediaType(MediaType.APPLICATION_JSON_TYPE).data(monitoringData).build();
        broadcaster.broadcast(event);
    }
}
