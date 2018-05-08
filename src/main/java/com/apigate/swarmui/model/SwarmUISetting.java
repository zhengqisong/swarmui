package com.apigate.swarmui.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("swarmui")
public class SwarmUISetting {
	String clusterCertPath;

	public String getClusterCertPath() {
		return clusterCertPath;
	}

	public void setClusterCertPath(String clusterCertPath) {
		this.clusterCertPath = clusterCertPath;
	}

}
