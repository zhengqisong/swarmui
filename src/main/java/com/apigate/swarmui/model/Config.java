package com.apigate.swarmui.model;

public class Config {
    private String configid;

    private Integer clusterid;

    private String name;

    private String code;

    private String configData;
    private String  configDataCharsetName;
    
    public String getConfigid() {
        return configid;
    }

    public void setConfigid(String configid) {
        this.configid = configid == null ? null : configid.trim();
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

	public String getConfigData() {
		return configData;
	}

	public void setConfigData(String configData) {
		this.configData = configData;
	}

	public String getConfigDataCharsetName() {
		return configDataCharsetName;
	}

	public void setConfigDataCharsetName(String configDataCharsetName) {
		this.configDataCharsetName = configDataCharsetName;
	}
    
}