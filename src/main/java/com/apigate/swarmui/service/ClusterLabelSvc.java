package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apigate.swarmui.mapper.ClusterLabelMapper;
import com.apigate.swarmui.model.ClusterLabel;

@Service
public class ClusterLabelSvc {
	@Autowired
	private ClusterLabelMapper clusterLabelMapper;
	
	public int deleteByPrimaryKey(Integer labelid){
		return clusterLabelMapper.deleteByPrimaryKey(labelid);
	}

	public int insert(ClusterLabel record){
		return clusterLabelMapper.insert(record);
	}

	public int insertSelective(ClusterLabel record){
		return clusterLabelMapper.insertSelective(record);
	}

	public ClusterLabel selectByPrimaryKey(Integer labelid){
		return clusterLabelMapper.selectByPrimaryKey(labelid);
	}

	public int updateByPrimaryKeySelective(ClusterLabel record){
		return clusterLabelMapper.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKey(ClusterLabel record){
		return clusterLabelMapper.updateByPrimaryKey(record);
	}
	
	public List<ClusterLabel> selectAll(Integer clusterid){
		return clusterLabelMapper.selectAll(clusterid);
	}
}
