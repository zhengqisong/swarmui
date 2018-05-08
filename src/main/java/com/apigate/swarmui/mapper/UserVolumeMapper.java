package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.UserVolume;
import com.apigate.swarmui.model.UserVolumeRight;

public interface UserVolumeMapper {
    int insert(UserVolume record);

    int insertSelective(UserVolume record);
    
    int deleteByPrimaryKey(UserVolume userVolume);
    
    int deleteByVolumeid(Integer volumeid);
    
    int deleteByUserid(Integer userid);
    
    int updateByPrimaryKey(UserVolume userVolume);
    
    UserVolume selectByPrimaryKey(UserVolume userVolume);
    
    List<UserVolumeRight> selectByVolumeid(Integer volumeid);
}