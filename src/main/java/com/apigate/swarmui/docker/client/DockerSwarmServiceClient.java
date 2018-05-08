package com.apigate.swarmui.docker.client;

import static com.spotify.docker.client.VersionCompare.compareVersion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.apigate.swarmui.model.Config;
import com.apigate.swarmui.model.Network;
import com.apigate.swarmui.model.Secret;
import com.apigate.swarmui.model.StorageVolume;
import com.apigate.swarmui.model.service.CreateSerivce;
import com.apigate.swarmui.model.service.CreateServiceCodeResource;
import com.apigate.swarmui.model.service.ServiceConfig;
import com.apigate.swarmui.model.service.ServiceContainer;
import com.apigate.swarmui.model.service.ServiceEndpointSpec;
import com.apigate.swarmui.model.service.ServiceFile;
import com.apigate.swarmui.model.service.ServiceHealthcheck;
import com.apigate.swarmui.model.service.ServiceLogDriver;
import com.apigate.swarmui.model.service.ServiceResource;
import com.apigate.swarmui.model.service.ServiceResources;
import com.apigate.swarmui.model.service.ServiceRestartPolicy;
import com.apigate.swarmui.model.service.ServiceSecret;
import com.apigate.swarmui.model.service.ServiceUpdateConfig;
import com.apigate.swarmui.model.service.TaskCriteria;
import com.apigate.swarmui.model.service.TaskTemplate;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.exceptions.DockerRequestException;
import com.spotify.docker.client.messages.ContainerConfig.Healthcheck;
import com.spotify.docker.client.messages.ServiceCreateResponse;
import com.spotify.docker.client.messages.mount.BindOptions;
import com.spotify.docker.client.messages.mount.Mount;
import com.spotify.docker.client.messages.mount.TmpfsOptions;
import com.spotify.docker.client.messages.mount.VolumeOptions;
import com.spotify.docker.client.messages.swarm.ConfigBind;
import com.spotify.docker.client.messages.swarm.ConfigFile;
import com.spotify.docker.client.messages.swarm.ContainerSpec;
import com.spotify.docker.client.messages.swarm.Driver;
import com.spotify.docker.client.messages.swarm.EndpointSpec;
import com.spotify.docker.client.messages.swarm.NetworkAttachmentConfig;
import com.spotify.docker.client.messages.swarm.Placement;
import com.spotify.docker.client.messages.swarm.PortConfig;
import com.spotify.docker.client.messages.swarm.PortConfig.PortConfigPublishMode;
import com.spotify.docker.client.messages.swarm.ResourceRequirements;
import com.spotify.docker.client.messages.swarm.Resources;
import com.spotify.docker.client.messages.swarm.RestartPolicy;
import com.spotify.docker.client.messages.swarm.SecretBind;
import com.spotify.docker.client.messages.swarm.SecretFile;
import com.spotify.docker.client.messages.swarm.Service;
import com.spotify.docker.client.messages.swarm.ServiceMode;
import com.spotify.docker.client.messages.swarm.ServiceSpec;
import com.spotify.docker.client.messages.swarm.Task;
import com.spotify.docker.client.messages.swarm.TaskSpec;
import com.spotify.docker.client.messages.swarm.UpdateConfig;

public class DockerSwarmServiceClient {
	private static boolean dockerApiVersionLessThan(String dockerApiVersion, final String expected) throws Exception {
		return compareVersion(dockerApiVersion, expected) < 0;
	}

	private static boolean dockerApiVersionAtLeast(String dockerApiVersion, final String expected) throws Exception {
		return compareVersion(dockerApiVersion, expected) >= 0;
	}

	private static List<ConfigBind> buildConfigBind(List<ServiceConfig> serviceConfigs, CreateServiceCodeResource cscr) {
		if (serviceConfigs != null && serviceConfigs.size() > 0) {
			List<ConfigBind> configs = new Vector<ConfigBind>();
			for (ServiceConfig serviceConfig : serviceConfigs) {
				Config Config =cscr.getConfig(serviceConfig.getCode());
				ServiceFile file = serviceConfig.getFile();
				final ConfigFile configFile = ConfigFile.builder()
						// 目标文件可以路径
						.name(file.getName())
						// 用户ID
						.uid(file.getUid())
						// 用户组ID
						.gid(file.getGid())
						// 权限 类似于chmod 0640 bsecret
						.mode(file.getMode()).build();
				final ConfigBind configBind = ConfigBind.builder().file(configFile)
						.configId(Config.getConfigid()).configName(Config.getCode()).build();
				configs.add(configBind);
			}
			return configs;

		}
		return null;
	}

	private static List<SecretBind> buildSecretBind(List<ServiceSecret> serviceSecrets, CreateServiceCodeResource cscr) {
		if (serviceSecrets != null && serviceSecrets.size() > 0) {
			List<SecretBind> secrets = new Vector<SecretBind>();
			for (ServiceSecret serviceSecret : serviceSecrets) {
				Secret secret = cscr.getSecret(serviceSecret.getCode());
				ServiceFile file = serviceSecret.getFile();
				final SecretFile secretFile = SecretFile.builder()
						// 目标文件可以路径
						.name(file.getName())
						// 用户ID
						.uid(file.getUid())
						// 用户组ID
						.gid(file.getGid())
						// 权限 类似于chmod 0640 bsecret
						.mode(file.getMode()).build();
				final SecretBind secretBind = SecretBind.builder().file(secretFile)
						.secretId(secret.getSecretid()).secretName(secret.getCode()).build();
				secrets.add(secretBind);

			}
			return secrets;
		}
		return null;
	}

	private static Healthcheck buildHealthcheck(String dockerApiVersion, ServiceHealthcheck serviceHealthcheck)
			throws Exception {
		if (serviceHealthcheck != null) {
			final long interval = DockerSwarmServiceClient.dockerApiVersionLessThan(dockerApiVersion, "1.30")
					? serviceHealthcheck.getInterval() : serviceHealthcheck.getInterval() * 1000000;
			final long timeout = DockerSwarmServiceClient.dockerApiVersionLessThan(dockerApiVersion, "1.30")
					? serviceHealthcheck.getTimeout() : serviceHealthcheck.getTimeout() * 1000000;
			final int retries = 3;
			final long startPeriod = DockerSwarmServiceClient.dockerApiVersionLessThan(dockerApiVersion, "1.30")
					? serviceHealthcheck.getStartPeriod() : serviceHealthcheck.getStartPeriod() * 1000000;
			Healthcheck healthcheck = Healthcheck.create(serviceHealthcheck.getTest(), interval, timeout, retries,
					startPeriod);
			return healthcheck;
		}
		
		return null;
	}
	
	private static List<Mount> buildMounts(List<com.apigate.swarmui.model.service.ServiceMount> mounts, CreateServiceCodeResource cscr){
		if(mounts!=null && mounts.size()>0){
			List<Mount> mountList = new Vector<Mount>();
			for(com.apigate.swarmui.model.service.ServiceMount mount : mounts){
				StorageVolume volume = cscr.getStorageVolume(mount.getCode());
				mountList.add(Mount.builder()
					.readOnly(mount.isReadOnly()==null?false:mount.isReadOnly())
					.target(mount.getTarget())
					.type(StringUtils.isEmpty(mount.getType())?"bind":mount.getType())//"bind":绑定主机目录或文件, "volume":创建卷或绑定已存在卷(暂不支持), "tmpfs":内存文件绑定到容器(暂不支持)
					.source(volume.getLocalpath())
					//.bindOptions(BindOptions.builder().propagation(""))
					//.volumeOptions(volumeOptions)
					//.tmpfsOptions(tmpfsOptions)
					.build()
				);
			}
			return mountList;
		}
		return null;
	}
	
	private static ContainerSpec buildContainerSpec(String dockerApiVersion, ServiceContainer serviceContainer, CreateServiceCodeResource cscr)
			throws Exception {
		ContainerSpec.Builder containerSpecBuilder = ContainerSpec.builder();
		// command
		containerSpecBuilder.command(serviceContainer.getCommand());
		// args
		containerSpecBuilder.args(serviceContainer.getArgs());
		// image
		containerSpecBuilder.image(serviceContainer.getImage());
		// env
		containerSpecBuilder.env(serviceContainer.getEnv());
		// labels
		containerSpecBuilder.labels(serviceContainer.getLabels());
		// config
		containerSpecBuilder.configs(DockerSwarmServiceClient.buildConfigBind(serviceContainer.getConfigs(), cscr));
		// secret
		containerSpecBuilder.secrets(DockerSwarmServiceClient.buildSecretBind(serviceContainer.getSecrets(), cscr));
		// healthcheck
		containerSpecBuilder.healthcheck(
				DockerSwarmServiceClient.buildHealthcheck(dockerApiVersion, serviceContainer.getHealthcheck()));

		// mount
		containerSpecBuilder.mounts(DockerSwarmServiceClient.buildMounts(serviceContainer.getMounts(), cscr));
		/*		
		containerSpecBuilder.mounts(
				Mount.builder()
				.readOnly(true)
				.target("/etc/app/conf/aa.conf")
				.type("bind")//"bind":绑定主机目录或文件, "volume":创建卷或绑定已存在卷, "tmpfs":内存文件绑定到容器
				.source("/data/glusterfs_block01/app1")
				//.bindOptions(BindOptions.builder().propagation(""))
				//.volumeOptions(volumeOptions)
				//.tmpfsOptions(tmpfsOptions)
				.build());
		*/
		return containerSpecBuilder.build();
	}

	private static ResourceRequirements buildResourceRequirements(ServiceResources serviceResources) {
		if (serviceResources != null) {
			ServiceResource serviceResource = serviceResources.getLimit();
			ServiceResource serviceReservation = serviceResources.getReservation();

			ResourceRequirements.Builder resourceBuilder = ResourceRequirements.builder();
			if (serviceResource != null) {
				Resources.Builder resBuilder = Resources.builder();
				if(serviceResource.getCpu()!=null && serviceResource.getCpu()>0){
					resBuilder.nanoCpus(serviceResource.getCpu()*1000000000);
				}
				if(serviceResource.getMemory()!=null && serviceResource.getMemory()>0){
					resBuilder.memoryBytes(serviceResource.getMemory()*1024*1024);
				}
				resourceBuilder.limits(resBuilder.build());
			}
			if (serviceReservation != null) {
				Resources.Builder resBuilder = Resources.builder();
				if(serviceReservation.getCpu()!=null && serviceReservation.getCpu()>0){
					resBuilder.nanoCpus(serviceReservation.getCpu()*1000000000);
				}
				if(serviceReservation.getMemory()!=null && serviceReservation.getMemory()>0){
					resBuilder.memoryBytes(serviceReservation.getMemory()*1024*1024);
				}
				
				resourceBuilder.reservations(resBuilder.build());
			}
			return resourceBuilder.build();
		}
		return null;
	}

	private static RestartPolicy buildRestartPolicy(ServiceRestartPolicy restartPolicy) {
		if (restartPolicy != null) {
			return RestartPolicy.builder().condition(restartPolicy.getCondition()).delay(restartPolicy.getDelay()*1000000000)
					.maxAttempts(restartPolicy.getMaxAttempts()).window(restartPolicy.getWindow()).build();
		}
		return null;
	}

	private static Driver buildLogDriver(ServiceLogDriver LogDriver) {
		if (LogDriver != null) {
			return Driver.builder().name(LogDriver.getName()).options(LogDriver.getOptions()).build();
		}
		return null;
	}

	private static TaskSpec buildTaskSpec(String dockerApiVersion, TaskTemplate taskTemplate, CreateServiceCodeResource cscr) throws Exception {
		final TaskSpec.Builder taskSpecBuilder = TaskSpec.builder();
		// container
		taskSpecBuilder.containerSpec(
				DockerSwarmServiceClient.buildContainerSpec(dockerApiVersion, taskTemplate.getContainer(), cscr));

		// resource

		taskSpecBuilder.resources(DockerSwarmServiceClient.buildResourceRequirements(taskTemplate.getResources()));

		// restartPolicy
		taskSpecBuilder.restartPolicy(DockerSwarmServiceClient.buildRestartPolicy(taskTemplate.getRestartPolicy()));
		
		// placement
		if (taskTemplate.getPlacement() != null) {
			taskSpecBuilder.placement(Placement.create(taskTemplate.getPlacement()));
		}
		// logDriver
		taskSpecBuilder.logDriver(DockerSwarmServiceClient.buildLogDriver(taskTemplate.getLogDriver()));
		
		return taskSpecBuilder.build();
	}
	
	private static UpdateConfig buidUpdateConfig(String dockerApiVersion, ServiceUpdateConfig updateConfig)
			throws Exception {
		if(updateConfig==null){
			return null;
		}
		Long delay = updateConfig.getDelay();
		String failureAction = updateConfig.getFailureAction();
		Long parallelism = updateConfig.getParallelism();
		//pause”|”continue”|”rollback
//		if(!"pause".equals(failureAction) && !"continue".equals(failureAction) && !"rollback".equals(failureAction)){
//			throw new Exception();
//		}
		if(delay!=null){
			delay = delay*1000000000;
		}
		return UpdateConfig.create(parallelism, delay, failureAction);
	}
	
	private static EndpointSpec buildEndpointSpec(String dockerApiVersion, ServiceEndpointSpec[] endpointSpecs)
			throws Exception {
		if (endpointSpecs != null && endpointSpecs.length > 0) {
			EndpointSpec.Builder endpointSpecBuild = EndpointSpec.builder();
			for (ServiceEndpointSpec portSpec : endpointSpecs) {
				final PortConfig.Builder portConfigBuilder = PortConfig.builder()
						.name(portSpec.getName())
						.protocol(portSpec.getProtocol())
						.publishedPort(portSpec.getPublishedPort())
						.targetPort(portSpec.getTargetPort());

				if (PortConfigPublishMode.INGRESS.toString().equalsIgnoreCase(portSpec.getMode())) {
					if (dockerApiVersionAtLeast(dockerApiVersion, "1.25")) {
						portConfigBuilder.publishMode(PortConfigPublishMode.INGRESS);
					}
				} else if (PortConfigPublishMode.HOST.toString().equalsIgnoreCase(portSpec.getMode())) {
					portConfigBuilder.publishMode(PortConfigPublishMode.INGRESS);
				}
				endpointSpecBuild.addPort(portConfigBuilder.build());
			}
			return endpointSpecBuild.build();

		}
		return null;
	}

	private static List<NetworkAttachmentConfig> buildNetwork(List<String> networks, CreateServiceCodeResource cscr) {
		if (networks != null && networks.size() > 0) {
			List<NetworkAttachmentConfig> networkList = new Vector<NetworkAttachmentConfig>();
			for (String networkCode : networks) {
				Network network = cscr.getNetwork(networkCode);
				networkList.add(NetworkAttachmentConfig.builder().target(network.getNetworkid()).build());
			}
			return networkList;
		}
		return null;
	}

	private static ServiceMode buildServiceMode(String mode, Integer replicas) {
		if (mode.equalsIgnoreCase("replicas")) {
			return ServiceMode.withReplicas(replicas);
		} else if (mode.equalsIgnoreCase("global")) {
			return ServiceMode.withGlobal();
		}
		return null;
	}

	private static ServiceSpec buildServiceSpec(String dockerApiVersion, CreateSerivce createService, CreateServiceCodeResource cscr) throws Exception {
		ServiceSpec.Builder specBuilder = ServiceSpec.builder().name(createService.getName());
		// publish port
		specBuilder.endpointSpec(
				DockerSwarmServiceClient.buildEndpointSpec(dockerApiVersion, createService.getEndpointSpec()));
		// mode
		specBuilder
				.mode(DockerSwarmServiceClient.buildServiceMode(createService.getMode(), createService.getReplicas()));
		// label
		specBuilder.labels(createService.getLabels());
		// network
		specBuilder.networks(DockerSwarmServiceClient.buildNetwork(createService.getTemplate().getNetwork(), cscr));

		// taskTemplate
		specBuilder.taskTemplate(DockerSwarmServiceClient.buildTaskSpec(dockerApiVersion, createService.getTemplate(), cscr));
		specBuilder.updateConfig(DockerSwarmServiceClient.buidUpdateConfig(dockerApiVersion, createService.getUpdateConfig()));
		ServiceSpec spec = specBuilder.build();
		return spec;
	}

	public static String createService(DockerClient client, CreateSerivce createService, CreateServiceCodeResource cscr)
			throws DockerException, InterruptedException, Exception {
		try {
			String dockerApiVersion = client.version().apiVersion();
			ServiceCreateResponse response = client
					.createService(DockerSwarmServiceClient.buildServiceSpec(dockerApiVersion, createService, cscr));
			String serviceid = response.id();

			return serviceid;
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

	public static void updateService(DockerClient client, String serviceid, boolean force, CreateSerivce createService, CreateServiceCodeResource cscr)
			throws DockerException, InterruptedException, Exception {
		try {
			String dockerApiVersion = client.version().apiVersion();
			Service inpsectService = client.inspectService(serviceid);

			client.updateService(serviceid, inpsectService.version().index(),
					DockerSwarmServiceClient.buildServiceSpec(dockerApiVersion, createService, cscr));
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

	public static Service inspectService(DockerClient client, String serviceid)
			throws DockerException, InterruptedException, Exception {
		try {
			Service inpsectService = client.inspectService(serviceid);
			return inpsectService;
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

	public static void removeService(DockerClient client, String serviceid)
			throws DockerException, InterruptedException, Exception {
		try {
			client.removeService(serviceid);
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

	public static List<Service> listService(DockerClient client)
			throws DockerException, InterruptedException, Exception {
		try {
			return client.listServices();
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

	// public List<Service> logsService(DockerClient client, String serviceid,
	// Map<String,String> params)
	// throws DockerException, InterruptedException, Exception{
	// return client.serviceLogs(serviceid, params)
	// }
	public static List<Task> taskList(DockerClient client, TaskCriteria taskCriteria)
			throws DockerException, InterruptedException, Exception {
		try {
			Task.Criteria.Builder tcb = Task.Criteria.builder();
			if (taskCriteria != null) {
				tcb.serviceName(taskCriteria.getServiceName());
				tcb.nodeId(taskCriteria.getNodeId());
				tcb.taskId(taskCriteria.getTaskId());
				tcb.desiredState(taskCriteria.getDesiredState());
			}
			return client.listTasks(tcb.build());
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

	public static ServiceCreateResponse createConfig(DockerClient client, String name, String image, String configData,
			String configDataCharsetName, Map<String, String> labels)
			throws DockerException, InterruptedException, Exception {
		String dockerApiVersion = client.version().apiVersion();
		Service service = client.inspectService("");
//		client.updateService("", service.version().index(), service.spec());
		
		// 敏感文件
		String secretId = "secretid";
		final SecretFile secretFile = SecretFile.builder()
				// 目标文件可以路径
				.name("bsecret")
				// 用户ID
				.uid("1001")
				// 用户组ID
				.gid("1002")
				// 权限 类似于chmod 0640 bsecret
				.mode(0640L).build();
		final SecretBind secretBind = SecretBind.builder().file(secretFile).secretId(secretId).secretName("asecret")
				.build();

		// 配置文件
		String configId = "secretid";
		final ConfigFile configFile = ConfigFile.builder()
				// 目标文件可以路径
				.name("bconfig")
				// 用户ID
				.uid("1001")
				// 用户组ID
				.gid("1002")
				// 权限 类似于chmod 0640 bsecret
				.mode(0640L).build();
		final ConfigBind configBind = ConfigBind.builder().file(configFile).configId(configId).configName("aconfig")
				.build();

		// 发布端口
		final PortConfig.Builder portConfigBuilder = PortConfig.builder().name("web").protocol("tcp")
				.publishedPort(8080).targetPort(80);

		if (dockerApiVersionAtLeast(dockerApiVersion, "1.25")) {
			portConfigBuilder.publishMode(PortConfigPublishMode.INGRESS);
		}

		final PortConfig expectedPort1 = portConfigBuilder.build();
		EndpointSpec endpointSpec = EndpointSpec.builder().addPort(expectedPort1)
				.addPort(PortConfig.builder().targetPort(22).publishMode(PortConfigPublishMode.HOST).build()).build();

		// 监控检查
		final String[] healthcheckCmd = { "ping", "-c", "1", "127.0.0.1" };
		final long interval = DockerSwarmServiceClient.dockerApiVersionLessThan(dockerApiVersion, "1.30") ? 30L
				: 30000000L;
		final long timeout = DockerSwarmServiceClient.dockerApiVersionLessThan(dockerApiVersion, "1.30") ? 3L
				: 3000000L;
		final int retries = 3;
		final long startPeriod = DockerSwarmServiceClient.dockerApiVersionLessThan(dockerApiVersion, "1.30") ? 15L
				: 15000000L;
		Healthcheck healthcheck = Healthcheck.create(Arrays.asList(healthcheckCmd), interval, timeout, retries,
				startPeriod);

		// service = {"name":"serviceName","image":"imageName",
		// "mode":"replicas|global",
		// "replicas":5, "labels":{"key":"value"},
		// "resources":{"cpu":3,"mem":232323},
		// "network":[],"secrets":[], "configs":[],"healthcheck":{},
		// "placement":{}, "volumes":[], "logDriver":{},"restartPolicy":{}}

		final TaskSpec taskSpec = TaskSpec.builder()
				// 容器配置
				.containerSpec(ContainerSpec.builder()
						// 命令
						.command(new Vector<String>())
						// 参数
						.args(new Vector<String>())
						// 镜像
						.image(image)
						// 磁盘挂在 -v
						.mounts(
								// first v
								Mount.builder().target("").readOnly(false).source("dddd").type("volume")
										.volumeOptions(VolumeOptions.builder()
												.driverConfig(com.spotify.docker.client.messages.mount.Driver.builder()
														.build())
												.build())
										// .bindOptions(BindOptions.builder().build())
										// .tmpfsOptions(TmpfsOptions.builder().build())
										.build(),
								// second v
								Mount.builder()
										.volumeOptions(VolumeOptions.builder()
												.driverConfig(com.spotify.docker.client.messages.mount.Driver.builder()
														.build())
												.build())
										.bindOptions(BindOptions.builder().build())
										.tmpfsOptions(TmpfsOptions.builder().build()).build())
						.env(new Vector<String>())
						// 配置文件
						.configs(Arrays.asList(configBind))
						// 敏感文件
						.secrets(Arrays.asList(secretBind))
						// 监控检查
						.healthcheck(healthcheck)
						// 容器标签
						.labels(new HashMap<String, String>()).build())
				// 资源cpu,mem
				.resources(ResourceRequirements.builder().build())
				// 重启策略
				.restartPolicy(RestartPolicy.builder().build())
				// 部署位置约束
				.placement(Placement.create(null))
				// 网络?
				.networks(NetworkAttachmentConfig.builder().build())

				// 日志驱动
				.logDriver(Driver.builder().build()).build();

		// UpdateConfig.create(parallelism, delay, failureAction)
		String networkName = "";
		final ServiceSpec spec = ServiceSpec.builder().name(name)
				// 发布端口
				.endpointSpec(endpointSpec)
				// 任务模板
				.taskTemplate(taskSpec)
				// 发布模式 Replicas or Global
				.mode(ServiceMode.withReplicas(1L))
				// 增加标签
				.addLabel("label", "value")
				// 增加网络
				.networks(NetworkAttachmentConfig.builder().target(networkName).build())
				// 服务标签
				.labels(labels)
				// .updateConfig(updateConfig)
				.build();

		// 创建服务
		final ServiceCreateResponse response = client.createService(spec);
		return response;
	}
}
