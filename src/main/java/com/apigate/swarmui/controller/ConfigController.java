package com.apigate.swarmui.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bouncycastle.util.encoders.Base64;
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
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.model.UserConfig;
import com.apigate.swarmui.model.UserConfigRight;
import com.apigate.swarmui.service.ConfigSvc;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.UserClusterSvc;
import com.apigate.swarmui.service.UserConfigSvc;

/**
 * 
 * cluster manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/config")
public class ConfigController {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	UserClusterSvc userClusterSvc;
	
	@Autowired
	ConfigSvc configSvc;
	
	@Autowired
	UserConfigSvc userConfigSvc;
	@RequestMapping(value = "/{clusterid}/listself")
	@RightMapping(role={"system","admin","user"},module="config",desc="当前用户的配置列表")
	public Result listSelf(@PathVariable Integer clusterid){
		Result result = new Result();
		result.setStatus(0);
		
		try{
			//check right
			if(!currentUserSvc.isSystemRole()
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_user.getKey()))){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			result.setMessage(configSvc.selectByUserClusterKey(clusterid, currentUserSvc.getUserId()));			
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/list")
	@RightMapping(role={"system","admin","service","user"},module="config",desc="所有用户的配置列表")
	public Result list(@PathVariable Integer clusterid){
		Result result = new Result();
		result.setStatus(0);
		
		try{
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				result.setMessage(configSvc.selectByUserClusterKey(clusterid, currentUserSvc.getUserId()));
				return result;
			}
			
			result.setMessage(configSvc.list(clusterid));			
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/create")
	@RightMapping(role={"system","admin","user"},module="config",desc="创建配置")
	public Result create(@PathVariable Integer clusterid, @RequestBody Config config){
		Result result = new Result();
		result.setStatus(0);

		if(clusterid < 0){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.param_invalid"));
			return result;
		}
		config.setClusterid(clusterid);
		
		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey(),
								Dictionaries.user_role_user.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
				
		if(StringUtils.isEmpty(config.getName()) 
				|| StringUtils.isEmpty(config.getConfigData()) 
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
						
		try{
			String configid = configSvc.create(config);
			result.setMessage(configid);
		}		
		catch(Exception ex){
			ex.printStackTrace();
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.add_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/delete/{configid}")
	@RightMapping(role={"system","admin","user"},module="config",desc="删除配置")
	public Result delete(@PathVariable int clusterid, @PathVariable String configid){
		Result result = new Result();
		result.setStatus(0);
		
		try{
			Config config = configSvc.selectByPrimaryKey(configid);
			if(config==null || config.getClusterid() != clusterid){
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
			if(currentUserSvc.isUserRole() && !userConfigSvc.hasRight(currentUserSvc.getUserId(), configid, 
					Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			configSvc.delete(configid);
			result.setMessage(MessageManager.getMsg("common.delete_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+": "+ex.getMessage());
			return result;
		}
		return result;	
	}
	
	@RequestMapping(value = "/{clusterid}/inspect/{configid}")
	@RightMapping(role={"system","admin","user","service"},module="config",desc="查看集配置详情")
	public Result inspect(@PathVariable int clusterid, @PathVariable String configid){
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
			
			//check user config right
			if(currentUserSvc.isUserRole() && !userConfigSvc.hasRight(currentUserSvc.getUserId(), configid, 
					Dictionaries.right_type_r.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			Config config = configSvc.selectByPrimaryKey(configid);
			if(config==null || config.getClusterid() != clusterid){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			//Base64.decode(configid)
			com.spotify.docker.client.messages.swarm.Config inspectConfig = configSvc.inspect(configid);
			HashMap<String,Object> map = new HashMap<String,Object>();
			if(inspectConfig != null){
				String data = new String(Base64.decode(inspectConfig.configSpec().data()),"utf-8");
				HashMap<String,Object> Spec = new HashMap<String,Object>();
				Spec.put("Name", inspectConfig.configSpec().name());
				Spec.put("Labels", inspectConfig.configSpec().labels());
				Spec.put("Data", data);
				
				map.put("ID", inspectConfig.id());
				map.put("Version", inspectConfig.version());
				map.put("CreatedAt", inspectConfig.createdAt());
				map.put("UpdatedAt", inspectConfig.updatedAt());
				map.put("Spec", Spec);				
			}
			result.setMessage(map);
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;	
	}
	
	@RequestMapping(value = "/{clusterid}/setright/{configid}")
	@RightMapping(role={"system","admin","user"},module="config",desc="授权配置访问权限")
	public Result setRight(@PathVariable int clusterid, @PathVariable String configid, @RequestBody UserConfig userConfig){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			String rights = userConfig.getRights();
			Integer userid = userConfig.getUserid();
			
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
			if(currentUserSvc.isUserRole() && !userConfigSvc.hasRight(currentUserSvc.getUserId(), 
					configid, Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey())){
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
			userConfigSvc.setRight(userid, configid, rights);			
			result.setError(MessageManager.getMsg("common.modify_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.modify_error")+": "+ex.getMessage());
			return result;
		}
		return result;			
	}
	
	@RequestMapping(value = "/{clusterid}/getright/{configid}/{userid}")
	@RightMapping(role={"system","admin","user","service"},module="config",desc="获取配置的访问权限")
	public Result getRight(@PathVariable int clusterid, @PathVariable String configid, @PathVariable Integer userid){
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
			if(currentUserSvc.isUserRole() && !userConfigSvc.hasRight(currentUserSvc.getUserId(), 
					configid, Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			if(userid == null || userid < 0){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.can_not_empty"));
				return result;
			}
			result.setMessage(userConfigSvc.getRight(userid, configid));	
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
		
	}
	
	@RequestMapping(value = "/{clusterid}/rights/{configid}/user/list")
	@RightMapping(role={"system","admin","service","user"},module="config",desc="查看配置授权用户列表")
	public Result rights_user_list(@PathVariable int clusterid,@PathVariable String configid){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				if(!userConfigSvc.hasRight(currentUserSvc.getUserId(), configid, "wd")){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_data_right"));
					return result;
				}
			}
			
			List<UserConfigRight> userConfigList = userConfigSvc.selectByConfigid(configid);			
			result.setMessage(userConfigList);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+ex.getMessage());
		}
		
		return result;
	}
	
}
