package com.apigate.swarmui.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.anntotation.RightMapping;
import com.apigate.swarmui.model.Cluster;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.model.UserClusterRight;
import com.apigate.swarmui.service.ClusterSvc;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.NodeSvc;
import com.apigate.swarmui.service.UserClusterSvc;
import com.spotify.docker.client.messages.swarm.Node;

/**
 * 
 * cluster manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/cluster")
public class ClusterController {
	
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	private ClusterSvc clusterSvc;
	
	@Autowired
	private UserClusterSvc userClusterSvc;
	
	@Autowired
	private NodeSvc nodeSvc;
	
	//@Autowired  
	//private MessageSource messageSource;
	
	@RequestMapping(value = "/create")
	@RightMapping(role={"system","admin"},module="user",desc="添加集群")
	public Result create(@RequestBody Cluster cluster){
		Result result = new Result();
		result.setStatus(0);
		
		if(cluster.getBaseUrl()==null || cluster.getBaseUrl().length()<5){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.param_invalid"));
		}
//		if(cluster.getVersion()==null || cluster.getVersion().length()<2){
//			result.setStatus(-1);
//			result.setError(MessageManager.getMsg("cluster.param_invalid"));
//		}
		if(cluster.getName() == null || cluster.getMaxcpus() == null || cluster.getMaxcpus() < 1 
			|| cluster.getMaxmem() == null || cluster.getMaxmem() < 1 
			|| cluster.getMaxinstance() == null || cluster.getMaxinstance() < 1){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.param_invalid"));
		}
		
		if(result.getStatus() == 0 && 
				(StringUtils.isEmpty(cluster.getCapem()) || 
						StringUtils.isEmpty(cluster.getCertpem()) || 
						StringUtils.isEmpty(cluster.getKeypem()))
				&&
				!(StringUtils.isEmpty(cluster.getCapem()) &&
						StringUtils.isEmpty(cluster.getCertpem()) &&
						StringUtils.isEmpty(cluster.getKeypem()))
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.certificate_invalid"));
		}
		
		if(result.getStatus() == 0 && clusterSvc.selectByName(cluster.getName()) != null ){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.name_repeat"));
		}
		
		
		if(result.getStatus() == 0){
			try{
				clusterSvc.insert(cluster);
				result.setMessage(MessageManager.getMsg("common.add_success"));
			}catch(Exception ex){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.sql_error")+": "+ex.getMessage());
			}
		}
		
		return result;
	}
	
	@RequestMapping(value = "/list")
	@RightMapping(role={"system","admin","service"},module="user",desc="集群列表")
	public Result list(){
		Result result = new Result();
		result.setStatus(0);
		try{
			List<Cluster> list;
			if(currentUserSvc.isSystemRole()) {
				list = clusterSvc.selectAll();				
			} else {
				list = clusterSvc.selectByUserClusterKey(currentUserSvc.getUserId());
			}
			result.setMessage(list);
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/delete")
	@RightMapping(role={"system","admin"},module="user",desc="删除集群")
	public Result delete(@PathVariable int clusterid){
		Result result = new Result();
		result.setStatus(0);
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			clusterSvc.deleteByPrimaryKey(clusterid);
			result.setMessage(MessageManager.getMsg("common.delete_success"));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+": "+ex.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/modify")
	@RightMapping(role={"system","admin"},module="user",desc="修改集群")
	public Result modify(@PathVariable int clusterid, @RequestBody Cluster cluster){
		Result result = new Result();
		result.setStatus(0);
		
		if(clusterid < 0){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.nofound_error"));
		}
		
		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
		
		if(result.getStatus() == 0 && (cluster.getBaseUrl()==null || cluster.getBaseUrl().length()<5)){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.param_invalid"));
		}
//		if(result.getStatus() == 0 && (cluster.getVersion()==null || cluster.getVersion().length()<2)){
//			result.setStatus(-1);
//			result.setError(MessageManager.getMsg("cluster.param_invalid"));
//		}
		if(result.getStatus() == 0 && (cluster.getName() == null || cluster.getMaxcpus() == null || cluster.getMaxcpus() < 1 
			|| cluster.getMaxmem() == null || cluster.getMaxmem() < 1 
			|| cluster.getMaxinstance() == null || cluster.getMaxinstance() < 1)){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.param_invalid"));
		}
		
		if(result.getStatus() == 0 && 
				(StringUtils.isEmpty(cluster.getCapem()) || 
						StringUtils.isEmpty(cluster.getCertpem()) || 
						StringUtils.isEmpty(cluster.getKeypem()))
				&&
				!(StringUtils.isEmpty(cluster.getCapem()) &&
						StringUtils.isEmpty(cluster.getCertpem()) &&
						StringUtils.isEmpty(cluster.getKeypem()))
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.certificate_invalid"));
		}
		
		if(result.getStatus() == 0) {
			Cluster findCluster = clusterSvc.selectByName(cluster.getName());
			if(findCluster != null && !findCluster.getClusterid().equals(clusterid)) {
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("cluster.name_repeat"));
			} else if(clusterSvc.selectByPrimaryKey(clusterid)==null) {
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
			}
		}
		
		if(result.getStatus() == 0) {
			try {
				cluster.setClusterid(clusterid);
				clusterSvc.updateByPrimaryKey(cluster);
				result.setMessage(MessageManager.getMsg("common.modify_success"));
			} catch(Exception ex) {
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.modify_error")+": "+ex.getMessage());
			}
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/info")
	@RightMapping(role={"system","admin","service"},module="user",desc="查看集群")
	public Result info(@PathVariable int clusterid){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			Cluster cluster = clusterSvc.selectByPrimaryKey(clusterid);
			
			if(cluster == null){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			
			result.setMessage(cluster);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/rights/user/list")
	@RightMapping(role={"system","admin","service"},module="user",desc="获取集群授权用户列表")
	public Result rights_user_list(@PathVariable int clusterid){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			List<UserClusterRight> userClusterList = userClusterSvc.selectByGlusterid(clusterid);
			
			result.setMessage(userClusterList);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/nodes/labels")
	@RightMapping(role={"system","admin"},module="user",desc="查看集群标签")
	public Result nodesLabels(@PathVariable int clusterid){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			//key value id1,id2,id3
			List<Map<String,Object>> label_list = new Vector<Map<String,Object>>();
			List<Node> nodes = nodeSvc.list(clusterid);
			for(Node node:nodes){
				Map<String,String> labels = node.spec().labels();
				if(labels != null){
					Iterator<String> iterator = labels.keySet().iterator();
					while(iterator.hasNext()){
						String key = iterator.next();
						String value = labels.get(key);
						String hostname = node.description().hostname();
						String id = node.id();
						
						boolean flag = false;
						for(Map<String,Object> label_map:label_list){
							
							if(label_map.get("key").equals(key) && label_map.get("value").equals(value)){
								((List<String>)label_map.get("hostname")).add(hostname);
								flag = true;
								continue;
							}
						}
						if(flag==false){
							List<String> hostList = new Vector<String>();
							hostList.add(hostname);
							Map<String,Object> label_map = new HashMap<String,Object>();
							label_map.put("key", key);
							label_map.put("value", value);
							label_map.put("hostname", hostList);							
							label_list.add(label_map);
						}
					}
				}
			}
			result.setMessage(label_list);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+ex.getMessage());
		}
		
		return result;
	}
	
}
