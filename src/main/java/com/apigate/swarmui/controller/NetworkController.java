package com.apigate.swarmui.controller;

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
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Network;
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.model.UserConfigRight;
import com.apigate.swarmui.model.UserNetwork;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.NetworkSvc;
import com.apigate.swarmui.service.UserClusterSvc;
import com.apigate.swarmui.service.UserNetworkSvc;

/**
 * 
 * cluster manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/network")
public class NetworkController {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	private UserClusterSvc userClusterSvc;
	
	@Autowired
	NetworkSvc networkSvc;
	
	@Autowired
	UserNetworkSvc userNetworkSvc;
	
	@RequestMapping(value = "/{clusterid}/listself")
	@RightMapping(role={"system","admin","user"},module="network",desc="列表集群网络")
	public Result listSelf(@PathVariable Integer clusterid){
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
			
			result.setMessage(networkSvc.selectByUserClusterKey(clusterid, currentUserSvc.getUserId()));			
		} catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/list")
	@RightMapping(role={"system","admin","service","user"},module="network",desc="列表集群网络")
	public Result list(@PathVariable Integer clusterid){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				result.setMessage(networkSvc.selectByUserClusterKey(clusterid, currentUserSvc.getUserId()));			
				return result;
			}
			result.setMessage(networkSvc.list(clusterid));			
		} catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/count")
	@RightMapping(role={"system","admin","service","user"},module="network",desc="列表集群网络")
	public Result count(@PathVariable Integer clusterid){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				result.setMessage(networkSvc.selectByUserClusterKey(clusterid, currentUserSvc.getUserId()));			
				return result;
			}
			result.setMessage(networkSvc.list(clusterid));			
		} catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/create")
	@RightMapping(role={"system","admin","user"},module="network",desc="创建集群网络")
	public Result create(@PathVariable int clusterid, @RequestBody Network network){
		Result result = new Result();
		result.setStatus(0);
		
		if(clusterid < 0){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.param_invalid"));
			return result;
		}
		network.setClusterid(clusterid);
		
		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey(),
								Dictionaries.user_role_user.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
		
		if(!"yes".equalsIgnoreCase(network.getIpv6())){
			network.setIpv6("no");
		}
		if(!"yes".equalsIgnoreCase(network.getInternal())){
			network.setInternal("no");
		}
		if(StringUtils.isEmpty(network.getDriver()) 
				|| StringUtils.isEmpty(network.getName()) 
				|| StringUtils.isEmpty(network.getSubnet())
				|| StringUtils.isEmpty(network.getIprange())
				|| StringUtils.isEmpty(network.getGateway())
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.param_invalid"));
			return result;
		}
		
		try{
			String networkid = networkSvc.create(network);
			result.setMessage(networkid);
		}
		catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.add_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/delete/{networkid}")
	@RightMapping(role={"system","admin","user"},module="network",desc="创建集群网络")
	public Result delete(@PathVariable int clusterid, @PathVariable String networkid){
		Result result = new Result();
		result.setStatus(0);
		
		try{
			Network network = networkSvc.selectByPrimaryKey(networkid);
			if(network==null || network.getClusterid() != clusterid){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_user.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			//check user network right
			if(currentUserSvc.isUserRole() && !userNetworkSvc.hasRight(currentUserSvc.getUserId(), networkid, 
					Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			networkSvc.delete(clusterid, networkid);
			result.setMessage(MessageManager.getMsg("common.delete_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+": "+ex.getMessage());
			return result;
		}
		return result;	
	}
	
	@RequestMapping(value = "/{clusterid}/inspect/{networkid}")
	@RightMapping(role={"system","admin","user","service"},module="network",desc="创建集群网络")
	public Result inspect(@PathVariable int clusterid, @PathVariable String networkid){
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
			
			//check user network right
			if(currentUserSvc.isUserRole() && !userNetworkSvc.hasRight(currentUserSvc.getUserId(), networkid, 
					Dictionaries.right_type_r.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			Network network = networkSvc.selectByPrimaryKey(networkid);
			if(network==null || network.getClusterid() != clusterid){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			result.setMessage(networkSvc.inspect(clusterid, networkid));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;	
	}
	
	@RequestMapping(value = "/{clusterid}/setright/{networkid}")
	@RightMapping(role={"system","admin","user"},module="network",desc="创建集群网络")
	public Result setRight(@PathVariable int clusterid, @PathVariable String networkid, @RequestBody UserNetwork userNetwork){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			String rights = userNetwork.getRights();
			Integer userid = userNetwork.getUserid();
			
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_user.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}

			//check user network right
			if(currentUserSvc.isUserRole() && !userNetworkSvc.hasRight(currentUserSvc.getUserId(), 
					networkid, Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey())){
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
			userNetworkSvc.setRight(userid, networkid, rights);			
			result.setError(MessageManager.getMsg("common.modify_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.modify_error")+": "+ex.getMessage());
			return result;
		}
		return result;			
	}
	
	@RequestMapping(value = "/{clusterid}/getright/{networkid}/{userid}")
	@RightMapping(role={"system","admin","user","service"},module="network",desc="创建集群网络")
	public Result getRight(@PathVariable int clusterid, @PathVariable String networkid, @PathVariable Integer userid){
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

			//check user network right
			if(currentUserSvc.isUserRole() && !userNetworkSvc.hasRight(currentUserSvc.getUserId(), 
					networkid, Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			if(userid == null || userid < 0){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.can_not_empty"));
				return result;
			}
			result.setMessage(userNetworkSvc.getRight(userid, networkid));	
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
		
	}
	
	@RequestMapping(value = "/{clusterid}/rights/{networkid}/user/list")
	@RightMapping(role={"system","admin","service","user"},module="network",desc="查看指定网络的授权用户列表")
	public Result rights_user_list(@PathVariable int clusterid,@PathVariable String networkid){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				if(!userNetworkSvc.hasRight(currentUserSvc.getUserId(), networkid, "wd")){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_data_right"));
					return result;
				}
			}
			
			List<UserConfigRight> userConfigList = userNetworkSvc.selectByNetworkid(networkid);		
			result.setMessage(userConfigList);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+ex.getMessage());
		}
		
		return result;
	}
}
