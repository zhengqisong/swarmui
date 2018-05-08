$(function() {
	
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	//----------------------------
	volumes = [];
	configs = [];
	secrets = [];
	
	function addDRrawVolume(){
		var mountPanel = '<div class="am-u-sm-12 am-u-md-12">'
			            +'<div class="am-form-group">';
		mountPanel += '<select data-am-selected="{btnSize: \'sm\'}" name="mount">';
		mountPanel +='<option value="">不挂载</option>';
		$.each(volumes, function(idx, volumeInfo){
			mountPanel +='<option value="'+volumeInfo.code+'">'+volumeInfo.name+'</option>';
		});
		mountPanel +='</select>'
				   +'</div>'
				   +'<div class="am-form-group">'
				   +'<input type="text" class="am-form-field am-radius" name="path" placeholder="存储内目录或文件,如:/" />'
				   +'</div>'
				   +'<div class="am-form-group">'
				   +'<input type="text" class="am-form-field am-radius" name="target" placeholder="挂载容器内目录" />'
				   +'</div>'
				   +'</div>';
		$("#mountPanel").append(mountPanel);
	}
	function delDRrawVolume(){
		var delflag=false;
		$.each($('#mountPanel').children(), function(idx, mountOptionInfo){
			mountOptionInfo = $(mountOptionInfo);
			if(mountOptionInfo.find("select[name=mount]").val()==''){
				mountOptionInfo.remove();
				delflag = true;
			}
		});
		if(delflag==false){
			$('#mountPanel').children().eq(-1).remove();
		}
		if($('#mountPanel').children().length < 1){
			//addDRrawVolume();
		}
	}
	
	function addDRrawConfig(){
		var configPanel = '<div class="am-u-sm-12 am-u-md-12">'
			            +'<div class="am-form-group">';
		configPanel += '<select data-am-selected="{btnSize: \'sm\'}" name="config">';
		configPanel +='<option value="">不挂载</option>';
		$.each(configs, function(idx, configInfo){
			configPanel +='<option value="'+configInfo.code+'">'+configInfo.name+'</option>';
		});
		configPanel +='</select>'
				   +'</div>'
				   +'<div class="am-form-group">'
				   +'<input type="text" class="am-form-field am-radius" name="target" placeholder="挂载容器内目录" />'
				   +'</div>'
				   +'</div>';
		$("#configPanel").append(configPanel);
	}
	function delDRrawConfig(){
		var delflag=false;
		$.each($('#configPanel').children(), function(idx, configOptionInfo){
			configOptionInfo = $(configOptionInfo);
			if(configOptionInfo.find("select[name=config]").val()==''){
				configOptionInfo.remove();
				delflag = true;
			}
		});
		if(delflag==false){
			$('#configPanel').children().eq(-1).remove();
		}
		if($('#configPanel').children().length < 1){
			//addDRrawConfig();
		}
	}
	
	function addDRrawSecret(){
		var secretPanel = '<div class="am-u-sm-12 am-u-md-12">'
			            +'<div class="am-form-group">';
		secretPanel += '<select data-am-selected="{btnSize: \'sm\'}" name="secret">';
		secretPanel +='<option value="">不挂载</option>';
		$.each(secrets, function(idx, secretInfo){
			secretPanel +='<option value="'+secretInfo.code+'">'+secretInfo.name+'</option>';
		});
		secretPanel +='</select>'
				   +'</div>'
				   +'<div class="am-form-group">'
				   +'<input type="text" class="am-form-field am-radius" name="target" placeholder="挂载容器内目录" />'
				   +'</div>'
				   +'</div>';
		$("#secretPanel").append(secretPanel);
	}
	function delDRrawSecret(){
		var delflag=false;
		$.each($('#secretPanel').children(), function(idx, secretOptionInfo){
			secretOptionInfo = $(secretOptionInfo);
			if(secretOptionInfo.find("select[name=secret]").val()==''){
				secretOptionInfo.remove();
				delflag = true;
			}
		});
		if(delflag==false){
			$('#secretPanel').children().eq(-1).remove();
		}
		if($('#secretPanel').children().length < 1){
			//addDRrawSecret();
		}
	}
	function addDRrawEnv(){
		var envPanel ='<div class="am-u-sm-12 am-u-md-12">'
            	+'<div class="am-form-group">'
			    +'<input type="text" class="am-form-field am-radius" placeholder="如：envkey=envvalue" name="env"/>'
			    +'</div>'
			    +'</div>';
		$("#envPanel").append(envPanel);
	}
	function delDRrawEnv(){
		var delflag=false;
		$.each($('#envPanel').children(), function(idx, envOptionInfo){
			envOptionInfo = $(envOptionInfo);
			if(envOptionInfo.find("input[name=env]").val()==''){
				envOptionInfo.remove();
				delflag = true;
			}
		});
		if(delflag==false){
			$('#envPanel').children().eq(-1).remove();
		}
		if($('#envPanel').children().length < 1){
			//addDRrawEnv();
		}
	}
	function addDRrawArg(){
		var argPanel ='<div class="am-u-sm-12 am-u-md-12">'
            	+'<div class="am-form-group">'
			    +'<input type="text" class="am-form-field am-radius" name="arg" placeholder="容器启动参数，每个参数一条" />'
			    +'</div>'
			    +'</div>';
		$("#argPanel").append(argPanel);
	}
	function delDRrawArg(){
		var delflag=false;
		$.each($('#argPanel').children(), function(idx, argOptionInfo){
			argOptionInfo = $(argOptionInfo);
			if(argOptionInfo.find("input[name=arg]").val()==''){
				argOptionInfo.remove();
				delflag = true;
			}
		});
		if(delflag==false){
			$('#argPanel').children().eq(-1).remove();
		}
		if($('#argPanel').children().length < 1){
			//addDRrawEnv();
		}
	}
	function addDRrawLabel(){
		var labelPanel ='<div class="am-u-sm-12 am-u-md-12">'
					   +'       <div class="am-form-group">'
					   +'     <select data-am-selected="{btnSize: \'sm\'}" name="label">'
					   +'            <option value="">不配置</option>'
					   +'            <option value="service">服务标签</option>'
					   +'            <option value="contailer">容器标签</option>'
					   +'    </select>'
					   +' </div>'
					   +' <div class="am-form-group">'
					   +'    <input type="text" class="am-form-field am-radius" placeholder="标签key" name="labelkey"/>'
					   +' </div>'
					   +' <div class="am-form-group">'
					   +'    <input type="text" class="am-form-field am-radius" placeholder="标签值" name="labelvalue"/>'
					   +' </div>'
					   +'</div>';
		$("#labelPanel").append(labelPanel);
	}
	function delDRrawLabel(){
		var delflag=false;
		$.each($('#labelPanel').children(), function(idx, envOptionInfo){
			envOptionInfo = $(envOptionInfo);
			if(envOptionInfo.find("select[name=label]").val()==''){
				envOptionInfo.remove();
				delflag = true;
			}
		});
		if(delflag==false){
			$('#labelPanel').children().eq(-1).remove();
		}
		if($('#labelPanel').children().length < 1){
			//addDRrawLabel();
		}
	}
	function addDRrawLog(){
		if($('#logPanel').children().length < 1){
			var logPanel ='<div class="am-u-sm-12 am-u-md-12">'
						   +'       <div class="am-form-group">'
						   +'     <select data-am-selected="{btnSize: \'sm\'}" name="drivername">'
						   +'      <option value="syslog">syslog</option>'
						   +'     </select>'
						   +' </div>'
						   +' <div class="am-form-group">'
						   +'    <input type="text" class="am-form-field am-radius" placeholder="发送地址,如:udp://ip:port" name="address"/>'
						   +' </div>'
						   +' <div class="am-form-group">'
						   +'    <input type="text" class="am-form-field am-radius" placeholder="标签值" name="tag"/>'
						   +' </div>'
						   +'</div>';
			$("#logPanel").append(logPanel);
		}
	}
	function delDRrawLog(){
		$('#logPanel').children().remove();
	}
	function addDRrawRestart(){
		if($('#restartPanel').children().length < 1){
			var restartPanel ='<div class="am-u-sm-12 am-u-md-12">'
						   +'       <div class="am-form-group">'
						   +'     <select data-am-selected="{btnSize: \'sm\'}" name="condition">'
						   +'      <option value="on-failure">on-failure</option>'
						   +'      <option value="any">any</option>'
						   +'     </select>'
						   +' </div>'
						   +' <div class="am-form-group">'
						   +'    <input type="number" class="am-form-field am-radius" placeholder="延迟秒" name="delay" min="30"/>'
						   +' </div>'
						   +' <div class="am-form-group">'
						   +'    <input type="number" class="am-form-field am-radius" placeholder="最大重启次数" name="maxAttempts" min="3"/>'
						   +' </div>'
						   +'</div>';
			$("#restartPanel").append(restartPanel);
		}
	}
	function delDRrawRestart(){
		$('#restartPanel').children().remove();
	}
	function registerOptions(){
		var aj = $.ajax({ 
			url:'/api/register/'+currentCluster.clusterid+'/list',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		var registerPanel = '<select data-am-selected="{btnSize: \'sm\'}" name="register">';
		    		$.each(data.message, function(idx, registerInfo){
		    			registerPanel +='<option value="'+registerInfo.address+'">'+registerInfo.name+'</option>';
		    		});
		    		registerPanel +='</select>';
		    		$("#registerPanel").html(registerPanel);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}	
	function placementOptions(){
		var aj = $.ajax({ 
			url:'/api/cluster/label/'+currentCluster.clusterid+'/list',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		var placementPanel = '<select data-am-selected="{btnSize: \'sm\'}" name="placement">';
		    		$.each(data.message, function(idx, labelInfo){
		    			placementPanel +='<option value="'+labelInfo.labelvalue+'" key="node.labels.'+labelInfo.labelkey+'">'+labelInfo.labelname+'</option>';
		    		});
		    		placementPanel +='</select>';
		    		$("#placementPanel").html(placementPanel);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	function networkOptions(){
		var aj = $.ajax({ 
			url:'/api/network/'+currentCluster.clusterid+'/listself',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		var networkPanel = '';
		    		$.each(data.message, function(idx, networkInfo){
		    			networkPanel +='<label class="am-checkbox-inline">'
		    						+'<input type="checkbox" value="'+networkInfo.code+'" name="network"> '+networkInfo.name+' ';
		    						+'</label>\n';
		    		});
		    		$("#networkPanel").html(networkPanel);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	function mountOptions(){
		var aj = $.ajax({ 
			url:'/api/storagevolume/'+currentCluster.clusterid+'/listself',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		volumes = data.message;
		    		$('#mountPanel').html('');
		    		//addDRrawVolume();
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	function configOptions(){
		var aj = $.ajax({ 
			url:'/api/config/'+currentCluster.clusterid+'/listself',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		configs = data.message;
		    		
		    		$('#configPanel').html('');
		    		//addDRrawConfig();
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	function secretOptions(){
		var aj = $.ajax({ 
			url:'/api/secret/'+currentCluster.clusterid+'/listself',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		secrets = data.message;
		    		
		    		$('#secretPanel').html('');
		    		//addDRrawSecret();
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	registerOptions();
	placementOptions();
	networkOptions();
	mountOptions();
	configOptions();
	secretOptions();
	
	$('#mountAddBtn').click(function(){
		addDRrawVolume();
	});
	$('#mountDelBtn').click(function(){
		delDRrawVolume();
	});
	$('#configAddBtn').click(function(){
		addDRrawConfig();
	});
	$('#configDelBtn').click(function(){
		delDRrawConfig();
	});
	$('#secretAddBtn').click(function(){
		addDRrawSecret();
	});
	$('#secretDelBtn').click(function(){
		delDRrawSecret();
	});
	$('#argAddBtn').click(function(){
		addDRrawArg();
	});
	$('#argDelBtn').click(function(){
		delDRrawArg();
	});
	$('#envAddBtn').click(function(){
		addDRrawEnv();
	});
	$('#envDelBtn').click(function(){
		delDRrawEnv();
	});
	$('#labelAddBtn').click(function(){
		addDRrawLabel();
	});
	$('#labelDelBtn').click(function(){
		delDRrawLabel();
	});
	$('#logAddBtn').click(function(){
		addDRrawLog();
	});
	$('#logDelBtn').click(function(){
		delDRrawLog();
	});
	$('#restartAddBtn').click(function(){
		addDRrawRestart();
	});
	$('#restartDelBtn').click(function(){
		delDRrawRestart();
	});
	//----------------------------
	
	function submitService(serviceJson){
		var aj = $.ajax({    
			url:'/api/service/'+currentCluster.clusterid+'/create',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    data:JSON.stringify(serviceJson),
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		$.showMessage("success","成功添加服务。");
		    		$.contextLoad("/service/servicelist.html");
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	$('#serviceForm').submit(function(e){
		e.preventDefault();
		var serviceJson = null;
		try{
			serviceJson = JSON.parse($("#serviceForm [name=serviceJson]").val());
		} catch(error) {
			$.showMessage("warning","json格式错误，无法解析。");
			return;
		}
		submitService(serviceJson);
	});
	$('#serviceForm2').submit(function(e){
		e.preventDefault();
		var service_name = $("#serviceForm2 [name=name]").val();
		var service_register = $("#serviceForm2 [name=register]").val();
		var service_image = $("#serviceForm2 [name=image]").val();
		var service_mode = $("#serviceForm2 [name=mode]:checked").val();
		var service_replicas = $("#serviceForm2 [name=replicas]").val();
		var service_placement_key =$("#serviceForm2 [name=placement]").find("option:selected").attr("key");
		var service_placement_value =$("#serviceForm2 [name=placement]").find("option:selected").val();
		var service_placement = [service_placement_key+"=="+service_placement_value];
		var service_network = [];
		$.each($("#serviceForm2 [name=network]:checked"),function(idx,networkInfo){
			service_network.push($(networkInfo).val());
		});
		var service_cpu = $("#serviceForm2 [name=cpu]").val();
		var service_memory = $("#serviceForm2 [name=memory]").val();
		var service_port = $("#serviceForm2 [name=port]").val();
		var service_protocol = $("#serviceForm2 [name=protocol]:checked").val();
		var endpointSpec = [];
		if(service_port!=''){
			endpointSpec.push({"name":service_port,"mode":"ingress","targetPort":service_port,"protocol":service_protocol});
		}
		//高级
		var service_args = [];
		$.each($('#argPanel').children(), function(idx, argOptionInfo){
			argOptionInfo = $(argOptionInfo);
			var tmp_arg = argOptionInfo.find("input[name=arg]").val();
			if(tmp_arg!=''){
				service_args.push(tmp_arg);
			}
		});
		var service_env=[];
		$.each($('#envPanel').children(), function(idx, envOptionInfo){
			envOptionInfo = $(envOptionInfo);
			var tmp_env = envOptionInfo.find("input[name=env]").val();
			if(tmp_env!=''){
				service_env.push(tmp_env);
			}
		});
		var service_mounts=[];
		$.each($('#mountPanel').children(), function(idx, mountOptionInfo){
			mountOptionInfo = $(mountOptionInfo);
			var tmp_mount = mountOptionInfo.find("select[name=mount]").val();
			if(tmp_mount != ''){
				var tmp_path = mountOptionInfo.find("input[name=path]").val();
				var tmp_target = mountOptionInfo.find("input[name=target]").val();
				service_mounts.push({"code":tmp_mount,"target":tmp_target,"readOnly":"false","path":tmp_path});
			}
		});
		
		var service_configs=[];
		$.each($('#configPanel').children(), function(idx, configOptionInfo){
			configOptionInfo = $(configOptionInfo);
			tmp_config = configOptionInfo.find("select[name=config]").val();
			if(tmp_config != ''){
				var tmp_target = configOptionInfo.find("input[name=target]").val();
				service_configs.push({"code":tmp_config,"file":{"name":tmp_target,"uid":"1","gid":"1","mode":640}});
			}
		});
		
		var service_secrets=[];
		$.each($('#secretPanel').children(), function(idx, secretOptionInfo){
			secretOptionInfo = $(secretOptionInfo);
			tmp_secret = secretOptionInfo.find("select[name=secret]").val();
			if(tmp_secret != ''){
				var tmp_target = secretOptionInfo.find("input[name=target]").val();
				service_secrets.push({"code":tmp_secret,"file":{"name":tmp_target,"uid":"1","gid":"1","mode":640}});
			}
		});
		
		var service_labels={};
		var service_container_labels={};
		$.each($('#labelPanel').children(), function(idx, envOptionInfo){
			envOptionInfo = $(envOptionInfo);
			tmp_label = envOptionInfo.find("select[name=label]").val();
			tmp_labelkey = envOptionInfo.find("input[name=labelkey]").val();
			tmp_labelvalue = envOptionInfo.find("input[name=labelvalue]").val();
			if(tmp_label=='service'){
				service_labels[tmp_labelkey]=tmp_labelvalue;
			}else if(tmp_label=='contailer'){
				service_container_labels[tmp_labelkey]=tmp_labelvalue;
			}
		});
		
		var service_logDriver=null;
		if($('#logPanel').children().length > 0){
			tmp_log_name = $('#logPanel').find("select[name=drivername]").val();
			tmp_log_address = $('#logPanel').find("input[name=address]").val();
			tmp_log_tag = $('#logPanel').find("input[name=tag]").val();
			tmp_log_key=tmp_log_name+"-address";
			
			service_logDriver = {"name":tmp_log_name,"options":{tmp_log_key:tmp_log_address,"tag":tmp_log_tag}};
			service_logDriver = {"name":tmp_log_name};
			var tmp_options = {"tag":tmp_log_tag}
			tmp_options[tmp_log_key] = tmp_log_address;
			service_logDriver["options"] = tmp_options;
		}
		
		var service_restartPolicy=null;
		if($('#restartPanel').children().length > 0){
			tmp_restart_condition = $('#restartPanel').find("select[name=condition]").val();
			tmp_restart_delay = $('#restartPanel').find("input[name=delay]").val();
			tmp_restart_maxAttempts = $('#restartPanel').find("input[name=maxAttempts]").val();
			
			service_restartPolicy = {"condition":tmp_restart_condition,"delay":tmp_restart_delay,"maxAttempts":tmp_restart_maxAttempts};
		}
		
		var service_json = {};
		service_json['name'] = service_name;
		service_json['mode'] = service_mode;
		if(service_mode=='replicas'){
			service_json['replicas'] = service_replicas;
		}
		service_json['labels'] = service_labels;
		service_json['endpointSpec'] = endpointSpec;
		//template
		var template = {};
		template["resources"]={"limit":{"cpu":service_cpu,"memory":service_memory}}
		if(service_restartPolicy!=null){
			template["restartPolicy"]=service_restartPolicy;
		}
		template["placement"] = service_placement;
		template["network"] = service_network;
		if(service_logDriver!=null){
			template["logDriver"] = service_logDriver;
		}
		var container={};
		container["image"] = service_register+"/"+service_image
		if(service_args.length > 0){
			container["args"] = service_args;
		}
		container["configs"] = service_configs;
		container["secrets"] = service_secrets;
		
		container["env"] = service_env;
		container["labels"] = service_container_labels;
		container["mounts"] = service_mounts;
		template["container"] = container;
		service_json["template"] = template;
		
		//console.log(JSON.stringify(service_json));
		submitService(service_json);
	});
	$('#backupId').click(function(){
		$.contextLoad("/service/servicelist.html");
	});
	$('button[name=new_service_cancel]').click(function(){
		$.contextLoad("/service/servicelist.html");
	});	
	
	$('#showhidenMoreButton_id').click(function(){
		var display = $('#showhidenMorePanel_id').css('display');
		if(display=='none'){
			$('#showhidenMorePanel_id').css('display',''); 
		}else{
			$('#showhidenMorePanel_id').css('display','none'); 
		}
		
	});
	
});