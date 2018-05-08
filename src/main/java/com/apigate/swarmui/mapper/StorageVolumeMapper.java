package com.apigate.swarmui.mapper;

import java.util.List;

import com.apigate.swarmui.model.StoragePvUsed;
import com.apigate.swarmui.model.StorageVolume;
import com.apigate.swarmui.model.StorageVolumeCount;
import com.apigate.swarmui.model.UserClusterKey;

public interface StorageVolumeMapper {
	int deleteByPrimaryKey(Integer volumeid);

	int insert(StorageVolume record);

	int insertSelective(StorageVolume record);

	StorageVolume selectByPrimaryKey(Integer volumeid);

	StorageVolume selectByCode(String code);

	int updateByPrimaryKeySelective(StorageVolume record);

	int updateByPrimaryKey(StorageVolume record);

	List<StorageVolume> selectByClusterId(Integer clusterid);
	
	List<StorageVolume> selectByUserClusterKey(UserClusterKey userClusterKey);
	
	StorageVolumeCount countByClusterId(Integer clusterid);
	
	StorageVolumeCount countByUserClusterKey(UserClusterKey userClusterKey);
	
	StoragePvUsed countByPvid(Integer pvid);
	
}