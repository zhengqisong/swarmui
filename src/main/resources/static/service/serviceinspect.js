$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	pname = $.getContextLoadParam("name");
	$('#channel_servicenameId').html(pname);
	
	function drawServer(serviceInfo){
		//服务ID:serviceInfo.ID   
		//服务名称:serviceInfo.Spec.Name 
		//创建时间:serviceInfo.CreatedAt
		//更新时间:serviceInfo.UpdatedAt
		//服务标签:serviceInfo.Spec.Labels<map>
		//网络:serviceInfo.Networks['Target']
		//Mode:serviceInfo.Spec.Mode.Replicated.Replicas|Global
		
		//资源:serviceInfo.Spec.TaskTemplate.Resources.Limits.NanoCPUs,MemoryBytes
		//    serviceInfo.Spec.TaskTemplate.Resources.Reservations.NanoCPUs,MemoryBytes
		
		//重启策略:serviceInfo.Spec.TaskTemplate.RestartPolicy<map>
		//更新策略:serviceInfo.Spec.UpdateConfig<map>
		//部署位置标签:serviceInfo.Spec.TaskTemplate.Placement[String]
		
		//日志处理:serviceInfo.Spec.TaskTemplate.LogDriver<map>
		service_updateConfigPolicy
		var labels_html='';
		$.each(serviceInfo.Spec.Labels, function(key,value){
			if(labels_html!=''){
				labels_html +=",";
			}
			labels_html += key+"="+value;
		});
		var network_html='';
		$.each(serviceInfo.Spec.Networks, function(idx, network){
			if(network_html!=''){
				network_html +=",";
			}
			network_html+=network.Target;
		});
		var mode_html='';
		if(serviceInfo.Spec.Mode.Global==null){
			mode_html = 'replicas: '+serviceInfo.Spec.Mode.Replicated.Replicas+"实例";
		}else{
			mode_html = 'global';
		}
		var resource_html='';
		if(serviceInfo.Spec.TaskTemplate.Resources.Limits!=null){
			if(serviceInfo.Spec.TaskTemplate.Resources.Limits.NanoCPUs){
				resource_html+="cpu: "+serviceInfo.Spec.TaskTemplate.Resources.Limits.NanoCPUs/1000000000+"核<br>";
			}else{
				resource_html+="cpu: 不限<br>";
			}
			if(serviceInfo.Spec.TaskTemplate.Resources.Limits.MemoryBytes){
				resource_html+="mem: "+serviceInfo.Spec.TaskTemplate.Resources.Limits.MemoryBytes/1024/1024+"兆<br>";
			}else{
				resource_html+="mem: 不限<br>";
			}
		}
		if(serviceInfo.Spec.TaskTemplate.Resources.Reservations!=null){
			resource_html+="预留资源<br>";
			resource_html+="&nbsp;&nbsp;cpu: "+serviceInfo.Spec.TaskTemplate.Resources.Reservations.NanoCPUs+"<br>";
			resource_html+="&nbsp;&nbsp;mem: "+serviceInfo.Spec.TaskTemplate.Resources.Reservations.MemoryBytes+"<br>";
		}
		var placement_html='';
		if(serviceInfo.Spec.TaskTemplate.Placement!=null 
				&& serviceInfo.Spec.TaskTemplate.Placement.Constraints!=null){
			placement_html = serviceInfo.Spec.TaskTemplate.Placement.Constraints.join("<br>");
		}
		var restartPolicy_html='';		
		if(serviceInfo.Spec.TaskTemplate.RestartPolicy!=null){			
			//{Condition: "on-failure", Delay: 60000000000, MaxAttempts: 10, Window: null}
			restartPolicy_html +="条件: "+serviceInfo.Spec.TaskTemplate.RestartPolicy.Condition+"<br>";
			restartPolicy_html +="延迟: "+serviceInfo.Spec.TaskTemplate.RestartPolicy.Delay/1000000000+"秒<br>";
			restartPolicy_html +="最大尝试次数: "+serviceInfo.Spec.TaskTemplate.RestartPolicy.MaxAttempts;
		}
		var updateConfigPolicy_html='';
		if(serviceInfo.Spec.UpdateConfig){
			updateConfigPolicy_html += "失败时动作: "+serviceInfo.Spec.UpdateConfig.FailureAction+"<br>";
			updateConfigPolicy_html += "延迟更新: "+serviceInfo.Spec.UpdateConfig.Delay/1000000000+"秒<br>";
			updateConfigPolicy_html += "并发更新数: "+serviceInfo.Spec.UpdateConfig.Parallelism+"<br>";
			
		}
		var logDriver_html='';
		if(serviceInfo.Spec.TaskTemplate.LogDriver != null){	
			logDriver_html += "驱动: "+serviceInfo.Spec.TaskTemplate.LogDriver.Name+"<br>";
			$.each(serviceInfo.Spec.TaskTemplate.LogDriver.Options, function(key,value){
				logDriver_html += key+": "+value+"<br>";
			});
		}
		$('#servicePanel #service_id').html(serviceInfo.ID);
		$('#servicePanel #service_name').html(serviceInfo.Spec.Name);		
		$('#servicePanel #service_label').html(labels_html);
		$('#servicePanel #service_network').html(network_html);
		$('#servicePanel #service_mode').html(mode_html);
		$('#servicePanel #service_resource').html(resource_html);
		$('#servicePanel #service_placement').html(placement_html);
		$('#servicePanel #service_restartPolicy').html(restartPolicy_html);
		$('#servicePanel #service_updateConfigPolicy').html(updateConfigPolicy_html);
		$('#servicePanel #service_logDriver').html(logDriver_html);
		
		$('#servicePanel #service_createdAt').html(serviceInfo.CreatedAt);
		$('#servicePanel #service_updatedAt').html(serviceInfo.UpdatedAt);
		
		//容器信息.镜像:serviceInfo.Spec.TaskTemplate.ContainerSpec.Image
		//容器信息.标签:serviceInfo.Spec.TaskTemplate.ContainerSpec.Labels<map>
		//容器信息.命令:serviceInfo.Spec.TaskTemplate.ContainerSpec.Command
		//容器信息.参数:serviceInfo.Spec.TaskTemplate.ContainerSpec.Args[String]
		//容器信息.环境变量:serviceInfo.Spec.TaskTemplate.ContainerSpec.Env
		//容器信息.敏感数据:serviceInfo.Spec.TaskTemplate.ContainerSpec.Secrets
		//容器信息.配置文件:serviceInfo.Spec.TaskTemplate.ContainerSpec.Configs
		//容器信息.文件挂在:serviceInfo.Spec.TaskTemplate.ContainerSpec.Mounts
		//容器信息.健康检查:serviceInfo.Spec.TaskTemplate.ContainerSpec.Healthcheck
		
		var containerSpec = serviceInfo.Spec.TaskTemplate.ContainerSpec;
		var c_labels_html = "";
		if(containerSpec.Labels!=null){
			$.each(containerSpec.Labels, function(key,value){
				c_labels_html = key+"="+value;
			});
		}
		var c_command_html = "";
		if(containerSpec.Command!=null){
			c_command_html = containerSpec.Command.join(" ");
		}
		var c_args_html = "";
		if(containerSpec.Args!=null){
			c_args_html = containerSpec.Args.join(" ");
		}
		var c_env_html = "";
		if(containerSpec.Env!=null){
			c_env_html = containerSpec.Env.join("<br>");
		}
		var c_secrets_html = "";
		if(containerSpec.Secrets!=null){
			$.each(containerSpec.Secrets, function(cidx, c_secret){
				if(c_secrets_html!=''){
					c_secrets_html+="<br>";
				}
				c_secrets_html += "ID: "+c_secret.SecretID+"<br>";
				if(c_secret.File!=null){
					c_secrets_html += "文件名称: "+c_secret.File.Name+"<br>";
					c_secrets_html += "GID: "+c_secret.File.GID+"<br>";
					c_secrets_html += "UID: "+c_secret.File.UID+"<br>";
					c_secrets_html += "Mode: "+c_secret.File.Mode+"<br>";
				}
				
			});
		}		
		var c_configs_html="";
		if(containerSpec.Configs!=null){
			$.each(containerSpec.Configs, function(cidx, c_config){
				if(c_configs_html!=''){
					c_configs_html+="<br>";
				}
				c_configs_html += "ID: "+c_config.ConfigID+"<br>";
				if(c_config.File!=null){
					c_configs_html += "文件名称: "+c_config.File.Name+"<br>";
					c_configs_html += "GID: "+c_config.File.GID+"<br>";
					c_configs_html += "UID: "+c_config.File.UID+"<br>";
					c_configs_html += "Mode: "+c_config.File.Mode+"<br>";
				}
				
			});
		}
		var c_mounts_html="";
		
		if(containerSpec.Mounts!=null){
			$.each(containerSpec.Mounts, function(idx, c_mount){
				c_mounts_html += "源目录: "+c_mount.Source+"<br>";
				c_mounts_html += "挂载目录: "+c_mount.Target+"<br>";
				c_mounts_html += "挂载目录: "+c_mount.Target+"<br>";
				c_mounts_html += "是否只读: "+(true==c_mount.ReadOnly?"是":"否")+"<br>";
				c_mounts_html +="<br>";
			});
		}
		var c_healthcheck_html="";
		if(containerSpec.Healthcheck!=null){
			if(containerSpec.Healthcheck.Test!=null){
				c_healthcheck_html +="Test: "+containerSpec.Healthcheck.Test.join(" ")+"<br>";
			}
			c_healthcheck_html +="周期: "+containerSpec.Healthcheck.Interval+"<br>";
			c_healthcheck_html +="重试次数: "+containerSpec.Healthcheck.Retries+"<br>";
			c_healthcheck_html +="超时: "+containerSpec.Healthcheck.Timeout+"<br>";
			c_healthcheck_html +="启动时间: "+containerSpec.Healthcheck.StartPeriod+"<br>";
		}
		$('#containerPanel #container_image').html(containerSpec.Image);
		$('#containerPanel #container_labels').html(c_labels_html);
		$('#containerPanel #container_command').html(c_command_html);
		$('#containerPanel #container_args').html(c_args_html);
		$('#containerPanel #container_env').html(c_env_html);
		$('#containerPanel #container_secrets').html(c_secrets_html);
		$('#containerPanel #container_configs').html(c_configs_html);
		$('#containerPanel #container_mounts').html(c_mounts_html);
		$('#containerPanel #container_healthcheck').html(c_healthcheck_html);
		
	}
	
	function drawPort(ports){
		items = [];
		if(ports==null){
			ports=[];
		}
		$.each(ports, function(idx, port){
			//"Name","Protocol","TargetPort","PublishedPort","PublishMode"
			items.push({"name":port.Name,"protocol":port.Protocol,
				"targetPort":port.TargetPort,"publishedPort":port.PublishedPort,
				"publishMode":port.PublishMode});
		});
		data = {"status":0,"message":{"items":items}};
		$.loadTableData({
			"tableId":"portslistTab",
			"status":0,
			"statusName":"status",
			"beforeListFunc":function(data, flag){
			},
			data:data,
			"column":[
			          {"name":"name", class:"table-title am-hide-sm-only",
			        	  "format":function(name,row){
			        	  if(name==null)
			        		  name="";
			        	  return name;
			          }},
			          {"name":"protocol"},
			          {"name":"targetPort"},
			          {"name":"publishedPort"},
			          {"name":"publishMode"}
			          ]
		});
	}
	function showTimeLengths(theTime){
		return theTime;
	}
	$.extend({ 
		showCloseHistoryTask:function(slot){	 
			  //$(this).removeClass('am-icon-chevron-right');
			  //$(this).addClass('am-icon-chevron-down');
			if($('.chevron_'+slot).hasClass('am-icon-chevron-right')){
//			if($('.slot_'+slot).css('display') == "none"){
				$('.slot_'+slot).css('display','');
				$('.chevron_'+slot).removeClass('am-icon-chevron-right');
				$('.chevron_'+slot).addClass('am-icon-chevron-down');
			}else{
				$('.slot_'+slot).css('display','none');
				$('.chevron_'+slot).removeClass('am-icon-chevron-down');
				$('.chevron_'+slot).addClass('am-icon-chevron-right');
			}
		}
	});
	
	function drawTaskList(taskList){
		//id=ID,name=pname+'.'+Slot,image=Spec.ContainerSpec.Image,nodeid=NodeID,
		//desiredState=DesiredState,currentStatus=Status.State + Status.Timestamp,
		//error=Status.Err==null?"":Status.Err+Status.ContainerStatus.ExitCode
		//network=[NetworksAttachments:[{Addresses:[]]]
		var taskItems = [];
		//var lastName = "";
		//var lastTaskid = "";
		$.each(taskList, function(idx, task){
			taskInfo = {};
			taskInfo['version'] = task.Version.Index;
			taskInfo['id'] = task.ID;
			taskInfo['slot'] = task.Slot;
			taskInfo['name'] = pname+"."+task.Slot;
			taskInfo['image'] = task.Spec.ContainerSpec.Image;
			taskInfo['nodeid'] = task.NodeID==null?"":task.NodeID;
			taskInfo['desiredState'] = task.DesiredState;
			taskInfo['currentStatus'] = task.Status.State+" "+showTimeLengths(task.Status.Timestamp);
			taskInfo['CreatedAt'] = task.CreatedAt;
			
			if(task.Status.Err!=null){
				taskInfo['error'] = task.Status.State+" "+task.Status.Err;
			}else{
				taskInfo['error'] = '';
			}
			taskInfo['network'] = '';
			$.each(task.NetworksAttachments, function(nidx, network){
				if(network.Addresses){
					if(taskInfo['network'] !=""){
						taskInfo['network'] = ' ';
					}
					taskInfo['network'] = taskInfo['network'] + network.Addresses.join(",");
				}
			});
			var idx_item = 0;
			var insert_flag = false;
			var find_flag = false;
			for(idx_item = 0; idx_item < taskItems.length; idx_item++){
				if(taskInfo.slot == taskItems[idx_item].slot){
					find_flag = true;
				}
				if((taskInfo.slot == taskItems[idx_item].slot
							&& taskInfo.CreatedAt > taskItems[idx_item].CreatedAt)
						){
					taskItems.splice(idx_item,0,taskInfo);
					insert_flag = true;
					break;
				}else if(find_flag == true && taskInfo.slot != taskItems[idx_item].slot){
					taskItems.splice(idx_item,0,taskInfo);
					insert_flag = true;
					break;
				}
			}
			if(!insert_flag){
				taskItems.push(taskInfo);
			}
				
		});
		
		var taskSlot = -1;
		taskData = {"status":0,"message":{"items":taskItems}};
		$.loadTableData({
			"tableId":"taskslistTab",
			"status":0,
			"statusName":"status",
			"beforeListFunc":function(taskData, flag){
			},
			"row":{"class":function(row){
				if(taskSlot != -1 && taskSlot == row.slot){
					return "am-primary slot_"+row.slot;
				}
				return "";
				},"style":function(row){
					if(taskSlot != -1 && taskSlot == row.slot){
						return "display:none";
					}
					return "";
				}
			},
			data:taskData,
			"column":[
			          {"name":"id", class:"table-title am-hide-sm-only"},
			          {"name":"name","format":function(name,row){
			        	  if(taskSlot == row.slot){
			        		  name = "&nbsp;&nbsp;\\_"+name; 
			        	  }else{
			        		  name = '<span style="cursor:pointer" class="am-icon-chevron-right chevron_'+row.slot+'" onClick="$.showCloseHistoryTask('+row.slot+')">'+name+'</span>';
			        	  }
			        	  taskSlot = row.slot;			        	  
			        	  return name;
			          }},
			          {"name":"image", class:"table-title am-hide-sm-only"},
			          {"name":"nodeid", class:"table-title am-hide-sm-only"},
			          {"name":"desiredState"},
			          {"name":"currentStatus", class:"table-title am-hide-sm-only"},
			          {"name":"network", class:"table-title am-hide-sm-only"},
			          {"name":"error", class:"table-title am-hide-sm-only"}
			          ]
		});
	}
	function inspect(pname){
		$('#serviceForm [name=name]').val("加载中...");
		var aj = $.ajax({    
			url:'/api/service/'+currentCluster.clusterid+'/inspect/'+pname,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    //async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		//console.log(data);
		    		//console.log(JSON.stringify(data));
		    		drawPort(data.message.Endpoint.Ports);
		    		drawServer(data.message);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	function psTask(pname){
		var aj = $.ajax({    
			url:'/api/service/'+currentCluster.clusterid+'/pstask/'+pname,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    //async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		//console.log(data);
		    		//console.log(JSON.stringify(data));
		    		drawTaskList(data.message);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	inspect(pname);
	psTask(pname);
	
	$('#serviceForm').submit(function(e){
		e.preventDefault();
	});
	$('#backupId').click(function(){
		$.contextLoad("/service/servicelist.html");
	});
	$('#reloadId').click(function(){
		inspect(pname);
		psTask(pname);
	});
	
		
});