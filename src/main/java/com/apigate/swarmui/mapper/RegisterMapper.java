package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.Register;

public interface RegisterMapper {
    int deleteByPrimaryKey(Integer registerid);

    int insert(Register record);

    int insertSelective(Register record);

    Register selectByPrimaryKey(Integer registerid);

    List<Register> selectAll(Integer clusterid);
    
    int updateByPrimaryKeySelective(Register record);

    int updateByPrimaryKey(Register record);
}