package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.docker.client.DockerSwarmExecClient;
import com.apigate.swarmui.mapper.StoragePvMapper;
import com.apigate.swarmui.mapper.StorageVolumeMapper;
import com.apigate.swarmui.mapper.UserVolumeMapper;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.StoragePv;
import com.apigate.swarmui.model.StoragePvUsed;
import com.apigate.swarmui.model.StorageVolume;
import com.apigate.swarmui.model.StorageVolumeCount;
import com.apigate.swarmui.model.UserClusterKey;
import com.apigate.swarmui.model.UserInfo;
import com.apigate.swarmui.model.UserVolume;
import com.spotify.docker.client.DockerClient;

@Service
public class StorageVolumeSvc {
	
	@Autowired
	StorageVolumeMapper storageVolumeMapper;
	@Autowired
	UserVolumeMapper userVolumeMapper;
	@Autowired
	StoragePvMapper storagePvMapper;
	
	@Autowired
	ClusterClientSvc clusterClientSvc;
	
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	public List<StorageVolume> list(Integer clusterid) {
		return storageVolumeMapper.selectByClusterId(clusterid);
	}
	
	public List<StorageVolume> selectByUserClusterKey(int clusterid,int userid){
		UserClusterKey userClusterKey = new UserClusterKey();
		userClusterKey.setClusterid(clusterid);
		userClusterKey.setUserid(userid);
		return storageVolumeMapper.selectByUserClusterKey(userClusterKey);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public Integer create(StorageVolume storageVolume) throws Exception {
		StoragePv storagePv = storagePvMapper.selectByPrimaryKey(storageVolume.getPvid());
		if(storagePv == null){
			throw new Exception(MessageManager.getMsg("storagepv.not_exist"));
		}
		if(storageVolume.getStoragesize()==null || storageVolume.getStoragesize()<=0){			
			throw new Exception(MessageManager.getMsg("common.can_not_empty"));
		}
		StoragePvUsed storagePvUsed = storageVolumeMapper.countByPvid(storageVolume.getPvid());
		if(storagePvUsed != null){
			//判断存储实例数是否超过最大限度
			if(storagePv.getVolumes() != null && storagePv.getVolumes() > 0){
				if(storagePvUsed.getVolumes()+1>storagePv.getVolumes()){
					throw new Exception(MessageManager.getMsg("storagepv.no_resource"));
				}
			}
			//判断存储是否超过最大限度
			if(storagePv.getStoragesize() != null && storagePv.getStoragesize() > 0){
				if(storagePvUsed.getStoragesize()+storageVolume.getStoragesize()>storagePv.getStoragesize()){
					throw new Exception(MessageManager.getMsg("storagepv.no_resource"));
				}
			}
		}
		
		UserInfo userInfo = currentUserSvc.getCurrentSessionUser();
		
		storageVolume.setCode(clusterClientSvc.randomName());
		//创建存储目录
		DockerClient docker = clusterClientSvc.getDockerClient(storagePv.getClusterid());
		String message = DockerSwarmExecClient.createDir(docker, storagePv.getContainername().trim(), storagePv.getLocalpath()+"/"+storageVolume.getCode());
				
		storageVolumeMapper.insert(storageVolume);
		
		UserVolume userVolume = new UserVolume();
		userVolume.setVolumeid(storageVolume.getVolumeid());
		userVolume.setUserid(userInfo.getUserid());
		userVolume.setIsowner(Dictionaries.boolean_type_yes.getKey());
		userVolume.setRights(Dictionaries.right_type_r.getKey()+Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey());
		userVolumeMapper.insert(userVolume);
		
		return storageVolume.getVolumeid();
	}
	
	public StorageVolume info(Integer volumeid) throws Exception {
		return storageVolumeMapper.selectByPrimaryKey(volumeid);
	}
	
	public StorageVolume selectByCode(String code) throws Exception {
		return storageVolumeMapper.selectByCode(code);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void delete(Integer volumeid) throws Exception {
		StorageVolume volume = storageVolumeMapper.selectByPrimaryKey(volumeid);
		if(volume==null){
			throw new Exception(MessageManager.getMsg("common.nofound_error"));
		}
		StoragePv storagePv = storagePvMapper.selectByPrimaryKey(volume.getPvid());
		if(storagePv == null){
			throw new Exception(MessageManager.getMsg("storagepv.not_exist"));
		}
		//正在使用的不允许删除
		userVolumeMapper.deleteByVolumeid(volumeid);
		storageVolumeMapper.deleteByPrimaryKey(volumeid);
		DockerClient docker = clusterClientSvc.getDockerClient(storagePv.getClusterid());
		String message = DockerSwarmExecClient.rmDir(docker, storagePv.getContainername().trim(), storagePv.getLocalpath()+"/"+volume.getCode());
		
	}
	
	public void update(StorageVolume storageVolume) throws Exception {
		StorageVolume volume = storageVolumeMapper.selectByPrimaryKey(storageVolume.getVolumeid());
		if(volume==null){
			throw new Exception(MessageManager.getMsg("common.nofound_error"));
		}
		
		if(storageVolume.getStoragesize()==null || storageVolume.getStoragesize()<=0){			
			throw new Exception(MessageManager.getMsg("common.can_not_empty"));
		}
		
		StoragePv storagePv = storagePvMapper.selectByPrimaryKey(volume.getPvid());
		if(storagePv == null){
			throw new Exception(MessageManager.getMsg("storagepv.not_exist"));
		}
		
		StoragePvUsed storagePvUsed = storageVolumeMapper.countByPvid(volume.getPvid());
		if(storagePvUsed != null){
			storagePvUsed.setVolumes(storagePvUsed.getVolumes()-1);
			storagePvUsed.setStoragesize(storagePvUsed.getStoragesize()-volume.getStoragesize());
			
			//判断存储实例数是否超过最大限度
			if(storagePv.getVolumes() != null && storagePv.getVolumes() > 0){
				if(storagePvUsed.getVolumes()+1>storagePv.getVolumes()){
					throw new Exception(MessageManager.getMsg("storagepv.no_resource"));
				}
			}
			//判断存储是否超过最大限度
			if(storagePv.getStoragesize() != null && storagePv.getStoragesize() > 0){
				if(storagePvUsed.getStoragesize()+storageVolume.getStoragesize()>storagePv.getStoragesize()){
					throw new Exception(MessageManager.getMsg("storagepv.no_resource"));
				}
			}
		}
		storageVolume.setCode(volume.getCode());
		storageVolume.setClusterid(volume.getClusterid());
		storageVolume.setPvid(volume.getPvid());
		storageVolumeMapper.updateByPrimaryKey(storageVolume);
		return;
	}
	
	public StorageVolumeCount countByClusterId(Integer clusterid){
		return storageVolumeMapper.countByClusterId(clusterid);
	}
	
	public StorageVolumeCount countByUserClusterKey(int clusterid,int userid){
		UserClusterKey userClusterKey = new UserClusterKey();
		userClusterKey.setClusterid(clusterid);
		userClusterKey.setUserid(userid);
		return storageVolumeMapper.countByUserClusterKey(userClusterKey);
	}
}
