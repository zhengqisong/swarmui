package com.apigate.swarmui.model;

public class SwarmService extends SwarmServiceBasic{

    private Integer clusterid;


    private Long cpus;

    private Long mems;

    private Integer replicas;

    private String config;

    public Integer getClusterid() {
        return clusterid;
    }

    public void setClusterid(Integer clusterid) {
        this.clusterid = clusterid;
    }

    public Long getCpus() {
        return cpus;
    }

    public void setCpus(Long cpus) {
        this.cpus = cpus;
    }

    public Long getMems() {
        return mems;
    }

    public void setMems(Long mems) {
        this.mems = mems;
    }

    public Integer getReplicas() {
        return replicas;
    }

    public void setReplicas(Integer replicas) {
        this.replicas = replicas;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config == null ? null : config.trim();
    }
}