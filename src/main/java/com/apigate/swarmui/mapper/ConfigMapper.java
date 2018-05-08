package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.Config;
import com.apigate.swarmui.model.Secret;
import com.apigate.swarmui.model.UserClusterKey;

public interface ConfigMapper {
    int deleteByPrimaryKey(String configid);

    int insert(Config record);

    int insertSelective(Config record);

    Config selectByPrimaryKey(String configid);
    
    Config selectByCode(String code);

    int updateByPrimaryKeySelective(Config record);

    int updateByPrimaryKey(Config record);
    
    List<Config> selectByClusterId(Integer clusterid);
    
    List<Config> selectByUserClusterKey(UserClusterKey record);
    
    int countByClusterId(Integer clusterid);
    
    int countByUserClusterKey(UserClusterKey record);
    
}