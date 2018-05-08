package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.docker.client.DockerSwarmSecretClient;
import com.apigate.swarmui.mapper.SecretMapper;
import com.apigate.swarmui.mapper.UserSecretMapper;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Secret;
import com.apigate.swarmui.model.UserClusterKey;
import com.apigate.swarmui.model.UserSecret;
import com.spotify.docker.client.DockerClient;

@Service
public class SecretSvc {
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	ClusterClientSvc clusterClientSvc;
	
	@Autowired
	SecretMapper secretMapper;
	
	@Autowired
	UserSecretMapper userSecretMapper;
	
	public List<Secret> list(Integer clusterid) {
		return secretMapper.selectByClusterId(clusterid);
	}
	
	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public String create(Secret secret) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(secret.getClusterid());
		try {
			secret.setCode(clusterClientSvc.randomName());
			String secretid = DockerSwarmSecretClient.createSecret(docker, secret.getCode(), secret.getSecretData(), secret.getSecretDataCharsetName(), null);
			
			try{
				com.spotify.docker.client.messages.swarm.Secret dockerSecret = DockerSwarmSecretClient.inspectSecret(docker, secretid);
				if(dockerSecret != null && !dockerSecret.id().equals(secretid)){
					throw new Exception(MessageManager.getMsg("network.subnet_repeat"));
				}
			}catch(Exception ex){
				DockerSwarmSecretClient.deleteSecret(docker, secretid);
				throw new Exception(MessageManager.getMsg("network.subnet_repeat"));
			}
			secret.setSecretid(secretid);
			secretMapper.insert(secret);
			
			//add user secret relational table
			UserSecret userSecret = new UserSecret();
			userSecret.setIsowner(Dictionaries.boolean_type_yes.getKey());
			userSecret.setRights(Dictionaries.right_type_r.getKey()+Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey());
			userSecret.setSecretid(secretid);
			userSecret.setUserid(currentUserSvc.getUserId());			
			userSecretMapper.insert(userSecret);
			
			return secretid;
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
		
	}
	
	public com.spotify.docker.client.messages.swarm.Secret inspect(String secretId) throws Exception {

		Secret secret = secretMapper.selectByPrimaryKey(secretId);
		DockerClient docker = clusterClientSvc.getDockerClient(secret.getClusterid());
		return DockerSwarmSecretClient.inspectSecret(docker, secretId);

	}

	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void delete(String secretid) throws Exception {

		Secret secret = secretMapper.selectByPrimaryKey(secretid);
		DockerClient docker = clusterClientSvc.getDockerClient(secret.getClusterid());
		if (DockerSwarmSecretClient.inspectSecret(docker, secretid) != null) {
			DockerSwarmSecretClient.deleteSecret(docker, secretid);
		}
		
		//delete relational table
		userSecretMapper.deleteBySecretid(secretid);
		
		secretMapper.deleteByPrimaryKey(secretid);
	}
	
	public Secret selectByPrimaryKey(String secretid){
		return secretMapper.selectByPrimaryKey(secretid);
	}
	
	public Secret selectByCode(String code){
		return secretMapper.selectByCode(code);
	}
	
	public List<Secret> selectByUserClusterKey(int clusterid,int userid){
		UserClusterKey userClusterKey = new UserClusterKey();
		userClusterKey.setClusterid(clusterid);
		userClusterKey.setUserid(userid);
		return secretMapper.selectByUserClusterKey(userClusterKey);
	}
	
	public int countByClusterId(Integer clusterid){
    	return secretMapper.countByClusterId(clusterid);
    }
    
	public int countByUserClusterKey(int clusterid,int userid){
		UserClusterKey userClusterKey = new UserClusterKey();
		userClusterKey.setClusterid(clusterid);
		userClusterKey.setUserid(userid);
    	return secretMapper.countByUserClusterKey(userClusterKey);
    }
}
