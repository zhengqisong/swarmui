package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.UserKey;

public interface UserKeyMapper {
    int deleteByPrimaryKey(String keyid);

    int insert(UserKey record);

    int insertSelective(UserKey record);

    UserKey selectByPrimaryKey(String keyid);

    int updateByPrimaryKeySelective(UserKey record);

    int updateByPrimaryKey(UserKey record);
    
    List<UserKey> selectAll(Integer userid);
}