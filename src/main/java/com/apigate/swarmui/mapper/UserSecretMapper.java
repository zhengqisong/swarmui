package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.UserConfigRight;
import com.apigate.swarmui.model.UserSecret;

public interface UserSecretMapper {
	int insert(UserSecret record);

	int insertSelective(UserSecret record);

	int deleteByPrimaryKey(UserSecret record);

	int deleteBySecretid(String secretid);

	int deleteByUserid(Integer userid);

	int updateByPrimaryKey(UserSecret record);

	UserSecret selectByPrimaryKey(UserSecret record);
	
	List<UserConfigRight> selectBySecretid(String secretid);
}