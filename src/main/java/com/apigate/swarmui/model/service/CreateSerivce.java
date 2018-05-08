package com.apigate.swarmui.model.service;

import java.util.Map;

public class CreateSerivce {
	String name;
	String mode;
	Integer replicas;
	Map<String,String> labels;
	ServiceEndpointSpec[] endpointSpec;
	TaskTemplate template;
	ServiceUpdateConfig updateConfig;
	
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
	public Integer getReplicas() {
		return replicas;
	}
	public void setReplicas(Integer replicas) {
		this.replicas = replicas;
	}
	public Map<String, String> getLabels() {
		return labels;
	}
	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}
	public ServiceEndpointSpec[] getEndpointSpec() {
		return endpointSpec;
	}
	public void setEndpointSpec(ServiceEndpointSpec[] endpointSpec) {
		this.endpointSpec = endpointSpec;
	}
	public TaskTemplate getTemplate() {
		return template;
	}
	public void setTemplate(TaskTemplate template) {
		this.template = template;
	}
	public ServiceUpdateConfig getUpdateConfig() {
		return updateConfig;
	}
	public void setUpdateConfig(ServiceUpdateConfig updateConfig) {
		this.updateConfig = updateConfig;
	}	
}
