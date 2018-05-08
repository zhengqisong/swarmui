package com.apigate.swarmui.model.service;

import java.util.List;

public class TaskTemplate {
	ServiceResources resources;
	ServiceRestartPolicy restartPolicy;
	List<String> placement;
	List<String> network;
	ServiceLogDriver logDriver;
	ServiceContainer container;
	
	public ServiceResources getResources() {
		return resources;
	}
	public void setResources(ServiceResources resources) {
		this.resources = resources;
	}
	public ServiceRestartPolicy getRestartPolicy() {
		return restartPolicy;
	}
	public void setRestartPolicy(ServiceRestartPolicy restartPolicy) {
		this.restartPolicy = restartPolicy;
	}
	public List<String> getPlacement() {
		return placement;
	}
	public void setPlacement(List<String> placement) {
		this.placement = placement;
	}
	public List<String> getNetwork() {
		return network;
	}
	public void setNetwork(List<String> network) {
		this.network = network;
	}
	public ServiceLogDriver getLogDriver() {
		return logDriver;
	}
	public void setLogDriver(ServiceLogDriver logDriver) {
		this.logDriver = logDriver;
	}
	public ServiceContainer getContainer() {
		return container;
	}
	public void setContainer(ServiceContainer container) {
		this.container = container;
	}	
}
