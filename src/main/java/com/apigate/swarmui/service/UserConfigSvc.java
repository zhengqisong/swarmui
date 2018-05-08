package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.apigate.swarmui.mapper.UserConfigMapper;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.UserConfig;
import com.apigate.swarmui.model.UserConfigRight;

@Service
public class UserConfigSvc {
	@Autowired
	UserConfigMapper userConfigMapper;
	
	public boolean hasRight(Integer userid, String configid, String rights){
		UserConfig userConfig = new UserConfig();
		userConfig.setConfigid(configid);
		userConfig.setUserid(userid);
		userConfig = userConfigMapper.selectByPrimaryKey(userConfig);
		if(userConfig==null){
			return false;
		}
		String userRights = userConfig.getRights();
		if(rights == null || rights.equals("")){
			return true;
		}
		
		boolean flag = true;
		if(userConfig.getIsowner().equalsIgnoreCase(Dictionaries.boolean_type_yes.getKey())){
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
	
	public void deleteByPrimaryKey(Integer userid, String configid){
		UserConfig userConfig = new UserConfig();
		userConfig.setConfigid(configid);
		userConfig.setUserid(userid);
		userConfigMapper.deleteByPrimaryKey(userConfig);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void setRight(Integer userid, String configid, String rights){
		UserConfig userConfig = new UserConfig();
		userConfig.setConfigid(configid);
		userConfig.setUserid(userid);
		userConfigMapper.deleteByPrimaryKey(userConfig);
		if(!StringUtils.isEmpty(rights)){
			userConfig.setIsowner(Dictionaries.boolean_type_no.getKey());
			userConfig.setConfigid(configid);
			userConfig.setUserid(userid);
			userConfig.setRights(rights);
			userConfigMapper.insert(userConfig);
		}
		
	}
	
	public UserConfig getRight(Integer userid, String configid){		
		UserConfig userConfig = new UserConfig();
		userConfig.setConfigid(configid);
		userConfig.setUserid(userid);
		userConfig = userConfigMapper.selectByPrimaryKey(userConfig);
		return userConfig;		
	}
	
	public List<UserConfigRight> selectByConfigid(String configid){	
		return userConfigMapper.selectByConfigid(configid);
	}
}
