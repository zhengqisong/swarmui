package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.UserConfigRight;
import com.apigate.swarmui.model.UserNetwork;

public interface UserNetworkMapper {
    int insert(UserNetwork record);

    int insertSelective(UserNetwork record);
    
    int deleteByPrimaryKey(UserNetwork record);
    
    int deleteByNetworkid(String networkid);
    
    int deleteByUserid(Integer userid);
    
    int updateByPrimaryKey(UserNetwork record); 
    
    UserNetwork selectByPrimaryKey(UserNetwork record);
    
    List<UserConfigRight> selectByNetworkid(String networkid);
}