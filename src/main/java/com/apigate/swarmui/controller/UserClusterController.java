package com.apigate.swarmui.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

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
import com.apigate.swarmui.model.UserCluster;
import com.apigate.swarmui.model.UserClusterKey;
import com.apigate.swarmui.service.ClusterSvc;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.UserClusterSvc;

/**
 * 
 * cluster manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/user/cluster")
public class UserClusterController {
	@Autowired
	CurrentUserSvc currentUserSvc;
	@Autowired
	private ClusterSvc clusterSvc;
	@Autowired
	private UserClusterSvc userClusterSvc;
	
	@RequestMapping(value = "/{userid}/list")
	@RightMapping(role={"system","admin"},module="user",desc="列出用户的集群")
	public Result list(@PathVariable int userid) {
		Result result = new Result();
		result.setStatus(0);
		try {
			List<UserCluster> list = new Vector<UserCluster>();
			List<UserCluster> ulist = userClusterSvc.selectByAll(userid);
			//Just list the clusters of users and operators
			if(!currentUserSvc.isSystemRole()){
				List<UserCluster> clist = userClusterSvc.selectByAll(currentUserSvc.getUserId());
				for(UserCluster key : ulist){
					for(UserCluster ckey :clist){
						if(key.getClusterid().equals(ckey.getClusterid())){
							list.add(key);
							break;
						}
					}
				}
			}else{
				list = ulist;
			}
			result.setMessage(list);
		} catch (Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error") + ": " + ex.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value = "/listself")
	@RightMapping(module="user",desc="列出用户的集群")
	public Result listSelf() {
		Result result = new Result();
		result.setStatus(0);
		try {
			Integer userid = currentUserSvc.getUserId();
			if(!currentUserSvc.isSystemRole()){
				List<UserCluster> ulist = userClusterSvc.selectByAll(userid);
				result.setMessage(ulist);
			}else{
				List<UserCluster> ulist = new Vector<UserCluster>();
				List<Cluster> alist = clusterSvc.selectAll();
				for(Cluster cluster:alist){
					UserCluster key = new UserCluster();
					key.setClusterid(cluster.getClusterid());
					key.setUserid(userid);
					key.setName(cluster.getName());
					ulist.add(key);
				}
				result.setMessage(ulist);
			}
			return result;
		} catch (Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.sql_error")+": "+ex.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value = "/{userid}/addright")
	@RightMapping(role={"system","admin"},module="user",desc="增加用户集群权限")
	public Result addright(@PathVariable int userid, @RequestBody UserCluster userCluster) {
		Result result = new Result();
		result.setStatus(0);
		try {
			if(userid==currentUserSvc.getUserId()){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_set_right_self"));
				return result;
			}
			if(!currentUserSvc.isSystemRole() && !userClusterSvc.hasClusterRight(userid, 
					userCluster.getClusterid(),
					Arrays.asList(Dictionaries.user_role_admin.getKey()))){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_same_data_right"));
					return result;
			}
			UserClusterKey userClusterKey =new UserClusterKey();
			userClusterKey.setClusterid(userCluster.getClusterid());
			userClusterKey.setUserid(userid);
			if(userClusterSvc.selectByPrimaryKey(userClusterKey)!=null){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.recode_exist"));
				return result;
			}
			
			userClusterSvc.setClusterId(userid, userCluster.getClusterid(),userCluster.getRights());
			result.setMessage(MessageManager.getMsg("common.add_success"));
		} catch (Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.sql_error")+": "+ex.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value = "/{userid}/rmright/{clusterid}")
	@RightMapping(role={"system","admin"},module="user",desc="删除用户集群权限")
	public Result rmright(@PathVariable int userid, @PathVariable int clusterid) {
		Result result = new Result();
		result.setStatus(0);
		try {
			if(userid == currentUserSvc.getUserId()){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_set_right_self"));
				return result;
			}
			if(!currentUserSvc.isSystemRole() && !userClusterSvc.hasClusterRight(userid, 
					clusterid, Arrays.asList(Dictionaries.user_role_admin.getKey()))){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_same_data_right"));
					return result;
			}
			UserClusterKey userClusterKey = new UserClusterKey();
			userClusterKey.setClusterid(clusterid);
			userClusterKey.setUserid(userid);
			userClusterSvc.deleteByPrimaryKey(userClusterKey);
			result.setMessage(MessageManager.getMsg("common.add_success"));
		} catch (Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.sql_error")+": "+ex.getMessage());
		}
		return result;
	}
}
