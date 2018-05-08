package com.apigate.swarmui.docker.client;

import java.util.Collections;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.DockerRequestException;
import com.spotify.docker.client.messages.Ipam;
import com.spotify.docker.client.messages.IpamConfig;
import com.spotify.docker.client.messages.Network;
import com.spotify.docker.client.messages.NetworkConfig;
import com.spotify.docker.client.messages.NetworkCreation;

public class DockerSwarmNetworkClient {
	public static String createNetwork(DockerClient client, String name, String driver, String subnet, String ipRange,
			String gateway, boolean enableIPv6, boolean internal, Map<String, String> labels)
			throws DockerException, InterruptedException {

		try {
			final IpamConfig ipamConfig = IpamConfig.create(subnet, ipRange, gateway);
			final Ipam ipam = Ipam.builder().driver("default").config(Collections.singletonList(ipamConfig)).build();

			final NetworkConfig networkConfig = NetworkConfig.builder().name(name).driver(driver).checkDuplicate(true)
					.ipam(ipam).internal(internal).enableIPv6(enableIPv6).labels(labels).build();

			// System.out.println(Entity.json(networkConfig));
			final NetworkCreation networkCreation = client.createNetwork(networkConfig);
			return networkCreation.id();
		} catch (DockerRequestException ex) {
			String resBody = ex.getResponseBody();
			try {
				String message = JSON.parseObject(resBody).getString("message");
				if (!StringUtils.isEmpty(message)) {
					resBody = message;
				}
			} catch (Exception ex2) {
			}
			throw new InterruptedException(resBody);
		}

	}

	public static void deleteNetwork(DockerClient client, String networkId) throws DockerException, InterruptedException {
		try {
			client.removeNetwork(networkId);
		} catch (DockerRequestException ex) {
			String resBody = ex.getResponseBody();
			try {
				String message = JSON.parseObject(resBody).getString("message");
				if (!StringUtils.isEmpty(message)) {
					resBody = message;
				}
			} catch (Exception ex2) {
			}
			throw new InterruptedException(resBody);
		}
	}

	public  static Network inspectNetwork(DockerClient client, String networkId) throws DockerException, InterruptedException {
		try {
			Network network = client.inspectNetwork(networkId);
			return network;
		} catch (DockerRequestException ex) {
			String resBody = ex.getResponseBody();
			try {
				if (resBody != null) {
					String message = JSON.parseObject(resBody).getString("message");
					if (!StringUtils.isEmpty(message)) {
						resBody = message;
					}
				}
			} catch (Exception ex2) {
			}
			throw new InterruptedException(resBody);
		}
	}
}
