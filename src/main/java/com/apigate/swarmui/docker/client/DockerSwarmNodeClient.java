package com.apigate.swarmui.docker.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.apigate.swarmui.MessageManager;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.swarm.Node;
import com.spotify.docker.client.messages.swarm.NodeInfo;
import com.spotify.docker.client.messages.swarm.NodeSpec;

public class DockerSwarmNodeClient {
	public static List<Node> listNode(DockerClient client) 
			throws DockerException, InterruptedException {

		List<Node> nodes = client.listNodes();

		return nodes;
	}
	
	public static NodeInfo inspect(DockerClient client, String nodeId) 
			throws DockerException, InterruptedException {

		NodeInfo node = client.inspectNode(nodeId);

		return node;
	}
	
	public static void addLabel(DockerClient client, String nodeId, Map<String,String> label) 
			throws DockerException, InterruptedException {
		NodeInfo node = client.inspectNode(nodeId);
		Map<String,String> oldLabels = node.spec().labels();
		long version = node.version().index();
		if(oldLabels== null){
			oldLabels = label;
		}else{
			Iterator<String> iterator = oldLabels.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				if(label.containsKey(key)){
					throw new InterruptedException(MessageManager.getMsg("node.label_exist"));
				}
				label.put(key, oldLabels.get(key));
			}
		}
		
		client.updateNode(nodeId, version, NodeSpec.builder()
				.labels(label)
				.role(node.spec().role())
				.name(node.spec().name())
				.availability(node.spec().availability())
				.build());
	}
	
	public static void rmLabel(DockerClient client, String nodeId, List<String> label) 
			throws DockerException, InterruptedException {
		NodeInfo node = client.inspectNode(nodeId);
		Map<String,String> oldLabels = node.spec().labels();
		long version = node.version().index();
		Map<String,String> map = new HashMap<String,String>();
		if(oldLabels != null){
			Iterator<String> iterator = oldLabels.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				map.put(key, oldLabels.get(key));
			}
			
			for(String key: label){
				map.remove(key);
			}
		}
		client.updateNode(nodeId, version, NodeSpec.builder()
				.labels(map)
				.role(node.spec().role())
				.name(node.spec().name())
				.availability(node.spec().availability())
				.build());
	}
	
	public static void setLabel(DockerClient client, String nodeId, Map<String,String> label) 
			throws DockerException, InterruptedException {
		NodeInfo node = client.inspectNode(nodeId);
		long version = node.version().index();
		
		client.updateNode(nodeId, version, NodeSpec.builder()
				.labels(label)
				.role(node.spec().role())
				.name(node.spec().name())
				.availability(node.spec().availability())
				.build());
	}	
}
