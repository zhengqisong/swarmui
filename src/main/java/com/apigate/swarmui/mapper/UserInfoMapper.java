package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.SelectOption;
import com.apigate.swarmui.model.UserInfo;
import com.github.pagehelper.Page;

public interface UserInfoMapper {
    int deleteByPrimaryKey(Integer userid);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer userid);
    
    UserInfo selectByAccount(String account);
    
    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
    
    Page<UserInfo> selectAllUser();
    
    UserInfo logonByAccountAndPasswd(UserInfo record);
    
    List<SelectOption> selectforSelectOption();
}