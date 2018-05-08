package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.Network;
import com.apigate.swarmui.model.ServiceNetworkRelation;
import com.apigate.swarmui.model.SwarmServiceBasic;

public interface ServiceNetworkRelationMapper {
    int insert(ServiceNetworkRelation record);

    int deleteByServiceId(String serviceid);
    
    List<SwarmServiceBasic> selectByNetworkId(String networkid);
    
    List<Network> selectByServiceId(String serviceid);
    
}