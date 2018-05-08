package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apigate.swarmui.mapper.UserKeyMapper;
import com.apigate.swarmui.model.UserKey;

@Service
public class UserKeySvc {
	@Autowired
	private UserKeyMapper userKeyMapper;
	
	public int deleteByPrimaryKey(String keyid){
		return userKeyMapper.deleteByPrimaryKey(keyid);
	}

	public int insert(UserKey record){
    	return userKeyMapper.insert(record);
    }

	public int insertSelective(UserKey record){
    	return userKeyMapper.insertSelective(record);
    }

	public UserKey selectByPrimaryKey(String keyid){
    	return userKeyMapper.selectByPrimaryKey(keyid);
    }

	public int updateByPrimaryKeySelective(UserKey record){
    	return userKeyMapper.updateByPrimaryKeySelective(record);
    }

	public int updateByPrimaryKey(UserKey record){
    	return userKeyMapper.updateByPrimaryKey(record);
    }
	
	public List<UserKey> selectByAll(Integer userid){
		return userKeyMapper.selectAll(userid);
	}
	
}
