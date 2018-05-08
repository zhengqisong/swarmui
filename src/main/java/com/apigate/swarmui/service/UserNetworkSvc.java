package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.apigate.swarmui.mapper.UserNetworkMapper;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.UserConfigRight;
import com.apigate.swarmui.model.UserNetwork;

@Service
public class UserNetworkSvc {
	@Autowired
	UserNetworkMapper userNetworkMapper;
	
	public boolean hasRight(Integer userid, String networkid, String rights){
		UserNetwork userNetwork = new UserNetwork();
		userNetwork.setNetworkid(networkid);
		userNetwork.setUserid(userid);
		userNetwork = userNetworkMapper.selectByPrimaryKey(userNetwork);
		if(userNetwork==null){
			return false;
		}
		String userRights = userNetwork.getRights();
		if(rights == null || rights.equals("")){
			return true;
		}
		
		boolean flag = true;
		if(userNetwork.getIsowner().equalsIgnoreCase(Dictionaries.boolean_type_yes.getKey())){
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
	
	public void deleteByPrimaryKey(Integer userid, String networkid){
		UserNetwork userNetwork = new UserNetwork();
		userNetwork.setNetworkid(networkid);
		userNetwork.setUserid(userid);
		userNetworkMapper.deleteByPrimaryKey(userNetwork);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void setRight(Integer userid, String networkid, String rights){
		UserNetwork userNetwork = new UserNetwork();
		userNetwork.setIsowner(Dictionaries.boolean_type_no.getKey());
		userNetwork.setNetworkid(networkid);
		userNetwork.setUserid(userid);
		userNetwork.setRights(rights);
		userNetworkMapper.deleteByPrimaryKey(userNetwork);
		if(!StringUtils.isEmpty(rights))
			userNetworkMapper.insert(userNetwork);
		
	}
	
	public UserNetwork getRight(Integer userid, String networkid){		
		UserNetwork userNetwork = new UserNetwork();
		userNetwork.setNetworkid(networkid);
		userNetwork.setUserid(userid);
		userNetwork = userNetworkMapper.selectByPrimaryKey(userNetwork);
		return userNetwork;
		
	}
	
	public List<UserConfigRight> selectByNetworkid(String networkid){
		return userNetworkMapper.selectByNetworkid(networkid);
	}
}
