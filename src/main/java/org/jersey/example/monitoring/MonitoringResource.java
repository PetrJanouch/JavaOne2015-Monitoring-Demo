package org.jersey.example.monitoring;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.inject.Inject;
import javax.inject.Provider;

import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;

@Path("monitoring")
public class MonitoringResource {

    @Inject
    private Provider<MonitoringStatistics> statistics;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public MonitoringData get() {
        MonitoringData monitoringData = new MonitoringData();
        Map<String, Long> rr = new HashMap<>();
        monitoringData.setRequestsPerResource(rr);
        MonitoringStatistics monitoringStatistics = statistics.get();

        Map<Class<?>, ResourceStatistics> resourceClassStatistics = monitoringStatistics.getResourceClassStatistics();
        resourceClassStatistics.forEach((aClass, resourceStatistics) -> rr
                .put(aClass.getSimpleName(), resourceStatistics.getRequestExecutionStatistics().getTimeWindowStatistics()
                        .get(1000L).getRequestCount()));
        return monitoringData;
    }
}
