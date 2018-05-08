package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.Secret;
import com.apigate.swarmui.model.UserClusterKey;

public interface SecretMapper {
    int deleteByPrimaryKey(String secretid);

    int insert(Secret record);

    int insertSelective(Secret record);

    Secret selectByPrimaryKey(String secretid);
    
    Secret selectByCode(String code);

    int updateByPrimaryKeySelective(Secret record);

    int updateByPrimaryKey(Secret record);
    
    List<Secret> selectByClusterId(Integer clusterid);
    
    List<Secret> selectByUserClusterKey(UserClusterKey record);
    
    int countByClusterId(Integer clusterid);
    
    int countByUserClusterKey(UserClusterKey record);
}