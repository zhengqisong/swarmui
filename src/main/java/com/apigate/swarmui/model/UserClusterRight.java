package com.apigate.swarmui.model;

public class UserClusterRight extends UserInfo {
	String rights;
	Integer clusterid;
	
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public Integer getClusterid() {
		return clusterid;
	}
	public void setClusterid(Integer clusterid) {
		this.clusterid = clusterid;
	}
	
}
