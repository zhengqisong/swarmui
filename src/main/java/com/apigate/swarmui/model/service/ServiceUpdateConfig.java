package com.apigate.swarmui.model.service;

public class ServiceUpdateConfig {
	Long parallelism;
	Long delay;
	String failureAction;
	public Long getParallelism() {
		return parallelism;
	}
	public void setParallelism(Long parallelism) {
		this.parallelism = parallelism;
	}
	public Long getDelay() {
		return delay;
	}
	public void setDelay(Long delay) {
		this.delay = delay;
	}
	public String getFailureAction() {
		return failureAction;
	}
	public void setFailureAction(String failureAction) {
		this.failureAction = failureAction;
	}
	
	
}
