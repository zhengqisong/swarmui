package com.apigate.swarmui.model.service;

public class TaskCriteria {
	String nodeId;
	String serviceName;
	String desiredState;
	String taskId;
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getDesiredState() {
		return desiredState;
	}
	public void setDesiredState(String desiredState) {
		this.desiredState = desiredState;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	
}
