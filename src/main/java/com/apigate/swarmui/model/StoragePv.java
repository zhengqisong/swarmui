package com.apigate.swarmui.model;

public class StoragePv {
    private Integer pvid;

    private Integer clusterid;

    private String name;

    private String storagetype;

    private String localpath;

    private Integer storagesize;

    private Integer volumes;

    private String remark;
    
    private String containername;
    
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

    public String getStoragetype() {
        return storagetype;
    }

    public void setStoragetype(String storagetype) {
        this.storagetype = storagetype == null ? null : storagetype.trim();
    }

    public String getLocalpath() {
        return localpath;
    }

    public void setLocalpath(String localpath) {
        this.localpath = localpath == null ? null : localpath.trim();
    }

    public Integer getStoragesize() {
        return storagesize;
    }

    public void setStoragesize(Integer storagesize) {
        this.storagesize = storagesize;
    }

    public Integer getVolumes() {
        return volumes;
    }

    public void setVolumes(Integer volumes) {
        this.volumes = volumes;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

	public String getContainername() {
		return containername;
	}

	public void setContainername(String containername) {
		this.containername = containername;
	}
    
}