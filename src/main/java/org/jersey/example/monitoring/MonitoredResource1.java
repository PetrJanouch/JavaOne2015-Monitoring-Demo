package org.jersey.example.monitoring;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/resource1")
public class MonitoredResource1 {

    @GET
    public String getHello() {
        return "Hello from resource 1";
    }
}
