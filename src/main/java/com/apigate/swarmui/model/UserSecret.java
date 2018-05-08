package com.apigate.swarmui.model;

public class UserSecret {
    private Integer userid;

    private String secretid;

    private String isowner;

    private String rights;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getSecretid() {
		return secretid;
	}

	public void setSecretid(String secretid) {
		this.secretid = secretid;
	}

	public String getIsowner() {
        return isowner;
    }

    public void setIsowner(String isowner) {
        this.isowner = isowner == null ? null : isowner.trim();
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights == null ? null : rights.trim();
    }
}