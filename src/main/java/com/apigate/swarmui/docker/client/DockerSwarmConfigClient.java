package com.apigate.swarmui.docker.client;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.DockerRequestException;
import com.spotify.docker.client.messages.swarm.Config;
import com.spotify.docker.client.messages.swarm.ConfigCreateResponse;
import com.spotify.docker.client.messages.swarm.ConfigSpec;

public class DockerSwarmConfigClient {
	public static String createConfig(DockerClient client, String name, String configData, String configDataCharsetName,
			Map<String, String> labels) throws DockerException, InterruptedException, Exception {
		try {

			if (configDataCharsetName == null) {
				configDataCharsetName = "utf-8";
			}
			final String configData64 = new String(Base64.encodeBase64(configData.getBytes(configDataCharsetName)));
			final ConfigSpec configSpec = ConfigSpec.builder().name(name).data(configData64).labels(labels).build();
			final ConfigCreateResponse configResponse = client.createConfig(configSpec);
			final String configId = configResponse.id();
			return configId;
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

	public static Config inspectConfig(DockerClient client, String configId)
			throws DockerException, InterruptedException {
		try {
			Config config =client.inspectConfig(configId);
			return config;
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

	public static void deleteConfig(DockerClient client, String configId) throws DockerException, InterruptedException {
		try {
			client.deleteConfig(configId);
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

	public static void updateConfig(DockerClient client, String configId, String name, String configData,
			String configDataCharsetName, Map<String, String> labels)
					throws DockerException, InterruptedException, Exception {
		try {
			Config config = client.inspectConfig(configId);

			if (configDataCharsetName == null) {
				configDataCharsetName = "utf-8";
			}
			final String configData64 = new String(Base64.encodeBase64(configData.getBytes(configDataCharsetName)));
			final ConfigSpec configSpec = ConfigSpec.builder().name(name).data(configData64).labels(labels).build();

			client.updateConfig(configId, config.version().index(), configSpec);
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
