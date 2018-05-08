package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.UserConfig;
import com.apigate.swarmui.model.UserConfigRight;

public interface UserConfigMapper {
    int insert(UserConfig record);

    int insertSelective(UserConfig record);
    
	int deleteByPrimaryKey(UserConfig record);

	int deleteByConfigid(String configid);

	int deleteByUserid(Integer userid);

	int updateByPrimaryKey(UserConfig record);

	UserConfig selectByPrimaryKey(UserConfig record);
	
	List<UserConfigRight> selectByConfigid(String configid);
}