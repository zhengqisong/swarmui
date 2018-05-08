package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apigate.swarmui.mapper.ClusterMapper;
import com.apigate.swarmui.mapper.UserClusterMapper;
import com.apigate.swarmui.model.Cluster;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.UserCluster;
import com.apigate.swarmui.model.UserInfo;

@Service
public class ClusterSvc{
	
	@Autowired
	private ClusterMapper clusterMapper;
	@Autowired
	private UserClusterMapper userClusterMapper;
	
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public int deleteByPrimaryKey(Integer clusterid){
		userClusterMapper.deleteByClusterid(clusterid);
		return clusterMapper.deleteByPrimaryKey(clusterid);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public int insert(Cluster cluster){
		int num = clusterMapper.insert(cluster);
		
		UserInfo userInfo = currentUserSvc.getCurrentSessionUser();
		
		UserCluster userCluster = new UserCluster();
		userCluster.setClusterid(cluster.getClusterid());
		userCluster.setUserid(userInfo.getUserid());		
		userCluster.setRights(Dictionaries.user_role_admin.getKey());
		userClusterMapper.insert(userCluster);
		return num;
	}

	public int insertSelective(Cluster record){
		return clusterMapper.insertSelective(record);
	}

	public Cluster selectByPrimaryKey(Integer clusterid){
		return clusterMapper.selectByPrimaryKey(clusterid);
	}

	public Cluster selectByName(String Name){
		return clusterMapper.selectByName(Name);
	}
    
	public List<Cluster> selectAll(){
		return clusterMapper.selectAll();
	}
    
	public int updateByPrimaryKeySelective(Cluster record){
		return clusterMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKey(Cluster record){
		return clusterMapper.updateByPrimaryKey(record);
	}
	
	public List<Cluster> selectByUserClusterKey(Integer userid){
		return clusterMapper.selectByUserClusterKey(userid);
	}
}
