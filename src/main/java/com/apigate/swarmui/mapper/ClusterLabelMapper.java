package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.ClusterLabel;

public interface ClusterLabelMapper {
    int deleteByPrimaryKey(Integer labelid);

    int insert(ClusterLabel record);

    int insertSelective(ClusterLabel record);

    ClusterLabel selectByPrimaryKey(Integer labelid);

    int updateByPrimaryKeySelective(ClusterLabel record);

    int updateByPrimaryKey(ClusterLabel record);
    
    List<ClusterLabel> selectAll(Integer clusterid);
}