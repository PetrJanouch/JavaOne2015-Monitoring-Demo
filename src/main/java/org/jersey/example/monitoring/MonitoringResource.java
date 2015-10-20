package org.jersey.example.monitoring;

import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.inject.Inject;
import javax.inject.Provider;

import org.glassfish.jersey.server.monitoring.MonitoringStatistics;

@Path("monitoring")
public class MonitoringResource {

    @Inject
    private Provider<MonitoringStatistics> statistics;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public MonitoringData get() {
        MonitoringData monitoringData = new MonitoringData();
        Map<String, Long> rr = statistics.get()
                .getResourceClassStatistics()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getSimpleName(),
                        e -> e.getValue()
                                .getRequestExecutionStatistics()
                                .getTimeWindowStatistics()
                                .get(1000L)
                                .getRequestCount()));
        monitoringData.setRequestsPerResource(rr);
        return monitoringData;
    }
}
