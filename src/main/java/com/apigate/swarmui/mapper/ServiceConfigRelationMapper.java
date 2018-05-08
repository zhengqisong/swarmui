package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.Config;
import com.apigate.swarmui.model.ServiceConfigRelation;
import com.apigate.swarmui.model.SwarmServiceBasic;

public interface ServiceConfigRelationMapper {
    int insert(ServiceConfigRelation record);
    
    int deleteByServiceId(String serviceid);
    
    List<SwarmServiceBasic> selectByConfigId(String configid);
    
    List<Config> selectByServiceId(String serviceid);
    
}