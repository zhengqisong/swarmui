package com.apigate.swarmui.model;

public class Cluster {
    private Integer clusterid;

    private String name;

    private String baseUrl;

    private String version;

    private String capem;

    private String certpem;

    private String keypem;

    private String remark;

    private Integer maxcpus;

    private Integer maxmem;

    private Integer maxinstance;

    private String status;

    public Integer getClusterid() {
        return clusterid;
    }

    public void setClusterid(Integer clusterid) {
        this.clusterid = clusterid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl == null ? null : baseUrl.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getCapem() {
        return capem;
    }

    public void setCapem(String capem) {
        this.capem = capem == null ? null : capem.trim();
    }

    public String getCertpem() {
        return certpem;
    }

    public void setCertpem(String certpem) {
        this.certpem = certpem == null ? null : certpem.trim();
    }

    public String getKeypem() {
        return keypem;
    }

    public void setKeypem(String keypem) {
        this.keypem = keypem == null ? null : keypem.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getMaxcpus() {
        return maxcpus;
    }

    public void setMaxcpus(Integer maxcpus) {
        this.maxcpus = maxcpus;
    }

    public Integer getMaxmem() {
        return maxmem;
    }

    public void setMaxmem(Integer maxmem) {
        this.maxmem = maxmem;
    }

    public Integer getMaxinstance() {
        return maxinstance;
    }

    public void setMaxinstance(Integer maxinstance) {
        this.maxinstance = maxinstance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}