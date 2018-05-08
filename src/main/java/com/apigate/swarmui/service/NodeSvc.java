package com.apigate.swarmui.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apigate.swarmui.docker.client.DockerSwarmNodeClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.swarm.Node;
import com.spotify.docker.client.messages.swarm.NodeInfo;

@Service
public class NodeSvc {
	@Autowired
	ClusterClientSvc clusterClientSvc;
	
	public List<Node> list(int clusterid) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		try {
			return DockerSwarmNodeClient.listNode(docker);
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
	}
	
	public NodeInfo inspect(int clusterid, String nodeid) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		try {
			return DockerSwarmNodeClient.inspect(docker, nodeid);
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
	}
	
	public void addLabel(int clusterid, String nodeid, Map<String,String> label) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		try {
			DockerSwarmNodeClient.addLabel(docker, nodeid, label);
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
	}
	
	public void rmLabel(int clusterid, String nodeid, List<String> label) throws Exception {
		DockerClient docker = clusterClientSvc.getDockerClient(clusterid);
		try {
			DockerSwarmNodeClient.rmLabel(docker, nodeid, label);
		} finally {
			clusterClientSvc.closeDockerClient(docker);
		}
	}
}
