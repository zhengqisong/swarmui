package com.apigate.swarmui.model.service;

import java.util.Map;

public class ServiceLogDriver {
	String name;
	Map<String,String> options;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, String> getOptions() {
		return options;
	}
	public void setOptions(Map<String, String> options) {
		this.options = options;
	}
	
	
}
