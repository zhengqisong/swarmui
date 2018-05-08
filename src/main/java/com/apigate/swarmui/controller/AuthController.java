package com.apigate.swarmui.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.anntotation.RightMapping;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.model.UserInfo;
import com.apigate.swarmui.model.UserKey;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.UserInfoSvc;
import com.apigate.swarmui.service.UserKeySvc;

/**
 * 
 * cluster manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private UserInfoSvc userInfoSvc;
	@Autowired
	private UserKeySvc userKeySvc;
	
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@RequestMapping(value = "/login")
	public Result login(HttpServletRequest request, @RequestBody UserInfo userInfo){		
		Result result = new Result();
		result.setStatus(0);
		
		UserInfo findUserInfo = userInfoSvc.logonByAccountAndPasswd(userInfo);
		if(findUserInfo == null){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.account_passwd_invalid"));
			return result;
		}
		
		if(!Dictionaries.user_status_normal.getKey().equals(findUserInfo.getStatus())){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_normal"));
			return result;
		}
		
		request.getSession().getId();
		request.getSession().setAttribute("session_userInfo", findUserInfo);
		findUserInfo.setPasswd("");
		result.setMessage(findUserInfo);
		return result;
	}
	
	@RequestMapping(value = "/logout")
	public Result login(HttpServletRequest request){	
		Result result = new Result();
		result.setStatus(0);
		request.getSession().removeAttribute("session_userInfo");
		result.setMessage(MessageManager.getMsg("auth.logout_success"));
		return result;
	}
	
	@RequestMapping(value = "/loginByKey")
	public Result loginByKey(HttpServletRequest request, @RequestBody UserKey userKey){
		Result result = new Result();
		result.setStatus(0);
		
		UserKey findUserKey = userKeySvc.selectByPrimaryKey(userKey.getKeyid());
		if(findUserKey == null || !findUserKey.getAppsecret().equals(userKey.getAppsecret())){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.userkey_invalid"));
			return result;
		}
		
		UserInfo userInfo = userInfoSvc.selectByPrimaryKey(findUserKey.getUserid());
		if(userInfo == null){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("auth.not_exist"));
			return result;
		}
		
		request.getSession().getId();
		request.getSession().setAttribute("session_userInfo", userInfo);
		userInfo.setPasswd("");
		result.setMessage(userInfo);
		return result;
	}
	
	@RequestMapping(value = "/test")
	public Result getThreadId(HttpServletRequest request){
		Thread th=Thread.currentThread(); 
		Result result = new Result();
		result.setStatus(0);
		result.setMessage(th.getId()+","+th.getName()+","+request.getSession().getId());
		return result;
	}
	
	@RequestMapping(value = "/userinfo")
	public Result userinfo(HttpServletRequest request){		
		Result result = new Result();
		result.setStatus(0);
		
		result.setMessage(currentUserSvc.getCurrentSessionUser());
		return result;
	}
}
