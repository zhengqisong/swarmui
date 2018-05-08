package com.apigate.swarmui.docker.client;

import org.springframework.util.StringUtils;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ExecCreation;

public class DockerSwarmExecClient {
	public static String createDir(DockerClient client, String name, String path)
			throws DockerException, InterruptedException {
		final String[] command = {"sh", "-c", "mkdir -p "+ path};
		final ExecCreation execCreation = client.execCreate(
				name, command, DockerClient.ExecCreateParam.attachStdout(),
		    DockerClient.ExecCreateParam.attachStderr());
		
		final LogStream output = client.execStart(execCreation.id());
		final String execOutput = output.readFully();
		return execOutput;
	}
	
	public static String rmDir(DockerClient client, String name, String path)
			throws DockerException, InterruptedException {
		final String[] command = {"sh", "-c", "rm -rf "+ path};
		final ExecCreation execCreation = client.execCreate(
				name, command, DockerClient.ExecCreateParam.attachStdout(),
		    DockerClient.ExecCreateParam.attachStderr());
		
		final LogStream output = client.execStart(execCreation.id());
		final String execOutput = output.readFully();
		return execOutput;
	}
	
	public static String lsDir(DockerClient client, String name, String path)
			throws DockerException, InterruptedException {
		final String[] command = {"sh", "-c", "ls "+ path};
		final ExecCreation execCreation = client.execCreate(
				name, command, DockerClient.ExecCreateParam.attachStderr());

		final LogStream output = client.execStart(execCreation.id());
		final String execOutput = output.readFully();
		if(!StringUtils.isEmpty(execOutput)){
			throw new DockerException(execOutput);
		}
		return execOutput;
	}
}
