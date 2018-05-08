package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.ClusterIdNameKey;
import com.apigate.swarmui.model.SwarmService;
import com.apigate.swarmui.model.SwarmServiceMemCpuCount;
import com.apigate.swarmui.model.UserClusterKey;

public interface SwarmServiceMapper {
    int deleteByPrimaryKey(String serviceid);

    int insert(SwarmService record);

    int insertSelective(SwarmService record);

    SwarmService selectByPrimaryKey(String serviceid);

    int updateByPrimaryKeySelective(SwarmService record);

    int updateByPrimaryKeyWithBLOBs(SwarmService record);

    int updateByPrimaryKey(SwarmService record);
    
    List<SwarmService> selectByClusterId(Integer clusterid);
    
    List<SwarmService> selectByUserClusterKey(UserClusterKey userClusterKey);
    
    SwarmService selectByClusterIdNameKey(ClusterIdNameKey clusterIdNameKey);
    
    SwarmServiceMemCpuCount countMemCpuByClusterId(Integer clusterid);
    
}