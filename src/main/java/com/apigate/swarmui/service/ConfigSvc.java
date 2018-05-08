package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.docker.client.DockerSwarmConfigClient;
import com.apigate.swarmui.mapper.ConfigMapper;
import com.apigate.swarmui.mapper.UserConfigMapper;
import com.apigate.swarmui.model.Config;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.UserClusterKey;
import com.apigate.swarmui.model.UserConfig;
import com.spotify.docker.client.DockerClient;

@Service
public class ConfigSvc {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	ClusterClientSvc clusterClientSvc;
	
	@Autowired
	ConfigMapper configMapper;
	
	@Autowired
	UserConfigMapper userConfigMapper;
	
	public List<Config> list(Integer clusterid) {
		return configMapper.selectByClusterId(clusterid);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public String create(Config config) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(config.getClusterid());
		try {
			config.setCode(clusterClientSvc.randomName());
			String configid = DockerSwarmConfigClient.createConfig(docker, config.getCode(), config.getConfigData(), config.getConfigDataCharsetName(), null);
			
			try{
				com.spotify.docker.client.messages.swarm.Config dockerConfig = DockerSwarmConfigClient.inspectConfig(docker, configid);
				if(dockerConfig != null && !dockerConfig.id().equals(configid)){
					throw new Exception(MessageManager.getMsg("network.subnet_repeat"));
				}
			}catch(Exception ex){
				DockerSwarmConfigClient.deleteConfig(docker, configid);
				throw new Exception(MessageManager.getMsg("network.subnet_repeat"));
			}
			config.setConfigid(configid);
			configMapper.insert(config);
			
			//add user config relational table
			UserConfig userConfig = new UserConfig();
			userConfig.setIsowner(Dictionaries.boolean_type_yes.getKey());
			userConfig.setRights(Dictionaries.right_type_r.getKey()+Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey());
			userConfig.setConfigid(configid);
			userConfig.setUserid(currentUserSvc.getUserId());			
			userConfigMapper.insert(userConfig);
			
			return configid;
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
		
	}
	
	public com.spotify.docker.client.messages.swarm.Config inspect(String configid) throws Exception {

		Config config = configMapper.selectByPrimaryKey(configid);
		DockerClient docker = clusterClientSvc.getDockerClient(config.getClusterid());
		return DockerSwarmConfigClient.inspectConfig(docker, configid);

	}

	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void delete(String configid) throws Exception {

		Config config = configMapper.selectByPrimaryKey(configid);
		DockerClient docker = clusterClientSvc.getDockerClient(config.getClusterid());
		if (DockerSwarmConfigClient.inspectConfig(docker, configid) != null) {
			DockerSwarmConfigClient.deleteConfig(docker, configid);
		}
		
		//delete relational table
		userConfigMapper.deleteByConfigid(configid);
		
		configMapper.deleteByPrimaryKey(configid);
	}
	
	public Config selectByPrimaryKey(String configid){
		return configMapper.selectByPrimaryKey(configid);
	}
	
	public Config selectByCode(String code){
		return configMapper.selectByCode(code);
	}
	
	public List<Config> selectByUserClusterKey(int clusterid,int userid){
		UserClusterKey userClusterKey = new UserClusterKey();
		userClusterKey.setClusterid(clusterid);
		userClusterKey.setUserid(userid);
		return configMapper.selectByUserClusterKey(userClusterKey);
	}
	
	public int countByClusterId(Integer clusterid){
		return configMapper.countByClusterId(clusterid);
	}
    
	public int countByUserClusterKey(int clusterid,int userid){
		UserClusterKey userClusterKey = new UserClusterKey();
		userClusterKey.setClusterid(clusterid);
		userClusterKey.setUserid(userid);
		return configMapper.countByUserClusterKey(userClusterKey);
	}
}
