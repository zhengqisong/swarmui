package com.apigate.swarmui.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.docker.client.DockerSwarmNetworkClient;
import com.apigate.swarmui.mapper.NetworkMapper;
import com.apigate.swarmui.mapper.UserNetworkMapper;
import com.apigate.swarmui.model.Dictionaries;
import com.apigate.swarmui.model.Network;
import com.apigate.swarmui.model.UserClusterKey;
import com.apigate.swarmui.model.UserNetwork;
import com.spotify.docker.client.DockerClient;

@Service
public class NetworkSvc {

	@Autowired
	ClusterClientSvc clusterClientSvc;
	
	@Autowired
	CurrentUserSvc currentUserSvc;
	
	@Autowired
	NetworkMapper networkMapper;

	@Autowired
	UserNetworkMapper userNetworkMapper;
	
	public List<Network> list(Integer clusterid) {
		return networkMapper.selectByClusterId(clusterid);
	}

	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public String create(Network network) throws Exception {

		DockerClient docker = clusterClientSvc.getDockerClient(network.getClusterid());
		try {
			boolean ipv6 = "yes".equalsIgnoreCase(network.getIpv6());
			boolean internal = "yes".equalsIgnoreCase(network.getInternal());
			network.setCode(clusterClientSvc.randomName());
			
			String networkid = DockerSwarmNetworkClient.createNetwork(docker, network.getCode(), network.getDriver(),
					network.getSubnet(), network.getIprange(), network.getGateway(), internal, ipv6, null);
			
			try{
				com.spotify.docker.client.messages.Network dockerNetwork = DockerSwarmNetworkClient.inspectNetwork(docker,
					networkid);
				if(dockerNetwork != null && !dockerNetwork.driver().equalsIgnoreCase(network.getDriver())){
					throw new Exception(MessageManager.getMsg("network.subnet_repeat"));
				}
			}catch(Exception ex){
				DockerSwarmNetworkClient.deleteNetwork(docker, networkid);
				throw new Exception(MessageManager.getMsg("network.subnet_repeat"));
			}
			network.setNetworkid(networkid);
			networkMapper.insert(network);
			
			//add user network relational table
			UserNetwork userNetwork = new UserNetwork();
			userNetwork.setIsowner(Dictionaries.boolean_type_yes.getKey());
			userNetwork.setRights(Dictionaries.right_type_r.getKey()+Dictionaries.right_type_w.getKey()+Dictionaries.right_type_d.getKey());
			userNetwork.setNetworkid(networkid);
			userNetwork.setUserid(currentUserSvc.getUserId());
			userNetworkMapper.insert(userNetwork);
			
			return networkid;
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
	}

	public com.spotify.docker.client.messages.Network inspect(int clusterid, String networkid) throws Exception {

		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		return DockerSwarmNetworkClient.inspectNetwork(docker, networkid);

	}

	@Transactional(rollbackFor = { IllegalArgumentException.class })
	public void delete(int clusterid, String networkid) throws Exception {

		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		if (DockerSwarmNetworkClient.inspectNetwork(docker, networkid) != null) {
			DockerSwarmNetworkClient.deleteNetwork(docker, networkid);
		}
		//delete relational table
		userNetworkMapper.deleteByNetworkid(networkid);
		//delete network
		networkMapper.deleteByPrimaryKey(networkid);
	}

	public Network selectByPrimaryKey(String networkid){
		return networkMapper.selectByPrimaryKey(networkid);
	}
	
	public Network selectByCode(String code){
		return networkMapper.selectByCode(code);
	}
	
	public List<Network> selectByUserClusterKey(int clusterid,int userid){
		UserClusterKey userClusterKey = new UserClusterKey();
		userClusterKey.setClusterid(clusterid);
		userClusterKey.setUserid(userid);
		return networkMapper.selectByUserClusterKey(userClusterKey);
	}
	
	public int countByClusterId(Integer clusterid){
		return networkMapper.countByClusterId(clusterid);
	}
	
	public int countByUserClusterKey(int clusterid,int userid){
		UserClusterKey userClusterKey = new UserClusterKey();
		userClusterKey.setClusterid(clusterid);
		userClusterKey.setUserid(userid);
		return networkMapper.countByUserClusterKey(userClusterKey);
    }
}
