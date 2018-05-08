package com.apigate.swarmui.docker.client;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.DockerRequestException;
import com.spotify.docker.client.messages.swarm.Secret;
import com.spotify.docker.client.messages.swarm.SecretCreateResponse;
import com.spotify.docker.client.messages.swarm.SecretSpec;

public class DockerSwarmSecretClient {
	public static String createSecret(DockerClient client, String name, String secretData, String secretDataCharsetName, Map<String, String> labels)
			throws DockerException, InterruptedException, Exception{
		try {
			if(secretDataCharsetName == null){
				secretDataCharsetName = "utf-8";
			}
			final String secretData64 = new String(Base64.encodeBase64(secretData.getBytes(secretDataCharsetName)));
			final SecretSpec secretSpec = SecretSpec.builder().name(name).data(secretData64).labels(labels).build();
			final SecretCreateResponse secretResponse = client.createSecret(secretSpec);
			final String secretId = secretResponse.id();
			return secretId;
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

	public static Secret inspectSecret(DockerClient client, String secretId) throws DockerException, InterruptedException {
		try {
			return client.inspectSecret(secretId);
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
	
	public static void deleteSecret(DockerClient client, String secretId) throws DockerException, InterruptedException {
		try {
			client.deleteSecret(secretId);
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
	
}
