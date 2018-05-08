package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.apigate.swarmui.mapper.UserSecretMapper;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.UserConfigRight;
import com.apigate.swarmui.model.UserSecret;

@Service
public class UserSecretSvc {
	@Autowired
	UserSecretMapper userSecretMapper;
	
	public boolean hasRight(Integer userid, String secretid, String rights){
		UserSecret userSecret = new UserSecret();
		userSecret.setSecretid(secretid);
		userSecret.setUserid(userid);
		userSecret = userSecretMapper.selectByPrimaryKey(userSecret);
		if(userSecret==null){
			return false;
		}
		String userRights = userSecret.getRights();
		if(rights == null || rights.equals("")){
			return true;
		}
		
		boolean flag = true;
		if(userSecret.getIsowner().equalsIgnoreCase(Dictionaries.boolean_type_yes.getKey())){
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
	
	public void deleteByPrimaryKey(Integer userid, String secretid){
		UserSecret userSecret = new UserSecret();
		userSecret.setSecretid(secretid);
		userSecret.setUserid(userid);
		userSecretMapper.deleteByPrimaryKey(userSecret);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void setRight(Integer userid, String secretid, String rights){
		UserSecret userSecret = new UserSecret();
		userSecret.setSecretid(secretid);
		userSecret.setUserid(userid);
		userSecretMapper.deleteByPrimaryKey(userSecret);
		if(!StringUtils.isEmpty(rights)){
			userSecret.setIsowner(Dictionaries.boolean_type_no.getKey());
			userSecret.setSecretid(secretid);
			userSecret.setUserid(userid);
			userSecret.setRights(rights);
			userSecretMapper.insert(userSecret);
		}
		
	}
	
	public UserSecret getRight(Integer userid, String secretid){		
		UserSecret userSecret = new UserSecret();
		userSecret.setSecretid(secretid);
		userSecret.setUserid(userid);
		userSecret = userSecretMapper.selectByPrimaryKey(userSecret);
		return userSecret;		
	}
	
	public List<UserConfigRight> selectBySecretid(String secretid){
		return userSecretMapper.selectBySecretid(secretid);
	}
	
}
