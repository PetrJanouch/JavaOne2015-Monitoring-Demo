package org.jersey.example.monitoring;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/resource2")
public class MonitoredResource2 {

    @GET
    public String getHello() {
        return "Hello from resource 2";
    }
}
