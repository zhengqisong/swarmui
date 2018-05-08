package com.apigate.swarmui.model.service;

public class ServiceRestartPolicy {
	String condition;
	Long delay;
	Integer maxAttempts;
	Long window;
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public Long getDelay() {
		return delay;
	}
	public void setDelay(Long delay) {
		this.delay = delay;
	}
	public Integer getMaxAttempts() {
		return maxAttempts;
	}
	public void setMaxAttempts(Integer maxAttempts) {
		this.maxAttempts = maxAttempts;
	}
	public Long getWindow() {
		return window;
	}
	public void setWindow(Long window) {
		this.window = window;
	}
	
	
}
