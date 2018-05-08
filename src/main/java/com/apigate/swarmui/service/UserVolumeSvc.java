package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.apigate.swarmui.mapper.UserVolumeMapper;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.UserVolume;
import com.apigate.swarmui.model.UserVolumeRight;

@Service
public class UserVolumeSvc {
	@Autowired
	UserVolumeMapper userVolumeMapper;
	
	public boolean hasRight(Integer userid, Integer volumeid, String rights){
		UserVolume userVolume = new UserVolume();
		userVolume.setVolumeid(volumeid);
		userVolume.setUserid(userid);
		userVolume = userVolumeMapper.selectByPrimaryKey(userVolume);
		if(userVolume==null){
			return false;
		}
		String userRights = userVolume.getRights();
		if(rights == null || rights.equals("")){
			return true;
		}
		
		boolean flag = true;
		if(userVolume.getIsowner().equalsIgnoreCase(Dictionaries.boolean_type_yes.getKey())){
			return flag;
		}
		if(rights.toLowerCase().indexOf(Dictionaries.right_type_r.getKey()) != -1 
				&& userRights.indexOf(Dictionaries.right_type_r.getKey())==-1){
			flag = false;
		}
		if(rights.toLowerCase().indexOf(Dictionaries.right_type_w.getKey()) != -1 
				&& userRights.indexOf(Dictionaries.right_type_w.getKey())==-1){
			flag = false;
		}
		if(rights.toLowerCase().indexOf(Dictionaries.right_type_d.getKey()) != -1 
				&& userRights.indexOf(Dictionaries.right_type_d.getKey())==-1){
			flag = false;
		}
		return flag;
	}
	
	public void deleteByPrimaryKey(Integer userid, Integer volumeid){
		UserVolume userVolume = new UserVolume();
		userVolume.setVolumeid(volumeid);
		userVolume.setUserid(userid);
		userVolumeMapper.deleteByPrimaryKey(userVolume);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void setRight(Integer userid, Integer volumeid, String rights){
		UserVolume userVolume = new UserVolume();
		userVolume.setIsowner(Dictionaries.boolean_type_no.getKey());
		userVolume.setVolumeid(volumeid);
		userVolume.setUserid(userid);
		userVolume.setRights(rights);
		userVolumeMapper.deleteByPrimaryKey(userVolume);
		if(!StringUtils.isEmpty(rights))
			userVolumeMapper.insert(userVolume);
		
	}
	
	public UserVolume getRight(Integer userid, Integer volumeid){		
		UserVolume userVolume = new UserVolume();
		userVolume.setVolumeid(volumeid);
		userVolume.setUserid(userid);
		userVolume = userVolumeMapper.selectByPrimaryKey(userVolume);
		return userVolume;
		
	}
	
	public List<UserVolumeRight> selectByVolumeid(Integer volumeid){
		return userVolumeMapper.selectByVolumeid(volumeid);
	}
}
