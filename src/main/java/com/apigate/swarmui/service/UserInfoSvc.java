package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apigate.swarmui.mapper.UserInfoMapper;
import com.apigate.swarmui.model.SelectOption;
import com.apigate.swarmui.model.UserInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class UserInfoSvc {
	
	@Autowired
	private UserInfoMapper userInfoMapper;
	
	public int deleteByPrimaryKey(Integer userid) {
		return userInfoMapper.deleteByPrimaryKey(userid);
	}

	public int insert(UserInfo record) {
		return userInfoMapper.insert(record);
	}

	public int insertSelective(UserInfo record) {
		return userInfoMapper.insertSelective(record);
	}

	public UserInfo selectByPrimaryKey(Integer userid) {
		return userInfoMapper.selectByPrimaryKey(userid);
	}
	
	public UserInfo selectByAccount(String account) {
		return userInfoMapper.selectByAccount(account);
	}	
	
	public int updateByPrimaryKeySelective(UserInfo record) {
		return userInfoMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKey(UserInfo record) {
		return userInfoMapper.updateByPrimaryKey(record);
	}
	
	public Page<UserInfo> selectAllUser(int pageNum, int pageSize){
		//将参数传给这个方法就可以实现物理分页了，非常简单。
        PageHelper.startPage(pageNum, pageSize);
        Page<UserInfo> page = userInfoMapper.selectAllUser();
        
        return page;
	}
	
	public UserInfo logonByAccountAndPasswd(UserInfo record){
		return userInfoMapper.logonByAccountAndPasswd(record);
	}
	
	public List<SelectOption> selectforSelectOption(){
		return userInfoMapper.selectforSelectOption();
	}
}
