package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.StoragePv;

public interface StoragePvMapper {
    int deleteByPrimaryKey(Integer pvid);

    int insert(StoragePv record);

    int insertSelective(StoragePv record);

    StoragePv selectByPrimaryKey(Integer pvid);

    int updateByPrimaryKeySelective(StoragePv record);

    int updateByPrimaryKey(StoragePv record);
    
    List<StoragePv> selectAll(Integer clusterid);
}