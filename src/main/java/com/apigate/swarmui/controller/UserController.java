package com.apigate.swarmui.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.anntotation.RightMapping;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Result;
import com.apigate.swarmui.model.ResultPage;
import com.apigate.swarmui.model.SelectOption;
import com.apigate.swarmui.model.UserInfo;
import com.apigate.swarmui.service.CurrentUserSvc;
import com.apigate.swarmui.service.UserInfoSvc;
import com.github.pagehelper.Page;

/**
 * 
 * cluster manager
 * 
 * @author zhengqsa
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	private UserInfoSvc userInfoSvc;
	
	@RequestMapping(value = "/create")
	@RightMapping(role={"system"},module="user",desc="添加用户")
	public Result create(@RequestBody UserInfo userInfo){
		Result result = new Result();
		result.setStatus(0);
		
		if(StringUtils.isEmpty(userInfo.getAccount())
				|| StringUtils.isEmpty(userInfo.getUsername())
				|| StringUtils.isEmpty(userInfo.getPasswd())
				|| StringUtils.isEmpty(userInfo.getEmail())
				|| StringUtils.isEmpty(userInfo.getTelephone())		
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		if(StringUtils.isEmpty(userInfo.getRole())){
			userInfo.setRole(Dictionaries.user_role_default.getKey());
		}
		if(StringUtils.isEmpty(userInfo.getStatus())){
			userInfo.setStatus(Dictionaries.user_status_default.getKey());
		}
		if(!Dictionaries.keyIsIn(userInfo.getRole(), Dictionaries.user_role)){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_dictionarie"));
			return result;
		}
		if(!Dictionaries.keyIsIn(userInfo.getStatus(), Dictionaries.user_status)){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_dictionarie"));
			return result;
		}
		
		if(userInfoSvc.selectByAccount(userInfo.getAccount()) != null){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("user.account_exist"));
			return result;
		}
		
		try{
			userInfoSvc.insert(userInfo);
			result.setMessage(MessageManager.getMsg("common.add_success"));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.add_error"));
		}
		
		return result;		
	}
	
	@RequestMapping(value = "/modify/{userid}")
	@RightMapping(role={"system"}, module="user", desc="修改用户信息")
	public Result modify(@PathVariable int userid, 
			@RequestBody UserInfo userInfo){
		Result result = new Result();
		result.setStatus(0);
		
		userInfo.setUserid(userid);
		
		if(userInfo.getUserid() == null || userInfo.getUserid() < 0) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.nofound_error"));
			return result;
		}
		
		if(StringUtils.isEmpty(userInfo.getUsername())
				|| StringUtils.isEmpty(userInfo.getEmail())
				|| StringUtils.isEmpty(userInfo.getTelephone())		
				){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_empty"));
			return result;
		}
		//account can not modify
		userInfo.setAccount(null);
		if(StringUtils.isEmpty(userInfo.getPasswd())){
			userInfo.setPasswd(null);
		}
		
		UserInfo findUserInfo = userInfoSvc.selectByPrimaryKey(userid);
		if(findUserInfo == null){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.nofound_error"));
			return result;
		}
		
		if(userInfo.getRole() != null && !Dictionaries.keyIsIn(userInfo.getRole(), Dictionaries.user_role)){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_dictionarie"));
			return result;
		}
		if(userInfo.getRole() != null && !Dictionaries.keyIsIn(userInfo.getStatus(), Dictionaries.user_status)){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.can_not_dictionarie"));
			return result;
		}
		
		try {
			userInfoSvc.updateByPrimaryKeySelective(userInfo);
			result.setMessage(MessageManager.getMsg("common.modify_success"));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.modify_error"));
		}
		
		return result;		
	}
	
	@RequestMapping(value = "/list")
	@RightMapping(role={"admin", "system", "service"}, module="user", desc="用户列表信息")
	public Result list(@RequestParam int pageNum, @RequestParam int pageSize){
		Result result = new Result();
		result.setStatus(0);

		try{
			Page<UserInfo> page = userInfoSvc.selectAllUser(pageNum, pageSize);
//			int pages = page.getPages();
//			pageNum = page.getPageNum();
//			pageSize = page.getPageSize();
			
			result.setMessage(new ResultPage<UserInfo>(page));
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/delete/{userid}")
	@RightMapping(role={"system"},module="user",desc="用户列表信息")
	public Result delete(@PathVariable int userid){
		Result result = new Result();
		result.setStatus(0);
		try {
			userInfoSvc.deleteByPrimaryKey(userid);
			result.setMessage(MessageManager.getMsg("common.delete_success"));
		} catch(Exception ex) {
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(value = "/listselectoption")
	@RightMapping(role={"admin", "system", "service","user"}, module="user", desc="用户列表信息")
	public Result listselect(){
		Result result = new Result();
		result.setStatus(0);

		try{
			List<SelectOption> userList = userInfoSvc.selectforSelectOption();
//			int pages = page.getPages();
//			pageNum = page.getPageNum();
//			pageSize = page.getPageSize();
			
			result.setMessage(userList);
		}catch(Exception ex){
			result.setStatus(-1);
			result.setError(MessageManager.getMsg("common.search_error")+": "+ex.getMessage());
		}
		
		return result;
	}
	
}
