package com.apigate.swarmui.model;

public class ServiceNetworkRelation {
    private String serviceid;

    private String networkid;

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid == null ? null : serviceid.trim();
    }

    public String getNetworkid() {
        return networkid;
    }

    public void setNetworkid(String networkid) {
        this.networkid = networkid == null ? null : networkid.trim();
    }
}