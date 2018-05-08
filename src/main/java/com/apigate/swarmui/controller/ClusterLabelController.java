package com.apigate.swarmui.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.anntotation.RightMapping;
import com.apigate.swarmui.model.ClusterLabel;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.service.ClusterLabelSvc;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.UserClusterSvc;

/**
 * 
 * cluster manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/cluster/label")
public class ClusterLabelController {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	private UserClusterSvc userClusterSvc;
	
	@Autowired
	ClusterLabelSvc clusterLabelSvc;
	
	@RequestMapping(value = "/{clusterid}/list")
	@RightMapping(role={"system","admin","service","user"},module="label",desc="查看集群标签")
	public Result list(@PathVariable Integer clusterid){
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
			
			result.setMessage(clusterLabelSvc.selectAll(clusterid));			
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/create")
	@RightMapping(role={"system","admin"},module="label",desc="创建集群标签")
	public Result create(@PathVariable int clusterid, @RequestBody ClusterLabel clusterLabel){
		Result result = new Result();
		result.setStatus(0);
		
		if(clusterid < 0){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		clusterLabel.setClusterid(clusterid);
		
		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
		
		if(StringUtils.isEmpty(clusterLabel.getLabelname()) 
				|| StringUtils.isEmpty(clusterLabel.getLabelkey()) 
				|| StringUtils.isEmpty(clusterLabel.getLabelvalue())
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		
		try {
			clusterLabelSvc.insert(clusterLabel);
			result.setMessage(clusterLabel);
		}
		catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.add_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/delete/{labelid}")
	@RightMapping(role={"system","admin"},module="label",desc="删除集群标签")
	public Result delete(@PathVariable int clusterid, @PathVariable Integer labelid){
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
			
			ClusterLabel clusterLabel = clusterLabelSvc.selectByPrimaryKey(labelid);
			if(clusterLabel==null || clusterLabel.getClusterid()!=clusterid){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			clusterLabelSvc.deleteByPrimaryKey(labelid);
			result.setMessage(MessageManager.getMsg("common.delete_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+": "+ex.getMessage());
			return result;
		}
		return result;	
	}	
}
