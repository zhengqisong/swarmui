package com.apigate.swarmui.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.UserInfo;

@Service
public class CurrentUserSvc {
	private static String USER_SESSION_KEY = "session_userInfo";
	private static ThreadLocal<UserInfo> LocalUser = new ThreadLocal<UserInfo>();
	
	public void setSession(HttpServletRequest request, UserInfo userInfo){
		request.getSession().setAttribute(CurrentUserSvc.USER_SESSION_KEY, userInfo);
	}
	
	public UserInfo getSession(HttpServletRequest request){
		return (UserInfo)request.getSession().getAttribute(CurrentUserSvc.USER_SESSION_KEY);
	}
	
	public void removeSession(HttpServletRequest request){
		request.getSession().removeAttribute(CurrentUserSvc.USER_SESSION_KEY);
	}
	
	public void initCurrentSessionUser(HttpServletRequest request){
		LocalUser.remove();
		if(this.getSession(request) != null){
			LocalUser.set(this.getSession(request));
		}
	}
	
	public void removeCurrentSessionUser(){
		LocalUser.remove();
	}
	public UserInfo getCurrentSessionUser(){
		return LocalUser.get();
	}
	
	public boolean isSystemRole(){
		UserInfo user = getCurrentSessionUser();
		if(user == null){
			return false;
		}
		return Dictionaries.user_role_system.getKey().equals(user.getRole());
	}
	
	public boolean isAdminRole(){
		UserInfo user = getCurrentSessionUser();
		if(user == null){
			return false;
		}
		return Dictionaries.user_role_admin.getKey().equals(user.getRole());
	}
	
	public boolean isServiceRole(){
		UserInfo user = getCurrentSessionUser();
		if(user == null){
			return false;
		}
		return Dictionaries.user_role_service.getKey().equals(user.getRole());
	}
	
	public boolean isUserRole(){
		UserInfo user = getCurrentSessionUser();
		if(user == null){
			return false;
		}
		return Dictionaries.user_role_user.getKey().equals(user.getRole());
	}
	
	public Integer getUserId(){
		UserInfo user = getCurrentSessionUser();
		if(user == null){
			return null;
		}
		return user.getUserid();
	}
}
