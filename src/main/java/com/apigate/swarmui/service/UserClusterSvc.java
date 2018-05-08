package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apigate.swarmui.mapper.UserClusterMapper;
import com.apigate.swarmui.model.UserCluster;
import com.apigate.swarmui.model.UserClusterKey;
import com.apigate.swarmui.model.UserClusterRight;

@Service
public class UserClusterSvc {
	@Autowired
	private UserClusterMapper userClusterMapper;
	
	public int deleteByPrimaryKey(UserClusterKey key){
		return userClusterMapper.deleteByPrimaryKey(key);
	}

	public int insert(UserCluster record){
		return userClusterMapper.insert(record);
	}

	public int insertSelective(UserCluster record){
		return userClusterMapper.insertSelective(record);
	}
	
	public List<UserCluster> selectByAll(Integer userid){
		return userClusterMapper.selectByAll(userid);
	}
	public List<UserClusterRight> selectByGlusterid(Integer clusterid){
		return userClusterMapper.selectByGlusterid(clusterid);
	}
	public int deleteByUserid(Integer userid){
		return userClusterMapper.deleteByUserid(userid);
	}
	public UserCluster selectByPrimaryKey(UserClusterKey key){
		return userClusterMapper.selectByPrimaryKey(key);
	}
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void setClusterId(Integer userid, Integer clusterid, String rights){
		UserClusterKey userClusterKey = new UserClusterKey();
		userClusterKey.setClusterid(userid);
		userClusterKey.setClusterid(clusterid);
		userClusterMapper.deleteByPrimaryKey(userClusterKey);
		
		UserCluster userCluster = new UserCluster();
		userCluster.setUserid(userid);
		userCluster.setClusterid(clusterid);
		userCluster.setRights(rights);
		userClusterMapper.insert(userCluster);
		
	}
	
	public boolean hasClusterRight(Integer userid, Integer clusterid, List<String> rights){
		UserClusterKey key = new UserClusterKey();
		key.setClusterid(clusterid);
		key.setUserid(userid);
		UserCluster userCluster = userClusterMapper.selectByPrimaryKey(key);
		if(userCluster==null || rights.indexOf(userCluster.getRights()) == -1){
			return false;
		}
		return true;
	}
	
	
}
