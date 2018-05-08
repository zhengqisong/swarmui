package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.UserSerivceRight;
import com.apigate.swarmui.model.UserService;

public interface UserServiceMapper {
    int insert(UserService record);

    int insertSelective(UserService record);
    
    UserService selectByPrimaryKey(UserService record);
    
    int deleteByPrimaryKey(UserService userService);
    
    int deleteByServiceid(String serviceid);
    
    int deleteByUserid(Integer userid);
    
    List<UserSerivceRight> selectByServiceid(String serviceid);
}