package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apigate.swarmui.docker.client.DockerSwarmExecClient;
import com.apigate.swarmui.mapper.StoragePvMapper;
import com.apigate.swarmui.model.StoragePv;
import com.spotify.docker.client.DockerClient;

@Service
public class StoragePvSvc {
	
	@Autowired
	StoragePvMapper storagePvMapper;
	@Autowired
	ClusterClientSvc clusterClientSvc;
	
	public int deleteByPrimaryKey(Integer pvid){
		return this.storagePvMapper.deleteByPrimaryKey(pvid);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public int insert(Integer clusterid, StoragePv storagePv) throws Exception{
		storagePv.setContainername(storagePv.getContainername().trim());
		
		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		String message = DockerSwarmExecClient.lsDir(docker, storagePv.getContainername(), storagePv.getLocalpath());
		
		return this.storagePvMapper.insert(storagePv);
	}
	
//	@Transactional(rollbackFor = { IllegalArgumentException.class })
//	public int insertSelective(StoragePv storagePv){
//		return this.storagePvMapper.insertSelective(storagePv);
//	}

    public StoragePv selectByPrimaryKey(Integer pvid){
    	return this.storagePvMapper.selectByPrimaryKey(pvid);
    }

//    public int updateByPrimaryKeySelective(StoragePv storagePv){
//    	return this.storagePvMapper.updateByPrimaryKeySelective(storagePv);
//    }
    
    @Transactional(rollbackFor = { IllegalArgumentException.class })
    public int updateByPrimaryKey(Integer clusterid, StoragePv storagePv) throws Exception{
    	storagePv.setContainername(storagePv.getContainername().trim());
		
    	DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
    	String message = DockerSwarmExecClient.lsDir(docker, storagePv.getContainername(), storagePv.getLocalpath());
		
    	return this.storagePvMapper.updateByPrimaryKey(storagePv);
    }
    
    public List<StoragePv> selectAll(Integer clusterid){
    	return this.storagePvMapper.selectAll(clusterid);
    }
    
}
