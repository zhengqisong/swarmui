package com.apigate.swarmui.controller;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.anntotation.RightMapping;
import com.apigate.swarmui.model.Config;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Network;
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.model.Secret;
import com.apigate.swarmui.model.StoragePv;
import com.apigate.swarmui.model.StorageVolume;
import com.apigate.swarmui.model.SwarmService;
import com.apigate.swarmui.model.UserSerivceRight;
import com.apigate.swarmui.model.UserService;
import com.apigate.swarmui.model.service.CreateSerivce;
import com.apigate.swarmui.model.service.CreateServiceCodeResource;
import com.apigate.swarmui.model.service.ServiceConfig;
import com.apigate.swarmui.model.service.ServiceFile;
import com.apigate.swarmui.model.service.ServiceMount;
import com.apigate.swarmui.model.service.ServiceSecret;
import com.apigate.swarmui.model.service.ServiceUpdateConfig;
import com.apigate.swarmui.model.service.TaskCriteria;
import com.apigate.swarmui.service.ConfigSvc;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.NetworkSvc;
import com.apigate.swarmui.service.RegisterSvc;
import com.apigate.swarmui.service.SecretSvc;
import com.apigate.swarmui.service.ServiceSvc;
import com.apigate.swarmui.service.StoragePvSvc;
import com.apigate.swarmui.service.StorageVolumeSvc;
import com.apigate.swarmui.service.UserClusterSvc;
import com.apigate.swarmui.service.UserConfigSvc;
import com.apigate.swarmui.service.UserNetworkSvc;
import com.apigate.swarmui.service.UserSecretSvc;
import com.apigate.swarmui.service.UserServiceSvc;
import com.apigate.swarmui.service.UserVolumeSvc;
import com.github.pagehelper.util.StringUtil;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.swarm.Service;

/**
 * 
 * service manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/service")
public class ServiceController {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	private UserClusterSvc userClusterSvc;
	
	@Autowired
	UserNetworkSvc userNetworkSvc;
	@Autowired
	UserConfigSvc userConfigSvc;	
	@Autowired
	UserSecretSvc userSecretSvc;
	@Autowired
	UserVolumeSvc userVolumeSvc;
	
	@Autowired
	NetworkSvc networkSvc;
	
	@Autowired
	ConfigSvc configSvc;
	
	@Autowired
	SecretSvc secretSvc;
	
	@Autowired
	ServiceSvc serviceSvc;
	
	@Autowired
	StorageVolumeSvc storageVolumeSvc;
	
	@Autowired
	StoragePvSvc storagePvSvc;
	
	@Autowired
	UserServiceSvc userServiceSvc;
	
	@Autowired
	RegisterSvc registerSvc;
	
	@RequestMapping(value = "/{clusterid}/listself")
	@RightMapping(role={"system","admin","user"},module="service",desc="列出当前用户的服务列表")
	public Result listSelf(@PathVariable Integer clusterid) throws Exception{
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole()
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_user.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			result.setMessage(serviceSvc.selectByUserClusterKey(clusterid ,currentUserSvc.getUserId()));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/list")
	@RightMapping(role={"system","admin","service","user"},module="service",desc="列出所有服务列表")
	public Result list(@PathVariable Integer clusterid) throws Exception{
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				result.setMessage(serviceSvc.selectByUserClusterKey(clusterid ,currentUserSvc.getUserId()));
				return result;
			}
			result.setMessage(serviceSvc.list(clusterid));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/create")
	@RightMapping(role={"system","admin","user"},module="service",desc="添加服务")
	public Result create(@PathVariable Integer clusterid,
			@RequestBody(required = true) CreateSerivce createService) throws Exception{
		Result result = new Result();
		result.setStatus(0);
		
		String serviceName = createService.getName();
		String mode = createService.getMode();
		if(StringUtils.isEmpty(serviceName) || StringUtils.isEmpty(mode)
				|| createService.getTemplate()==null 
				|| createService.getTemplate().getContainer()==null
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		if(!mode.equals("replicas") && mode.equals("global")){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		List<String> networkCodes = createService.getTemplate().getNetwork();
		List<ServiceConfig> configs = createService.getTemplate().getContainer().getConfigs();
		List<ServiceSecret> secrets = createService.getTemplate().getContainer().getSecrets();
		List<ServiceMount> mounts = createService.getTemplate().getContainer().getMounts();
		
		//check cluster right
		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey(),
								Dictionaries.user_role_user.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
		
		CreateServiceCodeResource cscr =  new CreateServiceCodeResource();
		for(String networkCode : networkCodes){
			Network network = networkSvc.selectByCode(networkCode);
			if(network == null){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("service.network.code"));
				return result;
			}
			if(!userNetworkSvc.hasRight(currentUserSvc.getUserId(), network.getNetworkid(), 
					Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			cscr.addNetwork(network);
		}
		if(configs!=null){
			for(ServiceConfig serviceConfig: configs){
				Config config = configSvc.selectByCode(serviceConfig.getCode());
				if(config == null){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("service.config.code"));
					return result;
				}
				if(!userConfigSvc.hasRight(currentUserSvc.getUserId(), config.getConfigid(), 
						Dictionaries.right_type_d.getKey())){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("service.config.file"));
					return result;
				}
				ServiceFile serviceFile = serviceConfig.getFile();
				if(serviceFile != null){
					if(StringUtil.isEmpty(serviceFile.getName())){
						result.setStatus(-1);
						result.setError(MessageManager.getMsg("service.secret.file"));
						return result;
					}
					if(StringUtil.isEmpty(serviceFile.getGid())){
						serviceFile.setGid("0");
					}
					if(StringUtil.isEmpty(serviceFile.getUid())){
						serviceFile.setUid("0");
					}
					if(serviceFile.getMode()==null || serviceFile.getMode()==0){
						serviceFile.setMode(777L);
					}
				}
				cscr.addConfig(config);
			}
			
		}
		if(secrets!=null){
			for(ServiceSecret serviceSecret: secrets){
				Secret secret = secretSvc.selectByCode(serviceSecret.getCode());
				if(secret == null){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("service.secret.code"));
					return result;
				}
				if(!userSecretSvc.hasRight(currentUserSvc.getUserId(), secret.getSecretid(), 
						Dictionaries.right_type_d.getKey())){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_data_right"));
					return result;
				}
				ServiceFile serviceFile = serviceSecret.getFile();
				if(serviceFile != null){
					if(StringUtil.isEmpty(serviceFile.getName())){
						result.setStatus(-1);
						result.setError(MessageManager.getMsg("common.can_not_empty"));
						return result;
					}
					if(StringUtil.isEmpty(serviceFile.getGid())){
						serviceFile.setGid("0");
					}
					if(StringUtil.isEmpty(serviceFile.getUid())){
						serviceFile.setUid("0");
					}
					if(serviceFile.getMode()==null || serviceFile.getMode()==0){
						serviceFile.setMode(777L);
					}
				}
				
				cscr.addSecret(secret);
			}
		}
		if(mounts != null){
			for(ServiceMount serviceMount: mounts){
				StorageVolume storageVolume = storageVolumeSvc.selectByCode(serviceMount.getCode());
				if(storageVolume == null){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("service.mount.code"));
					return result;
				}
				if(!userVolumeSvc.hasRight(currentUserSvc.getUserId(), storageVolume.getVolumeid(), 
						Dictionaries.right_type_d.getKey())){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_data_right"));
					return result;
				}
				if(StringUtil.isEmpty(serviceMount.getTarget())){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("service.mount.target"));
					return result;
				}
				serviceMount.setType("bind");
				StoragePv storagePv = storagePvSvc.selectByPrimaryKey(storageVolume.getPvid());
				
				String volumeName = storageVolume.getCode();
				String pvPath = storagePv.getLocalpath().trim();
				String path = serviceMount.getPath();
				if(path != null && !path.trim().equals("")) {
					path = path.trim();
					if(!path.startsWith("/")){
						path = "/"+path;
					}
					if(path.equals("/")){
						path = "";
					}
				} else {
					path = "";
				}
				
				String source = pvPath + volumeName + path;
				if(!pvPath.endsWith("/")){
					source = pvPath + "/" + volumeName + path;
				}
				storageVolume.setLocalpath(source);
				
				cscr.addStorageVolume(storageVolume);
			}
		}
		ServiceUpdateConfig updateConfig = createService.getUpdateConfig();
		if(updateConfig != null){
			//“pause”|”continue”|”rollback”
			String failureAction = updateConfig.getFailureAction();
			if(failureAction!=null && failureAction.equals("pause") 
					&& failureAction.equals("continue")
					&& failureAction.equals("rollback")){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("service.updateconfig"));
				return result;
			}
			if(updateConfig.getDelay()!=null && updateConfig.getDelay() < 0){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("service.updateconfig"));
				return result;
			}
			if(updateConfig.getParallelism()!=null && updateConfig.getParallelism() < 0){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("service.updateconfig"));
				return result;
			}
		}
		
		try{
			String serviceId = serviceSvc.create(clusterid, createService, cscr);
			result.setMessage(serviceId);
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.add_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/info/{serviceName}")
	@RightMapping(role={"system","admin","user","service"},module="service",desc="服务详情")
	public Result info(@PathVariable Integer clusterid,
			@PathVariable String serviceName) throws Exception{
		Result result = new Result();
		result.setStatus(0);
		
		try{
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey(),
									Dictionaries.user_role_user.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			//check service is exist
			SwarmService service = serviceSvc.selectByClusterIdNameKey(clusterid, serviceName);
			if(service == null){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			
			//check user service right
			if(currentUserSvc.isUserRole() && !userServiceSvc.hasRight(currentUserSvc.getUserId(), service.getServiceid(), 
					Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			result.setMessage(service);
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		
		
		return result;		
	}
	
	@RequestMapping(value = "/{clusterid}/update")
	@RightMapping(role={"system","admin","user"},module="service",desc="升级服务")
	public Result update(@PathVariable Integer clusterid,
			@RequestBody(required = true) CreateSerivce createService) throws Exception{
		Result result = new Result();
		result.setStatus(0);
		
		//check cluster right
		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey(),
								Dictionaries.user_role_user.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}

		String serviceName = createService.getName();
		String mode = createService.getMode();
		if(StringUtils.isEmpty(serviceName) || StringUtils.isEmpty(mode)
				|| createService.getTemplate()==null 
				|| createService.getTemplate().getContainer()==null
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		
		//check service is exist
		SwarmService service = serviceSvc.selectByClusterIdNameKey(clusterid, serviceName);
		if(service == null){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.nofound_error"));
			return result;
		}
		
		//check user service right
		if(currentUserSvc.isUserRole() 
				&& !userServiceSvc.hasRight(currentUserSvc.getUserId(), service.getServiceid(), 
				Dictionaries.right_type_d.getKey())){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
		
		if(!mode.equals("replicas") && mode.equals("global")){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		List<String> networkCodes = createService.getTemplate().getNetwork();
		List<ServiceConfig> configs = createService.getTemplate().getContainer().getConfigs();
		List<ServiceSecret> secrets = createService.getTemplate().getContainer().getSecrets();
		List<ServiceMount> mounts = createService.getTemplate().getContainer().getMounts();
		
		CreateServiceCodeResource cscr =  new CreateServiceCodeResource();
		
		for(String networkCode : networkCodes){
			Network network = networkSvc.selectByCode(networkCode);
			if(network == null){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("service.network.code"));
				return result;
			}
			if(!userNetworkSvc.hasRight(currentUserSvc.getUserId(), network.getNetworkid(), 
					Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			cscr.addNetwork(network);
		}
		if(configs!=null){
			for(ServiceConfig serviceConfig: configs){
				Config config = configSvc.selectByCode(serviceConfig.getCode());
				if(config == null){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("service.config.code"));
					return result;
				}
				if(!userConfigSvc.hasRight(currentUserSvc.getUserId(), config.getConfigid(), 
						Dictionaries.right_type_d.getKey())){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_data_right"));
					return result;
				}
				ServiceFile serviceFile = serviceConfig.getFile();
				if(serviceFile != null){
					if(StringUtil.isEmpty(serviceFile.getName())){
						result.setStatus(-1);
						result.setError(MessageManager.getMsg("service.secret.file"));
						return result;
					}
					if(StringUtil.isEmpty(serviceFile.getGid())){
						serviceFile.setGid("0");
					}
					if(StringUtil.isEmpty(serviceFile.getUid())){
						serviceFile.setUid("0");
					}
					if(serviceFile.getMode()==null || serviceFile.getMode()==0){
						serviceFile.setMode(644L);
					}
				}
				
				cscr.addConfig(config);
			}
		}
		if(secrets!=null){
			for(ServiceSecret serviceSecret: secrets){
				Secret secret = secretSvc.selectByCode(serviceSecret.getCode());
				if(secret == null){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("service.secret.code"));
					return result;
				}
				if(!userSecretSvc.hasRight(currentUserSvc.getUserId(), secret.getSecretid(), 
						Dictionaries.right_type_d.getKey())){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_data_right"));
					return result;
				}
				ServiceFile serviceFile = serviceSecret.getFile();
				if(serviceFile != null){
					if(StringUtil.isEmpty(serviceFile.getName())){
						result.setStatus(-1);
						result.setError(MessageManager.getMsg("auth.not_data_right"));
						return result;
					}
					if(StringUtil.isEmpty(serviceFile.getGid())){
						serviceFile.setGid("0");
					}
					if(StringUtil.isEmpty(serviceFile.getUid())){
						serviceFile.setUid("0");
					}
					if(serviceFile.getMode()==null || serviceFile.getMode()==0){
						serviceFile.setMode(644L);
					}
				}
				
				cscr.addSecret(secret);
			}
		}
		if(mounts != null){
			for(ServiceMount serviceMount: mounts){
				StorageVolume storageVolume = storageVolumeSvc.selectByCode(serviceMount.getCode());
				if(storageVolume == null){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("service.mount.code"));
					return result;
				}
				if(!userVolumeSvc.hasRight(currentUserSvc.getUserId(), storageVolume.getVolumeid(), 
						Dictionaries.right_type_d.getKey())){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_data_right"));
					return result;
				}
				
				if(StringUtil.isEmpty(serviceMount.getTarget())){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("service.mount.target"));
					return result;
				}
				serviceMount.setType("bind");
				StoragePv storagePv = storagePvSvc.selectByPrimaryKey(storageVolume.getPvid());
				
				String volumeName = storageVolume.getCode();
				String pvPath = storagePv.getLocalpath().trim();
				String path = serviceMount.getPath();
				if(path != null && !path.trim().equals("")) {
					path = path.trim();
					if(!path.startsWith("/")){
						path = "/"+path;
					}
					if(path.equals("/")){
						path = "";
					}
				} else {
					path = "";
				}
				
				String source = pvPath + volumeName;
				if(!pvPath.endsWith("/")){
					source = pvPath + "/" + volumeName;
				}
				storageVolume.setLocalpath(source);
				
				cscr.addStorageVolume(storageVolume);
			}
		}
		ServiceUpdateConfig updateConfig = createService.getUpdateConfig();
		if(updateConfig != null){
			//“pause”|”continue”|”rollback”
			String failureAction = updateConfig.getFailureAction();
			if(failureAction!=null && failureAction.equals("pause")
					&& failureAction.equals("continue")
					&& failureAction.equals("rollback")){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("service.updateconfig"));
				return result;
			}
			if(updateConfig.getDelay()!=null && updateConfig.getDelay() < 0){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("service.updateconfig"));
				return result;
			}
			if(updateConfig.getParallelism()!=null && updateConfig.getParallelism() < 0){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("service.updateconfig"));
				return result;
			}
		}
		
		try{
			serviceSvc.update(clusterid, service.getServiceid(), createService, cscr);
			result.setMessage(MessageManager.getMsg("common.modify_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.add_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/delete/{serviceName}")
	@RightMapping(role={"system","admin","user"},module="service",desc="删除服务")
	public Result delete(@PathVariable Integer clusterid,
			@PathVariable String serviceName) throws Exception{
		Result result = new Result();
		result.setStatus(0);
		
		//check cluster right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey(),
								Dictionaries.user_role_user.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
		
		//check service is exist
		SwarmService service = serviceSvc.selectByClusterIdNameKey(clusterid, serviceName);
		if(service == null){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.nofound_error"));
			return result;
		}
		
		//check user service right
		if(currentUserSvc.isUserRole() && !userServiceSvc.hasRight(currentUserSvc.getUserId(), service.getServiceid(), 
				Dictionaries.right_type_d.getKey())){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
		
		try{
			serviceSvc.delete(clusterid, service.getServiceid());
			result.setMessage(MessageManager.getMsg("common.delete_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+": "+ex.getMessage());
			return result;
		}
		return result;
		
	}
	
	@RequestMapping(value = "/{clusterid}/inspect/{serviceName}")
	@RightMapping(role={"system","admin","user","service"},module="service",desc="添加服务")
	public Result inspect(@PathVariable Integer clusterid, @PathVariable String serviceName) throws Exception{
		Result result = new Result();
		result.setStatus(0);
		
		try{
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey(),
									Dictionaries.user_role_user.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			//check service is exist
			SwarmService service = serviceSvc.selectByClusterIdNameKey(clusterid, serviceName);
			if(service == null){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			
			//check user service right
			if(currentUserSvc.isUserRole() && !userServiceSvc.hasRight(currentUserSvc.getUserId(), service.getServiceid(), 
					Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			result.setMessage(serviceSvc.inspect(clusterid, serviceName));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
		
	}
	
	@RequestMapping(value = "/{clusterid}/pstask/{serviceName}")
	@RightMapping(role={"system","admin","user","service"},module="service",desc="添加服务")
	public Result psTask(@PathVariable Integer clusterid, 
			@PathVariable String serviceName, 
			@RequestBody(required = false) TaskCriteria taskCriteria) 
			throws Exception{
		Result result = new Result();
		result.setStatus(0);
		
		try{
			
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey(),
									Dictionaries.user_role_user.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			//check service is exist
			SwarmService service = serviceSvc.selectByClusterIdNameKey(clusterid, serviceName);
			if(service == null){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			
			//check user service right
			if(currentUserSvc.isUserRole() && !userServiceSvc.hasRight(currentUserSvc.getUserId(), service.getServiceid(), 
					Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			if(taskCriteria==null){
				taskCriteria = new TaskCriteria();
			}
			taskCriteria.setServiceName(serviceName);
			result.setMessage(serviceSvc.psTask(clusterid, taskCriteria));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
		
	}
	
	@RequestMapping(value = "/{clusterid}/task/list")
	@RightMapping(role={"system","admin","service"},module="service",desc="添加服务")
	public Result listTask(@PathVariable Integer clusterid, @RequestBody(required = false) TaskCriteria taskCriteria) throws Exception{
		Result result = new Result();
		result.setStatus(0);
		
		try{
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			result.setMessage(serviceSvc.psTask(clusterid, taskCriteria));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
		
	}
	
	@RequestMapping(value = "/{clusterid}/setright/{serviceid}")
	@RightMapping(role={"system","admin","user"},module="service",desc="服务授权")
	public Result setRight(@PathVariable int clusterid, @PathVariable String serviceid, @RequestBody UserService userService){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			String rights = userService.getRights();
			Integer userid = userService.getUserid();
			
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_user.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}

			//check user service right
			if(currentUserSvc.isUserRole() && !userServiceSvc.hasRight(currentUserSvc.getUserId(), 
					serviceid, Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			if(userid == null || userid < 0){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.can_not_empty"));
				return result;
			}			
			
			if(userid.equals(currentUserSvc.getUserId())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_set_right_self"));
				return result;
			}
			//判断right是否正确
			userServiceSvc.setRight(userid, serviceid, rights);			
			result.setError(MessageManager.getMsg("common.modify_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.modify_error")+": "+ex.getMessage());
			return result;
		}
		return result;			
	}
	
	@RequestMapping(value = "/{clusterid}/getright/{serviceid}/{userid}")
	@RightMapping(role={"system","admin","user","service"},module="service",desc="获取服务授权")
	public Result getRight(@PathVariable int clusterid, @PathVariable String serviceid, @PathVariable Integer userid){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey(),
									Dictionaries.user_role_user.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}

			//check user service right
			if(currentUserSvc.isUserRole() && !userServiceSvc.hasRight(currentUserSvc.getUserId(), 
					serviceid, Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			if(userid == null || userid < 0){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.can_not_empty"));
				return result;
			}
			result.setMessage(userServiceSvc.getRight(userid, serviceid));	
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
		
	}
	
	@RequestMapping(value = "/{clusterid}/rights/{serviceid}/user/list")
	@RightMapping(role={"system","admin","service","user"},module="service",desc="查看集群")
	public Result rights_user_list(@PathVariable int clusterid,@PathVariable String serviceid){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				if(!userServiceSvc.hasRight(currentUserSvc.getUserId(), serviceid, "wd")){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_data_right"));
					return result;
				}
			}
			
			List<UserSerivceRight> userServiceList = userServiceSvc.selectByServiceid(serviceid);		
			result.setMessage(userServiceList);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+ex.getMessage());
		}
		
		return result;
	}
	
	public static void main(String[] argvs){
		
		DefaultDockerClient.Builder builder = DefaultDockerClient.builder();
		try {
			builder.uri(URI.create("https://172.16.101.145:12376"))
					.dockerCertificates(new DockerCertificates(
							Paths.get("/data/clientkey/")));
			DefaultDockerClient docker = builder.build();
			
			final List<Service> services = docker.listServices();
			final List<Container> containers = docker.listContainers();
			
			System.out.println(docker.version().apiVersion());
			System.out.println(containers.toString());
			System.out.println(services.toString());
			
			docker.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
}
