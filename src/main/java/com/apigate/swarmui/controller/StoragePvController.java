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
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.model.StoragePv;
import com.apigate.swarmui.service.ClusterSvc;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.StoragePvSvc;
import com.apigate.swarmui.service.UserClusterSvc;

/**
 * 
 * Register manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/storagepv")
public class StoragePvController {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	private UserClusterSvc userClusterSvc;
	
	@Autowired
	private StoragePvSvc storagePvSvc;
	
	@Autowired
	private ClusterSvc clusterSvc;
	
	//@Autowired  
	//private MessageSource messageSource;
	
	@RequestMapping(value = "/{clusterid}/create")
	@RightMapping(role={"system","admin"},module="storagepv",desc="创建集群存储PV")
	public Result create(@PathVariable int clusterid, @RequestBody StoragePv storagePv){
		Result result = new Result();
		result.setStatus(0);
		
		if(clusterid < 0){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		storagePv.setClusterid(clusterid);
		
		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId()
						, clusterid, Arrays.asList(Dictionaries.user_role_admin.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
		
		if(StringUtils.isEmpty(storagePv.getName()) 
				|| StringUtils.isEmpty(storagePv.getStoragetype())
				|| StringUtils.isEmpty(storagePv.getLocalpath())
				|| StringUtils.isEmpty(storagePv.getContainername())
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		
		if(clusterSvc.selectByPrimaryKey(storagePv.getClusterid()) == null){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.not_exist"));
			return result;
		}
		
		try {
			storagePvSvc.insert(clusterid, storagePv);
			result.setMessage(MessageManager.getMsg("common.add_success"));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.sql_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/list")
	@RightMapping(role={"system","admin","service","user"},module="storagepv",desc="列表集群存储PV")
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
			
			List<StoragePv> list = storagePvSvc.selectAll(clusterid);
			result.setMessage(list);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/delete/{pvid}")
	@RightMapping(role={"system","admin"},module="storagepv",desc="删除集群存储PV")
	public Result delete(@PathVariable Integer clusterid, @PathVariable int pvid){
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
			
			StoragePv storagePv = storagePvSvc.selectByPrimaryKey(pvid);
			if(storagePv==null || storagePv.getClusterid() != clusterid){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			storagePvSvc.deleteByPrimaryKey(pvid);
			result.setMessage(MessageManager.getMsg("common.delete_success"));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+": "+ex.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/modify/{pvid}")
	@RightMapping(role={"system","admin"},module="storagepv",desc="修改集群存储PV")
	public Result modify(@PathVariable Integer clusterid, @PathVariable int pvid, @RequestBody StoragePv storagePv){
		Result result = new Result();
		result.setStatus(0);
		
		if(pvid < 0){
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
				
		if(StringUtils.isEmpty(storagePv.getContainername())
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("register.param_invalid"));
			return result;
		}

		StoragePv findStroragePv = storagePvSvc.selectByPrimaryKey(pvid); 
		if(findStroragePv==null || findStroragePv.getClusterid() != clusterid) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.nofound_error"));
			return result;
		}
		
		storagePv.setClusterid(findStroragePv.getClusterid());
		
		if(clusterSvc.selectByPrimaryKey(storagePv.getClusterid()) == null){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.not_exist"));
			return result;
		}
		
		try {
			storagePv.setPvid(pvid);
			storagePv.setName(findStroragePv.getName());
			storagePv.setLocalpath(findStroragePv.getLocalpath());
			storagePv.setStoragetype(findStroragePv.getStoragetype());
			storagePvSvc.updateByPrimaryKey(clusterid, storagePv);
			result.setMessage(MessageManager.getMsg("common.modify_success"));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.modify_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/info/{pvid}")
	@RightMapping(role={"system","admin","service"},module="storagepv",desc="查看集群存储PV")
	public Result info(@PathVariable Integer clusterid, @PathVariable int pvid){
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
			StoragePv storagePv = storagePvSvc.selectByPrimaryKey(pvid);
			
			if(storagePv == null || storagePv.getClusterid() != clusterid){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			result.setMessage(storagePv);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+ex.getMessage());
		}
		
		return result;
	}
	
}
