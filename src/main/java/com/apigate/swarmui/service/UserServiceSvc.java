package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.apigate.swarmui.mapper.UserServiceMapper;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.UserSerivceRight;
import com.apigate.swarmui.model.UserService;

@Service
public class UserServiceSvc {
	@Autowired
	UserServiceMapper userServiceMapper;
	
	public boolean hasRight(Integer userid, String serviceid, String rights){
		UserService userService = new UserService();
		userService.setServiceid(serviceid);
		userService.setUserid(userid);
		userService = userServiceMapper.selectByPrimaryKey(userService);
		if(userService==null){
			return false;
		}
		String userRights = userService.getRights();
		if(rights == null || rights.equals("")){
			return true;
		}
		
		boolean flag = true;
		if(userService.getIsowner().equalsIgnoreCase(Dictionaries.boolean_type_yes.getKey())){
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
	
	public void deleteByPrimaryKey(Integer userid, String serviceid){
		UserService userService = new UserService();
		userService.setServiceid(serviceid);
		userService.setUserid(userid);
		userServiceMapper.deleteByPrimaryKey(userService);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void setRight(Integer userid, String serviceid, String rights){
		UserService userService = new UserService();
		userService.setServiceid(serviceid);
		userService.setUserid(userid);
		userServiceMapper.deleteByPrimaryKey(userService);
		if(!StringUtils.isEmpty(rights)){
			userService.setIsowner(Dictionaries.boolean_type_no.getKey());
			userService.setServiceid(serviceid);
			userService.setUserid(userid);
			userService.setRights(rights);
			userServiceMapper.insert(userService);
		}
		
	}
	
	public UserService getRight(Integer userid, String serviceid){		
		UserService userService = new UserService();
		userService.setServiceid(serviceid);
		userService.setUserid(userid);
		userService = userServiceMapper.selectByPrimaryKey(userService);
		return userService;		
	}
	
	public List<UserSerivceRight> selectByServiceid(String serviceid){
		return userServiceMapper.selectByServiceid(serviceid);
	}
}
