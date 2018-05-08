package com.apigate.swarmui.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.anntotation.RightMapping;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.NodeSvc;
import com.apigate.swarmui.service.UserClusterSvc;

/**
 * 
 * cluster manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/node")
public class NodeController {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	private UserClusterSvc userClusterSvc;
	
	@Autowired
	NodeSvc nodeSvc;
		
	@RequestMapping(value = "/{clusterid}/list")
	@RightMapping(role={"system","admin","service"},module="node",desc="列表集群节点")
	public Result list(@PathVariable int clusterid){
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
			
			result.setMessage(nodeSvc.list(clusterid));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/inspect/{nodeid}")
	@RightMapping(role={"system","admin","service"},module="node",desc="查看集群节点")
	public Result inspect(@PathVariable int clusterid, @PathVariable String nodeid){
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
			result.setMessage(nodeSvc.inspect(clusterid, nodeid));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/addlabel/{nodeid}")
	@RightMapping(role={"system","admin"},module="node",desc="添加集群节点标签")
	public Result addLabel(@PathVariable int clusterid, @PathVariable String nodeid,
			@RequestBody Map<String,String> label){
		Result result = new Result();
		result.setStatus(0);
		
		try{
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			nodeSvc.addLabel(clusterid, nodeid, label);
			result.setMessage(MessageManager.getMsg("common.add_success"));
		}catch(Exception ex){
			//ex.printStackTrace();
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.add_error")+": "+ex.getMessage());
			return result;
		}		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/rmlabel/{nodeid}")
	@RightMapping(role={"system","admin"},module="node",desc="删除集群节点标签")
	public Result rmLabel(@PathVariable int clusterid, @PathVariable String nodeid,
			@RequestBody List<String> label){
		Result result = new Result();
		result.setStatus(0);
		
		try{
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			nodeSvc.rmLabel(clusterid, nodeid, label);
			result.setMessage(MessageManager.getMsg("common.add_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
}
