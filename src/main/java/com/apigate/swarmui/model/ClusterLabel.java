package com.apigate.swarmui.model;

public class ClusterLabel {
    private Integer labelid;

    private Integer clusterid;

    private String labelname;

    private String labelkey;

    private String labelvalue;

    public Integer getLabelid() {
        return labelid;
    }

    public void setLabelid(Integer labelid) {
        this.labelid = labelid;
    }

    public Integer getClusterid() {
        return clusterid;
    }

    public void setClusterid(Integer clusterid) {
        this.clusterid = clusterid;
    }

    public String getLabelname() {
        return labelname;
    }

    public void setLabelname(String labelname) {
        this.labelname = labelname == null ? null : labelname.trim();
    }

    public String getLabelkey() {
        return labelkey;
    }

    public void setLabelkey(String labelkey) {
        this.labelkey = labelkey == null ? null : labelkey.trim();
    }

    public String getLabelvalue() {
        return labelvalue;
    }

    public void setLabelvalue(String labelvalue) {
        this.labelvalue = labelvalue == null ? null : labelvalue.trim();
    }
}