package com.apigate.swarmui.model;

public class Secret {
    private String secretid;

    private Integer clusterid;

    private String name;

    private String code;
    
    private String secretData;
    
    private String secretDataCharsetName;
    
    public String getSecretid() {
		return secretid;
	}

	public void setSecretid(String secretid) {
		this.secretid = secretid;
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
		this.code = code;
	}

	public String getSecretData() {
		return secretData;
	}

	public void setSecretData(String secretData) {
		this.secretData = secretData;
	}

	public String getSecretDataCharsetName() {
		return secretDataCharsetName;
	}

	public void setSecretDataCharsetName(String secretDataCharsetName) {
		this.secretDataCharsetName = secretDataCharsetName;
	}
    
    
}