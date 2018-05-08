package com.apigate.swarmui.model;

public class UserVolumeRight extends UserInfo {
	Integer id;
	String rights;
	String isowner;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRights() {
		return rights;
	}
	public void setRights(String rights) {
		this.rights = rights;
	}
	public String getIsowner() {
		return isowner;
	}
	public void setIsowner(String isowner) {
		this.isowner = isowner;
	}
	
	
}
