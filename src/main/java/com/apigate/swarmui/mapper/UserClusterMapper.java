package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.UserCluster;
import com.apigate.swarmui.model.UserClusterKey;
import com.apigate.swarmui.model.UserClusterRight;

public interface UserClusterMapper {
    int deleteByPrimaryKey(UserClusterKey key);

    int insert(UserCluster record);

    int insertSelective(UserCluster record);
    
    List<UserCluster> selectByAll(Integer userid);
    
    int deleteByUserid(Integer userid);
    
    int deleteByClusterid(Integer clusterid);
    
    UserCluster selectByPrimaryKey(UserClusterKey key);
    
    List<UserClusterRight> selectByGlusterid(Integer clusterid);
}