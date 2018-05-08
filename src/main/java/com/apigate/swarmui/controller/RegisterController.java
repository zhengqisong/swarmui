package com.apigate.swarmui.controller;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.anntotation.RightMapping;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Register;
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.service.ClusterSvc;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.RegisterSvc;
import com.apigate.swarmui.service.UserClusterSvc;

/**
 * 
 * Register manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/register")
public class RegisterController {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	private UserClusterSvc userClusterSvc;
	
	@Autowired
	private RegisterSvc registerSvc;
	
	@Autowired
	private ClusterSvc clusterSvc;
	
	//@Autowired  
	//private MessageSource messageSource;
	
	@RequestMapping(value = "/{clusterid}/create")
	@RightMapping(role={"system","admin"},module="register",desc="创建集群镜像中心")
	public Result create(@PathVariable Integer clusterid, @RequestBody Register register){
		Result result = new Result();
		result.setStatus(0);
		
		if(clusterid < 0){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		register.setClusterid(clusterid);
		
		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
		
		if(StringUtils.isEmpty(register.getName()) 
				|| StringUtils.isEmpty(register.getAddress())
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		if(StringUtils.isEmpty(register.getIsauth()) || !"yes".equalsIgnoreCase(register.getIsauth())){
			register.setIsauth("no");
		}else{
			register.setIsauth("yes");
		}
		
		if(clusterSvc.selectByPrimaryKey(register.getClusterid()) == null){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.not_exist"));
			return result;
		}
		
		try{
			registerSvc.insert(register);
			result.setMessage(MessageManager.getMsg("common.add_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.sql_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/list")
	@RightMapping(role={"system","admin","service","user"},module="register",desc="列表集群镜像中心")
	public Result list(@PathVariable Integer clusterid){
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
			
			List<Register> list = registerSvc.selectAll(clusterid);
			result.setMessage(list);
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/delete/{registerid}")
	@RightMapping(role={"system","admin"},module="register",desc="删除集群镜像中心")
	public Result delete(@PathVariable Integer clusterid, @PathVariable int registerid){
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
			
			Register register = registerSvc.selectByPrimaryKey(registerid);
			if(register==null || register.getClusterid() != clusterid){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			registerSvc.deleteByPrimaryKey(registerid);
			result.setMessage(MessageManager.getMsg("common.delete_success"));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+": "+ex.getMessage());
		}		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/modify/{registerid}")
	@RightMapping(role={"system","admin"},module="register",desc="修改集群镜像中心")
	public Result modify(@PathVariable int clusterid, @PathVariable int registerid, @RequestBody Register register){
		Result result = new Result();
		result.setStatus(0);
		
		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
				
		if(registerid < 0){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.nofound_error"));
		}
		
		if(StringUtils.isEmpty(register.getName()) 
				|| StringUtils.isEmpty(register.getAddress())
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		if(StringUtils.isEmpty(register.getIsauth()) || !"yes".equalsIgnoreCase(register.getIsauth())){
			register.setIsauth("no");
		}else{
			register.setIsauth("yes");
		}
		
		Register findResister = registerSvc.selectByPrimaryKey(registerid); 
		if(findResister==null || findResister.getClusterid() != clusterid) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.nofound_error"));
			return result;
		}
		
		register.setClusterid(findResister.getClusterid());
		
		if(clusterSvc.selectByPrimaryKey(register.getClusterid()) == null){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.not_exist"));
			return result;
		}
		
		try {
			register.setRegisterid(registerid);
			registerSvc.updateByPrimaryKey(register);
			result.setMessage(MessageManager.getMsg("common.modify_success"));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.modify_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/info/{registerid}")
	@RightMapping(role={"system","admin","service"},module="register",desc="查看集群镜像中心")
	public Result info(@PathVariable int clusterid, @PathVariable int registerid){
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
			
			Register register = registerSvc.selectByPrimaryKey(registerid);
			
			if(register == null || register.getClusterid() != clusterid) {
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			result.setMessage(register);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+ex.getMessage());
		}
		
		return result;
	}
	
}
