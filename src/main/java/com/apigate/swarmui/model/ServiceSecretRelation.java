package com.apigate.swarmui.model;

public class ServiceSecretRelation {
    private String serviceid;

    private String secretid;

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid == null ? null : serviceid.trim();
    }

    public String getSecretid() {
        return secretid;
    }

    public void setSecretid(String secretid) {
        this.secretid = secretid == null ? null : secretid.trim();
    }
}