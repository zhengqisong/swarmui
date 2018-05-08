package com.apigate.swarmui.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.anntotation.RightMapping;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.model.StorageVolumeCount;
import com.apigate.swarmui.model.SwarmService;
import com.apigate.swarmui.model.SwarmServiceMemCpuCount;
import com.apigate.swarmui.model.service.TaskCriteria;
import com.apigate.swarmui.service.ConfigSvc;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.NetworkSvc;
import com.apigate.swarmui.service.NodeSvc;
import com.apigate.swarmui.service.SecretSvc;
import com.apigate.swarmui.service.ServiceSvc;
import com.apigate.swarmui.service.StorageVolumeSvc;
import com.apigate.swarmui.service.UserClusterSvc;
import com.apigate.swarmui.service.UserConfigSvc;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.swarm.Node;
import com.spotify.docker.client.messages.swarm.Task;

/**
 * 
 * cluster manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	UserClusterSvc userClusterSvc;
	
	@Autowired
	ConfigSvc configSvc;
	@Autowired
	SecretSvc secretSvc;
	@Autowired
	NetworkSvc networkSvc;
	@Autowired
	StorageVolumeSvc storageVolumeSvc;
	@Autowired
	ServiceSvc serviceSvc;
	
	@Autowired
	NodeSvc nodeSvc;
	
	@Autowired
	UserConfigSvc userConfigSvc;
	
	@RequestMapping(value = "/{clusterid}/resource/count")
	@RightMapping(role={"system","admin","service","user"},module="dashoard",desc="仪表盘资源统计")
	public Result resourceCount(@PathVariable Integer clusterid){
		Result result = new Result();
		result.setStatus(0);
		
		try{
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				int countConfig = configSvc.countByUserClusterKey(clusterid, currentUserSvc.getUserId());
				int countSecret = secretSvc.countByUserClusterKey(clusterid, currentUserSvc.getUserId());
				int countNetwork = networkSvc.countByUserClusterKey(clusterid, currentUserSvc.getUserId());
				StorageVolumeCount countVolume = storageVolumeSvc.countByUserClusterKey(clusterid, currentUserSvc.getUserId());
				
				HashMap<String,Object> countMap = new HashMap<String,Object>();
				countMap.put("config", countConfig);
				countMap.put("secret", countSecret);
				countMap.put("network", countNetwork);
				countMap.put("volume", countVolume);
				
				result.setMessage(countMap);
				return result;
			}
			
			int countConfig = configSvc.countByClusterId(clusterid);
			int countSecret = secretSvc.countByClusterId(clusterid);
			int countNetwork = networkSvc.countByClusterId(clusterid);
			StorageVolumeCount countVolume = storageVolumeSvc.countByClusterId(clusterid);
			
			HashMap<String,Object> countMap = new HashMap<String,Object>();
			countMap.put("config", countConfig);
			countMap.put("secret", countSecret);
			countMap.put("network", countNetwork);
			countMap.put("volume", countVolume);
			result.setMessage(countMap);
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/node/count")
	@RightMapping(role={"system","admin","service"},module="dashoard",desc="仪表盘Swarm统计")
	public Result nodeCount(@PathVariable Integer clusterid){
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
			
			List<Node> nodeList = nodeSvc.list(clusterid);
			boolean hasLeader = true;
			int nodeNum = 0;
			int managerNum = 0;
			long cpuNum = 0;
			long memNum = 0;
			int statusOkNum = 0;
			int statusErrorNum = 0;
			
			for(Node node:nodeList){
				boolean isLeader = false;
				boolean isManager = node.managerStatus() != null;
				boolean statusOk = "ready".equalsIgnoreCase(node.status().state());
				long cpu = node.description().resources().nanoCpus();
				long mem = node.description().resources().memoryBytes();
				if(isManager){
					statusOk = "Reachable".equalsIgnoreCase(node.managerStatus().reachability());
					isLeader = node.managerStatus().leader() != null;
				}
				if(isLeader){
					hasLeader = true;
				}
				//count
				nodeNum++;
				if(isManager){
					managerNum++;
				}
				cpuNum += cpu;
				memNum += mem;
				if(statusOk){
					statusOkNum++;
				}else{
					statusErrorNum++;
				}
			}
			if(!hasLeader){
				statusErrorNum = nodeNum;
				statusOkNum = 0;
			}
			HashMap<String, Object> countMap = new HashMap<String, Object>();
			countMap.put("nodeNum", nodeNum);
			countMap.put("managerNum", managerNum);
			countMap.put("cpuNum", cpuNum/1000000000);
			countMap.put("memNum", memNum/1024/1024/1024);
			countMap.put("statusOkNum", statusOkNum);
			countMap.put("statusErrorNum", statusErrorNum);
			
			SwarmServiceMemCpuCount serviceMemCpuCount =serviceSvc.countMemCpuByClusterId(clusterid);
			countMap.put("useCpuNum", serviceMemCpuCount==null?0:serviceMemCpuCount.getCpus());
			countMap.put("useMemNum", serviceMemCpuCount==null?0:serviceMemCpuCount.getMems());
			
			result.setMessage(countMap);
		}
		catch(DockerException cex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("docker.exception"));
		}
		catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/service/count")
	@RightMapping(role={"system","admin","service","user"},module="dashoard",desc="仪表盘Swarm统计")
	public Result serviceCount(@PathVariable Integer clusterid){
		Result result = new Result();
		result.setStatus(0);
		
		try{
			List<SwarmService> serviceList = null;
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				serviceList = serviceSvc.selectByUserClusterKey(clusterid ,currentUserSvc.getUserId());
			}
			Map<String,Integer> userServiceMap = new HashMap<String, Integer>();
			Map<String,Integer> taskServiceMap = new HashMap<String, Integer>();
			
			serviceList = serviceSvc.list(clusterid);
			if(serviceList != null){
				for(SwarmService service:serviceList){
					String serviceid = service.getServiceid();
					int replicas = service.getReplicas()==null?0:service.getReplicas();
					userServiceMap.put(serviceid, replicas);
				}
				
			}
			if(serviceList != null){
				TaskCriteria taskCriteria = new TaskCriteria();
				taskCriteria.setDesiredState("running");
				List<Task> taskList = serviceSvc.psTask(clusterid, taskCriteria);
				if(taskList != null){
					for(Task task : taskList){
						String serviceId = task.serviceId();
						Integer replicas = taskServiceMap.get(serviceId);
						if(replicas==null){
							replicas = 0;
						}
						replicas +=1;
						taskServiceMap.put(serviceId, replicas);
					}				
				}
			}
			//count
			int serviceNum = 0;
			int taskNum = 0;
			int statusOkNum = 0;
			int statusWarnNum = 0;
			int statusErrorNum = 0;
			
			Iterator<String> iterator = userServiceMap.keySet().iterator();
			while(iterator.hasNext()){
				String serviceId = iterator.next();
				Integer userServiceTaskNum = userServiceMap.get(serviceId);
				Integer taskServiceTaskNum = taskServiceMap.get(serviceId);
				
				if(taskServiceTaskNum == null || taskServiceTaskNum == 0){
					statusErrorNum++;
				}else if(userServiceTaskNum > taskServiceTaskNum){
					statusWarnNum++;
				}else{
					statusOkNum++;
				}
				taskNum += userServiceTaskNum;
				serviceNum++;
			}
			
			HashMap<String, Object> countMap = new HashMap<String, Object>();
			countMap.put("serviceNum", serviceNum);
			countMap.put("taskNum", taskNum);
			countMap.put("statusOkNum", statusOkNum);
			countMap.put("statusWarnNum", statusWarnNum);
			countMap.put("statusErrorNum", statusErrorNum);
			
			result.setMessage(countMap);
		}
		catch(DockerException cex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("docker.exception"));
		}
		catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
}
