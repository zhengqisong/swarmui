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
import com.apigate.swarmui.model.StorageVolume;
import com.apigate.swarmui.model.UserVolume;
import com.apigate.swarmui.model.UserVolumeRight;
import com.apigate.swarmui.service.ClusterSvc;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.StorageVolumeSvc;
import com.apigate.swarmui.service.UserClusterSvc;
import com.apigate.swarmui.service.UserVolumeSvc;

/**
 * 
 * Register manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/storagevolume")
public class StorageVolumeController {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	private UserClusterSvc userClusterSvc;
	
	@Autowired
	private StorageVolumeSvc storageVolumeSvc;
	@Autowired
	private UserVolumeSvc userVolumeSvc;
	
	@Autowired
	private ClusterSvc clusterSvc;
	
	@RequestMapping(value = "/{clusterid}/listself")
	@RightMapping(role={"system","admin","service","user"},module="storagevolume",desc="列表集群存储PV")
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
			List<StorageVolume> list = storageVolumeSvc.selectByUserClusterKey(clusterid, currentUserSvc.getUserId());
			result.setMessage(list);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/list")
	@RightMapping(role={"system","admin","service","user"},module="storagevolume",desc="列表集群存储PV")
	public Result list(@PathVariable Integer clusterid){
		Result result = new Result();
		result.setStatus(0);
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				result.setMessage(storageVolumeSvc.selectByUserClusterKey(clusterid, currentUserSvc.getUserId()));
				return result;
			}
			
			List<StorageVolume> list = storageVolumeSvc.list(clusterid);
			result.setMessage(list);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/create")
	@RightMapping(role={"system","admin","user"},module="storagevolume",desc="创建卷")
	public Result create(@PathVariable int clusterid, @RequestBody StorageVolume storageVolume){
		Result result = new Result();
		result.setStatus(0);
		
		if(clusterid < 0){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("cluster.param_invalid"));
			return result;
		}
		if(StringUtils.isEmpty(storageVolume.getName())
				|| StringUtils.isEmpty(storageVolume.getPvid())
				|| storageVolume.getStoragesize()==null	
				|| storageVolume.getStoragesize()<=0
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		
		storageVolume.setClusterid(clusterid);
		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey(),
								Dictionaries.user_role_user.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
			
		try{
			Integer volumeid = storageVolumeSvc.create(storageVolume);
			result.setMessage(volumeid);
		}
		catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.add_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/modify/{volumeid}")
	@RightMapping(role={"system","admin","user"},module="storagevolume",desc="修改卷")
	public Result modify(@PathVariable int clusterid, 
			@PathVariable int volumeid, 
			@RequestBody StorageVolume storageVolume){
		Result result = new Result();
		result.setStatus(0);

		//check right
		if(!currentUserSvc.isSystemRole() 
				&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
						Arrays.asList(Dictionaries.user_role_admin.getKey(),
								Dictionaries.user_role_user.getKey()))){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_data_right"));
			return result;
		}
		if(StringUtils.isEmpty(storageVolume.getName())
				|| storageVolume.getStoragesize()==null	
				|| storageVolume.getStoragesize()<=0
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		
		try{
			StorageVolume findStorageVolume = storageVolumeSvc.info(volumeid);
			if(findStorageVolume==null || findStorageVolume.getClusterid() != clusterid){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			//check user network right
			if(currentUserSvc.isUserRole() && !userVolumeSvc.hasRight(currentUserSvc.getUserId(), volumeid, 
					Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			storageVolume.setClusterid(clusterid);
			storageVolume.setVolumeid(volumeid);
			storageVolume.setPvid(findStorageVolume.getPvid());
			storageVolume.setCode(findStorageVolume.getCode());
			storageVolumeSvc.update(storageVolume);
			result.setMessage(volumeid);
		}
		catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.modify_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/delete/{volumeid}")
	@RightMapping(role={"system","admin","user"}, module="storagevolume",desc="删除卷")
	public Result delete(@PathVariable int clusterid, @PathVariable Integer volumeid){
		Result result = new Result();
		result.setStatus(0);
		
		try{
			StorageVolume storageVolume = storageVolumeSvc.info(volumeid);
			if(storageVolume==null || storageVolume.getClusterid() != clusterid){
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
			if(currentUserSvc.isUserRole() && !userVolumeSvc.hasRight(currentUserSvc.getUserId(), volumeid, 
					Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			storageVolumeSvc.delete(volumeid);
			result.setMessage(MessageManager.getMsg("common.delete_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.delete_error")+": "+ex.getMessage());
			return result;
		}
		return result;	
	}
	
	@RequestMapping(value = "/{clusterid}/info/{volumeid}")
	@RightMapping(role={"system","admin","user","service"},module="storagevolume",desc="查看卷详情")
	public Result info(@PathVariable int clusterid, @PathVariable Integer volumeid){
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
			if(currentUserSvc.isUserRole() && !userVolumeSvc.hasRight(currentUserSvc.getUserId(), volumeid, 
					Dictionaries.right_type_r.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			StorageVolume storageVolume = storageVolumeSvc.info(volumeid);
			if(storageVolume==null || storageVolume.getClusterid() != clusterid){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.nofound_error"));
				return result;
			}
			result.setMessage(storageVolume);
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;	
	}
	
	@RequestMapping(value = "/{clusterid}/setright/{volumeid}")
	@RightMapping(role={"system","admin","user"},module="storagevolume",desc="设置卷访问权限")
	public Result setRight(@PathVariable int clusterid, 
			@PathVariable Integer volumeid, 
			@RequestBody UserVolume userVolume){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			String rights = userVolume.getRights();
			Integer userid = userVolume.getUserid();
			
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
			if(currentUserSvc.isUserRole() && !userVolumeSvc.hasRight(currentUserSvc.getUserId(), 
					volumeid, Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey())){
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
			userVolumeSvc.setRight(userid, volumeid, rights);			
			result.setError(MessageManager.getMsg("common.modify_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.modify_error")+": "+ex.getMessage());
			return result;
		}
		return result;
	}
	
	@RequestMapping(value = "/{clusterid}/getright/{volumeid}/{userid}")
	@RightMapping(role={"system","admin","user","service"},module="storagevolume",desc="获取指定用户卷权限")
	public Result getRight(@PathVariable int clusterid, 
			@PathVariable Integer volumeid,
			@PathVariable Integer userid){
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
			if(currentUserSvc.isUserRole() && !userVolumeSvc.hasRight(currentUserSvc.getUserId(), 
					volumeid, Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey())){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("auth.not_data_right"));
				return result;
			}
			
			if(userid == null || userid < 0){
				result.setStatus(-1);
				result.setError(MessageManager.getMsg("common.can_not_empty"));
				return result;
			}
			result.setMessage(userVolumeSvc.getRight(userid, volumeid));	
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
			return result;
		}
		return result;
		
	}
	
	@RequestMapping(value = "/{clusterid}/rights/{volumeid}/user/list")
	@RightMapping(role={"system","admin","service","user"},module="storagevolume",desc="列表卷授权的所有用户")
	public Result rights_user_list(@PathVariable int clusterid,@PathVariable Integer volumeid){
		Result result = new Result();
		result.setStatus(0);
		
		try {
			//check right
			if(!currentUserSvc.isSystemRole() 
					&& !userClusterSvc.hasClusterRight(currentUserSvc.getUserId(), clusterid, 
							Arrays.asList(Dictionaries.user_role_admin.getKey(),
									Dictionaries.user_role_service.getKey()))){
				if(!userVolumeSvc.hasRight(currentUserSvc.getUserId(), volumeid, "wd")){
					result.setStatus(-1);
					result.setError(MessageManager.getMsg("auth.not_data_right"));
					return result;
				}
			}
			
			List<UserVolumeRight> userVolumeList = userVolumeSvc.selectByVolumeid(volumeid);		
			result.setMessage(userVolumeList);
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+ex.getMessage());
		}
		
		return result;
	}
}
