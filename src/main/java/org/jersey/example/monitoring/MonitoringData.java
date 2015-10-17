package org.jersey.example.monitoring;

import java.util.Map;

public class MonitoringData {

    private String node;
    private Map<String, Long> requestsPerResource;

    public void setNode(String node) {
        this.node = node;
    }

    public String getNode() {
        return node;
    }

    public Map<String, Long> getRequestsPerResource() {
        return requestsPerResource;
    }

    public void setRequestsPerResource(final Map<String, Long> requestsPerResource) {
        this.requestsPerResource = requestsPerResource;
    }

    @Override
    public String toString() {
        return "MonitoringData{" +
                "node='" + node + '\'' +
                ", requestsPerResource=" + requestsPerResource +
                '}';
    }
}
