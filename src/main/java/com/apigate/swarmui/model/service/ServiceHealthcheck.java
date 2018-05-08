package com.apigate.swarmui.model.service;

import java.util.List;

public class ServiceHealthcheck {
	List<String> test;
	long interval;
	long timeout;
	int retries;
	long startPeriod;
	
	public List<String> getTest() {
		return test;
	}
	public void setTest(List<String> test) {
		this.test = test;
	}
	public long getInterval() {
		return interval;
	}
	public void setInterval(long interval) {
		this.interval = interval;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public int getRetries() {
		return retries;
	}
	public void setRetries(int retries) {
		this.retries = retries;
	}
	public long getStartPeriod() {
		return startPeriod;
	}
	public void setStartPeriod(long startPeriod) {
		this.startPeriod = startPeriod;
	}
	
}
