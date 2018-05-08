package com.apigate.swarmui.service;

import static java.lang.Long.toHexString;

import java.net.URI;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.apigate.swarmui.MessageManager;
import com.apigate.swarmui.docker.client.DockerSwarmCertificates;
import com.apigate.swarmui.mapper.ClusterMapper;
import com.apigate.swarmui.model.Cluster;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;

@Service
public class ClusterClientSvc {
	private final String nameTag = toHexString(ThreadLocalRandom.current().nextLong());
	
	@Autowired
	private ClusterMapper clusterMapper;

	public DockerClient getDockerClient(int clusterid) throws Exception {
		Cluster cluster = clusterMapper.selectByPrimaryKey(clusterid);
		if (cluster == null) {
			throw new Exception(MessageManager.getMsg("cluster.not_exist"));
		}
		String baseUrl = cluster.getBaseUrl();
		String caPem = cluster.getCapem();
		String certPem = cluster.getCertpem();
		String keyPem = cluster.getKeypem();

		DefaultDockerClient.Builder builder = DefaultDockerClient.builder();
		builder.uri(URI.create(baseUrl));
		if (!StringUtils.isEmpty(caPem) && !StringUtils.isEmpty(certPem) && !StringUtils.isEmpty(keyPem)) {
			builder.dockerCertificates(new DockerSwarmCertificates(caPem, certPem, keyPem));
		}
		DefaultDockerClient docker = builder.build();
		return docker;
	}

	public void closeDockerClient(DockerClient docker) {
		try {
			docker.close();
		} catch (Exception ex) {
		}
	}

	public String randomName() {
	    return nameTag + '_' + toHexString(ThreadLocalRandom.current().nextLong());
	}

	

}
