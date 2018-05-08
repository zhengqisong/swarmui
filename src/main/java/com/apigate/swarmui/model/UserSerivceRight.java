package com.apigate.swarmui.model;

public class UserSerivceRight extends UserInfo {
	String id;
	String rights;
	String isowner;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
