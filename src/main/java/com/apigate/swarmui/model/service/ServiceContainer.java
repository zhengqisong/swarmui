package com.apigate.swarmui.model.service;

import java.util.List;
import java.util.Map;

public class ServiceContainer {
	String image;
	List<String> command;
	List<String> args;
	List<String> env;
	Map<String, String> labels;
	List<ServiceConfig> configs;
	List<ServiceSecret> secrets;
	ServiceHealthcheck healthcheck;
	List<ServiceMount> mounts;
	
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public List<String> getCommand() {
		return command;
	}
	public void setCommand(List<String> command) {
		this.command = command;
	}
	public List<String> getArgs() {
		return args;
	}
	public void setArgs(List<String> args) {
		this.args = args;
	}
	public List<String> getEnv() {
		return env;
	}
	public void setEnv(List<String> env) {
		this.env = env;
	}
	public Map<String, String> getLabels() {
		return labels;
	}
	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}
	public List<ServiceConfig> getConfigs() {
		return configs;
	}
	public void setConfigs(List<ServiceConfig> configs) {
		this.configs = configs;
	}
	public List<ServiceSecret> getSecrets() {
		return secrets;
	}
	public void setSecrets(List<ServiceSecret> secrets) {
		this.secrets = secrets;
	}
	public ServiceHealthcheck getHealthcheck() {
		return healthcheck;
	}
	public void setHealthcheck(ServiceHealthcheck healthcheck) {
		this.healthcheck = healthcheck;
	}
	public List<ServiceMount> getMounts() {
		return mounts;
	}
	public void setMounts(List<ServiceMount> mounts) {
		this.mounts = mounts;
	}
	
}
