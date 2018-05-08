package com.apigate.swarmui.model;

public class ClusterLabelKey {
    private Integer clusterid;

    private String labelkey;

    public Integer getClusterid() {
        return clusterid;
    }

    public void setClusterid(Integer clusterid) {
        this.clusterid = clusterid;
    }

    public String getLabelkey() {
        return labelkey;
    }

    public void setLabelkey(String labelkey) {
        this.labelkey = labelkey == null ? null : labelkey.trim();
    }
}