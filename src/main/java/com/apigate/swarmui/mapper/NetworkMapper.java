package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.Network;
import com.apigate.swarmui.model.UserClusterKey;

public interface NetworkMapper {
    int deleteByPrimaryKey(String networkid);

    int insert(Network record);

    int insertSelective(Network record);

    Network selectByPrimaryKey(String networkid);
    
    Network selectByCode(String code);

    int updateByPrimaryKeySelective(Network record);

    int updateByPrimaryKey(Network record);
    
    List<Network> selectByClusterId(Integer clusterid);
    
    List<Network> selectByUserClusterKey(UserClusterKey record);
    
    int countByClusterId(Integer clusterid);
    
    int countByUserClusterKey(UserClusterKey record);
    
}