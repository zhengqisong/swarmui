package com.apigate.swarmui.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.model.UserKey;
import com.apigate.swarmui.service.UserInfoSvc;
import com.apigate.swarmui.service.UserKeySvc;

/**
 * 
 * cluster manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/userkey")
public class UserKeyController {
	@Autowired
	private UserInfoSvc userInfoSvc;
	
	@Autowired
	private UserKeySvc userKeySvc;
	
	
	@RequestMapping(value = "/{userid}/create")
	public Result create(@PathVariable int userid){
		Result result = new Result();
		result.setStatus(0);
		
		if(userInfoSvc.selectByPrimaryKey(userid) == null){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_exist"));
			return result;
		}
		
		try {
			UserKey userKey = new UserKey();
			userKey.setUserid(userid);
			userKey.setAppsecret(UUID.randomUUID().toString().replaceAll("-", ""));
			userKey.setKeyid(UUID.randomUUID().toString().replaceAll("-", ""));
			userKeySvc.insert(userKey);
			result.setMessage(MessageManager.getMsg("common.add_success"));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.add_error"));
		}
		
		return result;			
	}
	
	@RequestMapping(value = "/{userid}/delete/{keyid}")
	public Result delete(@PathVariable int userid, @PathVariable String keyid){
		Result result = new Result();
		result.setStatus(0);
		try {
			userKeySvc.deleteByPrimaryKey(keyid);
			result.setMessage(MessageManager.getMsg("common.delete_success"));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/{userid}/list")
	public Result list(@PathVariable int userid){
		Result result = new Result();
		result.setStatus(0);
		try{
			List<UserKey> list = userKeySvc.selectByAll(userid);
			
			result.setMessage(list);
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
}
