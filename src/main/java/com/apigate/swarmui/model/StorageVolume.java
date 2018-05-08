package com.apigate.swarmui.model;

public class StorageVolume {
    private Integer volumeid;

    private Integer pvid;

    private Integer clusterid;

    private String name;

    private Integer storagesize;

    private String code;
    
    private String localpath;
    
    public Integer getVolumeid() {
        return volumeid;
    }

    public void setVolumeid(Integer volumeid) {
        this.volumeid = volumeid;
    }

    public Integer getPvid() {
        return pvid;
    }

    public void setPvid(Integer pvid) {
        this.pvid = pvid;
    }

    public Integer getClusterid() {
        return clusterid;
    }

    public void setClusterid(Integer clusterid) {
        this.clusterid = clusterid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getStoragesize() {
        return storagesize;
    }

    public void setStoragesize(Integer storagesize) {
        this.storagesize = storagesize;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

	public String getLocalpath() {
		return localpath;
	}

	public void setLocalpath(String localpath) {
		this.localpath = localpath;
	}
    
    
}