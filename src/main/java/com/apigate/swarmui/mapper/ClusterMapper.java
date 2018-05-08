package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.Cluster;

public interface ClusterMapper {
    int deleteByPrimaryKey(Integer clusterid);

    int insert(Cluster record);

    int insertSelective(Cluster record);

    Cluster selectByPrimaryKey(Integer clusterid);

    Cluster selectByName(String Name);
    
    List<Cluster> selectAll();
    
    int updateByPrimaryKeySelective(Cluster record);

    int updateByPrimaryKey(Cluster record);
    
    List<Cluster> selectByUserClusterKey(Integer userid);
    
}