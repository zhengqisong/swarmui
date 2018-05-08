package com.apigate.swarmui.model.service;

public class ServiceEndpointSpec {
	String name;
	String mode;
	String protocol;
	Integer publishedPort;
	Integer targetPort;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public Integer getPublishedPort() {
		return publishedPort;
	}
	public void setPublishedPort(Integer publishedPort) {
		this.publishedPort = publishedPort;
	}
	public Integer getTargetPort() {
		return targetPort;
	}
	public void setTargetPort(Integer targetPort) {
		this.targetPort = targetPort;
	}
}
