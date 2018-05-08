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
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.model.Secret;
import com.apigate.swarmui.model.UserConfigRight;
import com.apigate.swarmui.model.UserSecret;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.SecretSvc;
import com.apigate.swarmui.service.UserClusterSvc;
import com.apigate.swarmui.service.UserSecretSvc;

/**
 * 
 * cluster manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/secret")
public class SecretController {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	UserClusterSvc userClusterSvc;
	
	@Autowired
	SecretSvc secretSvc;
	
	@Autowired
	UserSecretSvc userSecretSvc;
	@RequestMapping(value = "/{clusterid}/listself")
	@RightMapping(role={"system","admin","user"},module="secret",desc="当前用户的敏感文件列表")
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
			result.setMessage(secretSvc.selectByUserClusterKey(clusterid, currentUserSvc.getUserId()));			
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/list")
	@RightMapping(role={"system","admin","service","user"},module="secret",desc="列出所有的敏感文件")
	public Result list(@PathVariable Integer clusterid){
		Result result = new Result();
		result.setStatus(0);
		
		try{
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				result.setMessage(secretSvc.selectByUserClusterKey(clusterid, currentUserSvc.getUserId()));
				return result;
			}
			
			result.setMessage(secretSvc.list(clusterid));			
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/create")
	@RightMapping(role={"system","admin","user"},module="secret",desc="创建敏感文件")
	public Result create(@PathVariable Integer clusterid, @RequestBody Secret secret){
		Result result = new Result();
		result.setStatus(0);

		if(clusterid < 0){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.param_invalid"));
			return result;
		}
		secret.setClusterid(clusterid);
		
		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey(),
								Dictionaries.user_role_user.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
				
		if(StringUtils.isEmpty(secret.getName()) 
				|| StringUtils.isEmpty(secret.getSecretData()) 
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
						
		try{
			String secretid = secretSvc.create(secret);
			result.setMessage(secretid);
		}		
		catch(Exception ex){
			ex.printStackTrace();
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.add_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/delete/{secretid}")
	@RightMapping(role={"system","admin","user"},module="secret",desc="删除敏感文件")
	public Result delete(@PathVariable int clusterid, @PathVariable String secretid){
		Result result = new Result();
		result.setStatus(0);
		
		try{
			Secret secret = secretSvc.selectByPrimaryKey(secretid);
			if(secret==null || secret.getClusterid() != clusterid){
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
			if(currentUserSvc.isUserRole() && !userSecretSvc.hasRight(currentUserSvc.getUserId(), secretid, 
					Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			secretSvc.delete(secretid);
			result.setMessage(MessageManager.getMsg("common.delete_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+": "+ex.getMessage());
			return result;
		}
		return result;	
	}
	
	@RequestMapping(value = "/{clusterid}/inspect/{secretid}")
	@RightMapping(role={"system","admin","user","service"},module="secret",desc="查看敏感文件详情")
	public Result inspect(@PathVariable int clusterid, @PathVariable String secretid){
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
			
			//check user network right
			if(currentUserSvc.isUserRole() && !userSecretSvc.hasRight(currentUserSvc.getUserId(), secretid, 
					Dictionaries.right_type_r.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			Secret secret = secretSvc.selectByPrimaryKey(secretid);
			if(secret==null || secret.getClusterid() != clusterid){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			result.setMessage(secretSvc.inspect(secretid));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;	
	}
	
	@RequestMapping(value = "/{clusterid}/setright/{secretid}")
	@RightMapping(role={"system","admin","user"},module="secret",desc="设置敏感文件访问权限")
	public Result setRight(@PathVariable int clusterid, @PathVariable String secretid, @RequestBody UserSecret userSecret){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			String rights = userSecret.getRights();
			Integer userid = userSecret.getUserid();
			
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
			if(currentUserSvc.isUserRole() && !userSecretSvc.hasRight(currentUserSvc.getUserId(), 
					secretid, Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey())){
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
			userSecretSvc.setRight(userid, secretid, rights);			
			result.setError(MessageManager.getMsg("common.modify_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.modify_error")+": "+ex.getMessage());
			return result;
		}
		return result;			
	}
	
	@RequestMapping(value = "/{clusterid}/getright/{secretid}/{userid}")
	@RightMapping(role={"system","admin","user","service"},module="secret",desc="获取敏感文件访问权限")
	public Result getRight(@PathVariable int clusterid, @PathVariable String secretid, @PathVariable Integer userid){
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
			if(currentUserSvc.isUserRole() && !userSecretSvc.hasRight(currentUserSvc.getUserId(), 
					secretid, Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			if(userid == null || userid < 0){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.can_not_empty"));
				return result;
			}
			result.setMessage(userSecretSvc.getRight(userid, secretid));	
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
		
	}
	
	@RequestMapping(value = "/{clusterid}/rights/{secretid}/user/list")
	@RightMapping(role={"system","admin","service","user"},module="secret",desc="列出敏感文件具有访问权限的所有用户")
	public Result rights_user_list(@PathVariable int clusterid,@PathVariable String secretid){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				if(!userSecretSvc.hasRight(currentUserSvc.getUserId(), secretid, "wd")){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_data_right"));
					return result;
				}
			}
			
			List<UserConfigRight> userConfigList = userSecretSvc.selectBySecretid(secretid);		
			result.setMessage(userConfigList);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+ex.getMessage());
		}
		
		return result;
	}
}
