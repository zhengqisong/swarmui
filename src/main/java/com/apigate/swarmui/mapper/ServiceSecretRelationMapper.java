package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.Secret;
import com.apigate.swarmui.model.ServiceSecretRelation;
import com.apigate.swarmui.model.SwarmServiceBasic;

public interface ServiceSecretRelationMapper {
    int insert(ServiceSecretRelation record);

    int deleteByServiceId(String serviceid);
    
    List<SwarmServiceBasic> selectBySecretId(String secretid);
    
    List<Secret> selectByServiceId(String serviceid);
}