package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.ServiceVolumeRelation;
import com.apigate.swarmui.model.StorageVolume;
import com.apigate.swarmui.model.SwarmServiceBasic;

public interface ServiceVolumeRelationMapper {
    int insert(ServiceVolumeRelation record);
    
    int deleteByServiceId(String serviceid);
    
    List<SwarmServiceBasic> selectByVolumeId(Integer volumeid);
    
    List<StorageVolume> selectByServiceId(String serviceid);
}