package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.apigate.swarmui.docker.client.DockerSwarmServiceClient;
import com.apigate.swarmui.mapper.ServiceConfigRelationMapper;
import com.apigate.swarmui.mapper.ServiceNetworkRelationMapper;
import com.apigate.swarmui.mapper.ServiceSecretRelationMapper;
import com.apigate.swarmui.mapper.ServiceVolumeRelationMapper;
import com.apigate.swarmui.mapper.SwarmServiceMapper;
import com.apigate.swarmui.mapper.UserServiceMapper;
import com.apigate.swarmui.model.ClusterIdNameKey;
import com.apigate.swarmui.model.Config;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Network;
import com.apigate.swarmui.model.Secret;
import com.apigate.swarmui.model.ServiceConfigRelation;
import com.apigate.swarmui.model.ServiceNetworkRelation;
import com.apigate.swarmui.model.ServiceSecretRelation;
import com.apigate.swarmui.model.ServiceVolumeRelation;
import com.apigate.swarmui.model.StorageVolume;
import com.apigate.swarmui.model.SwarmService;
import com.apigate.swarmui.model.SwarmServiceMemCpuCount;
import com.apigate.swarmui.model.UserClusterKey;
import com.apigate.swarmui.model.UserService;
import com.apigate.swarmui.model.service.CreateSerivce;
import com.apigate.swarmui.model.service.CreateServiceCodeResource;
import com.apigate.swarmui.model.service.ServiceConfig;
import com.apigate.swarmui.model.service.ServiceMount;
import com.apigate.swarmui.model.service.ServiceSecret;
import com.apigate.swarmui.model.service.TaskCriteria;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.ServiceNotFoundException;
import com.spotify.docker.client.messages.swarm.Task;

@Service
public class ServiceSvc {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	ClusterClientSvc clusterClientSvc;
	
	@Autowired
	SwarmServiceMapper serviceMapper;
	
	@Autowired
	UserServiceMapper userServiceMapper;
	
	@Autowired
	ServiceVolumeRelationMapper serviceVolumeRelationMapper;
	@Autowired
	ServiceNetworkRelationMapper serviceNetworkRelationMapper;
	@Autowired
	ServiceSecretRelationMapper serviceSecretRelationMapper;
	@Autowired
	ServiceConfigRelationMapper serviceConfigRelationMapper;
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public String create(Integer clusterid, CreateSerivce createService, CreateServiceCodeResource cscr) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		try{
			//写db
			SwarmService service = new SwarmService();
			service.setClusterid(clusterid);
			service.setName(createService.getName());
			if(createService.getTemplate().getResources()!=null 
					&& createService.getTemplate().getResources().getLimit()!=null 
					&& createService.getTemplate().getResources().getLimit().getCpu()!=null
					&& createService.getTemplate().getResources().getLimit().getCpu()>0){
				service.setCpus(createService.getTemplate().getResources().getLimit().getCpu());
			}
			if(createService.getTemplate().getResources()!=null 
					&& createService.getTemplate().getResources().getLimit()!=null 
					&& createService.getTemplate().getResources().getLimit().getMemory()!=null
					&& createService.getTemplate().getResources().getLimit().getMemory()>0){
				service.setMems(createService.getTemplate().getResources().getLimit().getMemory());
			}
			service.setReplicas(createService.getReplicas());
			service.setConfig(JSON.toJSONString(createService));
			
			String serviceid = DockerSwarmServiceClient.createService(docker, createService, cscr);
			service.setServiceid(serviceid);
			
			serviceMapper.insert(service);
			
			//写db user service releation
			UserService userService = new UserService();
			userService.setIsowner(Dictionaries.boolean_type_yes.getKey());
			userService.setRights(Dictionaries.right_type_r.getKey()+Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey());
			userService.setServiceid(serviceid);
			userService.setUserid(currentUserSvc.getUserId());
			userServiceMapper.insert(userService);
			
			//mount
			List<ServiceMount> mountList = createService.getTemplate().getContainer().getMounts();
			if(mountList!=null){
				for(ServiceMount mount:mountList){
					StorageVolume storageVolume = cscr.getStorageVolume(mount.getCode());
					
					ServiceVolumeRelation serviceVolumeRelation = new ServiceVolumeRelation();
					serviceVolumeRelation.setServiceid(serviceid);
					serviceVolumeRelation.setVolumeid(storageVolume.getVolumeid());
					serviceVolumeRelationMapper.insert(serviceVolumeRelation);
				}
			}
			//network
			List<String> networkList = createService.getTemplate().getNetwork();
			if(networkList!=null){
				for(String networkCode:networkList){
					Network network = cscr.getNetwork(networkCode);
					ServiceNetworkRelation serviceNetworkRelation = new ServiceNetworkRelation();
					serviceNetworkRelation.setServiceid(serviceid);
					serviceNetworkRelation.setNetworkid(network.getNetworkid());
					serviceNetworkRelationMapper.insert(serviceNetworkRelation);
				}
			}
			//secret
			List<ServiceSecret> secretList = createService.getTemplate().getContainer().getSecrets();
			if(secretList!=null){
				for(ServiceSecret serviceSecret:secretList){
					Secret secret = cscr.getSecret(serviceSecret.getCode());
					
					ServiceSecretRelation serviceSecretRelation = new ServiceSecretRelation();
					serviceSecretRelation.setServiceid(serviceid);
					serviceSecretRelation.setSecretid(secret.getSecretid());
					serviceSecretRelationMapper.insert(serviceSecretRelation);
				}
			}
			//config
			List<ServiceConfig> configList = createService.getTemplate().getContainer().getConfigs();
			if(configList!=null){
				for(ServiceConfig serviceConfig:configList){
					Config config = cscr.getConfig(serviceConfig.getCode());
					
					ServiceConfigRelation serviceConfigRelation = new ServiceConfigRelation();
					serviceConfigRelation.setServiceid(serviceid);
					serviceConfigRelation.setConfigid(config.getConfigid());
					serviceConfigRelationMapper.insert(serviceConfigRelation);
				}
			}
			return serviceid;
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void update(Integer clusterid, String serviceid, CreateSerivce createService, CreateServiceCodeResource cscr) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		try{
			DockerSwarmServiceClient.updateService(docker, serviceid, true, createService, cscr);
			//写db
			SwarmService service = new SwarmService();
			service.setServiceid(serviceid);
			service.setClusterid(clusterid);
			service.setName(createService.getName());
			if(createService.getTemplate().getResources()!=null 
					&& createService.getTemplate().getResources().getLimit()!=null 
					&& createService.getTemplate().getResources().getLimit().getCpu()!=null
					&& createService.getTemplate().getResources().getLimit().getCpu()>0){
				service.setCpus(createService.getTemplate().getResources().getLimit().getCpu());
			}
			if(createService.getTemplate().getResources()!=null 
					&& createService.getTemplate().getResources().getLimit()!=null 
					&& createService.getTemplate().getResources().getLimit().getMemory()!=null
					&& createService.getTemplate().getResources().getLimit().getMemory()>0){
				service.setMems(createService.getTemplate().getResources().getLimit().getMemory());
			}
			service.setReplicas(createService.getReplicas());
			service.setConfig(JSON.toJSONString(createService));
//			System.out.println(service.getConfig());
			serviceMapper.updateByPrimaryKey(service);
			
			//delete resource releation
			serviceVolumeRelationMapper.deleteByServiceId(serviceid);
			serviceNetworkRelationMapper.deleteByServiceId(serviceid);
			serviceSecretRelationMapper.deleteByServiceId(serviceid);
			serviceConfigRelationMapper.deleteByServiceId(serviceid);
			
			//mount
			List<ServiceMount> mountList = createService.getTemplate().getContainer().getMounts();
			if(mountList != null){
				for(ServiceMount mount:mountList){
					StorageVolume storageVolume = cscr.getStorageVolume(mount.getCode());
					
					ServiceVolumeRelation serviceVolumeRelation = new ServiceVolumeRelation();
					serviceVolumeRelation.setServiceid(serviceid);
					serviceVolumeRelation.setVolumeid(storageVolume.getVolumeid());
					serviceVolumeRelationMapper.insert(serviceVolumeRelation);
				}
			}
			//network
			List<String> networkList = createService.getTemplate().getNetwork();
			if(networkList != null){
				for(String networkCode:networkList){
					Network network = cscr.getNetwork(networkCode);
					
					ServiceNetworkRelation serviceNetworkRelation = new ServiceNetworkRelation();
					serviceNetworkRelation.setServiceid(serviceid);
					serviceNetworkRelation.setNetworkid(network.getNetworkid());
					serviceNetworkRelationMapper.insert(serviceNetworkRelation);
				}
			}
			//secret
			List<ServiceSecret> secretList = createService.getTemplate().getContainer().getSecrets();
			if(secretList != null){
				for(ServiceSecret serviceSecret:secretList){
					Secret secret = cscr.getSecret(serviceSecret.getCode());
					
					ServiceSecretRelation serviceSecretRelation = new ServiceSecretRelation();
					serviceSecretRelation.setServiceid(serviceid);
					serviceSecretRelation.setSecretid(secret.getSecretid());
					serviceSecretRelationMapper.insert(serviceSecretRelation);
				}
			}
			//config
			List<ServiceConfig> configList = createService.getTemplate().getContainer().getConfigs();
			if(configList != null){
				for(ServiceConfig serviceConfig:configList){
					Config config = cscr.getConfig(serviceConfig.getCode());
					
					ServiceConfigRelation serviceConfigRelation = new ServiceConfigRelation();
					serviceConfigRelation.setServiceid(serviceid);
					serviceConfigRelation.setConfigid(config.getConfigid());
					serviceConfigRelationMapper.insert(serviceConfigRelation);
				}
			}
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void delete(Integer clusterid, String serviceid) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		try{
			try{
				DockerSwarmServiceClient.removeService(docker, serviceid);
			}catch(ServiceNotFoundException ex){
//				ex.printStackTrace();
			}
			//delete resource releation
			serviceVolumeRelationMapper.deleteByServiceId(serviceid);
			serviceNetworkRelationMapper.deleteByServiceId(serviceid);
			serviceSecretRelationMapper.deleteByServiceId(serviceid);
			serviceConfigRelationMapper.deleteByServiceId(serviceid);
			
			//写db
			userServiceMapper.deleteByServiceid(serviceid);
			serviceMapper.deleteByPrimaryKey(serviceid);
			
			
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
	}
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public com.spotify.docker.client.messages.swarm.Service inspect(Integer clusterid, String serviceid) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		try{
			return DockerSwarmServiceClient.inspectService(docker, serviceid);
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public List<Task> psTask(Integer clusterid, TaskCriteria taskCriteria) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		try{
			return DockerSwarmServiceClient.taskList(docker, taskCriteria);
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
	}
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public List<com.spotify.docker.client.messages.swarm.Service> listFromSwarm(Integer clusterid) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		try{
			return DockerSwarmServiceClient.listService(docker);
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public List<SwarmService> list(Integer clusterid) throws Exception {
		return serviceMapper.selectByClusterId(clusterid);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public List<SwarmService> selectByUserClusterKey(Integer clusterid, Integer userid) throws Exception {
		UserClusterKey userClusterKey = new UserClusterKey();
		userClusterKey.setClusterid(clusterid);
		userClusterKey.setUserid(userid);
		return serviceMapper.selectByUserClusterKey(userClusterKey);
	}
	
	public SwarmService selectByClusterIdNameKey(Integer clusterid, String name) throws Exception {
		ClusterIdNameKey clusterIdNameKey = new ClusterIdNameKey();
		clusterIdNameKey.setClusterid(clusterid);
		clusterIdNameKey.setName(name);
		return serviceMapper.selectByClusterIdNameKey(clusterIdNameKey);
	}
	
	public SwarmServiceMemCpuCount countMemCpuByClusterId(Integer clusterid){
		return serviceMapper.countMemCpuByClusterId(clusterid);
	}
}
