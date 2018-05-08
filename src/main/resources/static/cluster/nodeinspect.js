
$(function() {
	pclusterid = $.getContextLoadParam("clusterid");
	pclustername = $.getContextLoadParam("clustername");
	pnodeid = $.getContextLoadParam("nodeid");
	pnodename = $.getContextLoadParam("nodename");
	
	var navigation_html = '<strong class="am-text-primary am-text-lg">系统管理</strong>'
		+' / <small><a href="javascript:$.contextLoad(\'/cluster/clusterlist.html\')">集群管理</a></small>'
		+' / <small><a href="javascript:$.contextLoad(\'/cluster/nodelist.html?clusterid='+pclusterid+'&clustername='+pclustername+'\')">节点管理('+pclustername+')</span></a></small>'
		+' / <small>节点详情</small> ';
	$('#navigationId').html(navigation_html);
	
	
	$.extend({ 
		deleteLabel:function(key){
			data = [key];
			
	    	var aj = $.ajax({    
	    		url:'/api/node/'+pclusterid+'/rmlabel/'+pnodeid,// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    data:JSON.stringify(data),
			    dataType:'json',
			    async: true,
			    success:function(data) {
			    	if(data.status != 0){
			    		$.showMessage("warning",data.error);
			    	}else{
			    		$.showMessage("success","成功删除accesskey。");
			    		loadNodeInfo();
			    	}
			    	return false;
			    },
			    error : function() {
			    	$.showMessage("warning","连接服务器失败。");
			    }
	  	  	});
	    }
	});
	function drawInfo(pNodeInfo){
		$('#node_State').html(pNodeInfo.Status.State);
		$('#node_Role').html(pNodeInfo.Spec.Role);
		if(pNodeInfo.ManagerStatus!=null){
			$('#node_Reachability').html(pNodeInfo.ManagerStatus.Reachability);
		}
		$('#node_Availability').html(pNodeInfo.Spec.Availability);
		$('#node_NanoCPUs').html(pNodeInfo.Description.Resources.NanoCPUs
				+'('
				+pNodeInfo.Description.Resources.NanoCPUs/1000000000
				+'核)');
		$('#node_MemoryBytes').html(pNodeInfo.Description.Resources.MemoryBytes
				+'('
				+ (pNodeInfo.Description.Resources.MemoryBytes/1024/1024/1024).toFixed(2)
				+'G)');
		
		$('#node_ID').html(pNodeInfo.ID);
		$('#node_Hostname').html(pNodeInfo.Description.Hostname);
		$('#node_Addr').html(pNodeInfo.Status.Addr);
		$('#node_EngineVersion').html(pNodeInfo.Description.Engine.EngineVersion);
		$('#node_Platform').html(pNodeInfo.Description.Platform.Architecture+" "+pNodeInfo.Description.Platform.OS);

		$('#node_CreatedAt').html(pNodeInfo.CreatedAt);
		$('#node_UpdatedAt').html(pNodeInfo.UpdatedAt);
		
	}
	
	function drawLabel(pNodeInfo){
		items = [];
		$.each(pNodeInfo.Spec.Labels, function(key, value){
			items.push({"name":key,"value":value,"type":"del"});
		});
		//items.push({"name":'<input type="text" name="label_key" size="2">',"value":'<input type="text" name="label_value">',"type":"add"});
		data = {"status":0,"message":{"items":items}};
		$.loadTableData({
			"tableId":"labelTab",
			"status":0,
			"statusName":"status",
			"beforeListFunc":function(data, flag){
			},
			data:data,
			"column":[
			          {"name":"name","format":function(name, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+name;
			          }},
			          {"name":"value"},
			          {"name":"name","buttons":function(name, row){
			        	  return [
							{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回，您确认要删除？\',this, \'$.deleteLabel(\\\''+name+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
							];
				        }}
			          
			          ]
		});
	}
	function drawPlugin(pNodeInfo){
		data = {"status":0,"message":{"items":pNodeInfo.Description.Engine.Plugins}};
		$.loadTableData({
			"tableId":"pluginTab",
			"status":0,
			"statusName":"status",
			"beforeListFunc":function(data, flag){
			},
			data:data,
			"column":[
			          {"name":"Type"},
			          {"name":"Name"}
			          ]
		});
	}
	
	function loadNodeInfo(){
		var aj = $.ajax({ 
			url:'/api/node/'+pclusterid+'/inspect/'+pnodeid,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		pNodeInfo = data.message;
		    		//$('#nodeInfo').html(jsonSyntaxHighlight(JSON.stringify(pNodeInfo, undefined, 4)));
		    		drawInfo(pNodeInfo);
		    		drawLabel(pNodeInfo);
		    		drawPlugin(pNodeInfo);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	loadNodeInfo();
	
	$('#nodeLabeleForm').click(function(e){
		e.preventDefault();
		var jsonObj = $("#nodeLabeleForm").serializeJson();
		data = {};
		if(jsonObj.key==""){
			return;
		}
		if(jsonObj.value==""){
			return;
		}
		data[jsonObj.key] = jsonObj.value;
		var aj = $.ajax({ 
			url:'/api/node/'+pclusterid+'/addlabel/'+pnodeid,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    data:JSON.stringify(data),
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		$.showMessage("success","添加标签成功。");
		    		loadNodeInfo();
		    		$("#nodeLabeleForm [name=key]").val('');
		    		$("#nodeLabeleForm [name=value]").val('');
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	});
	
	$('#backupId').click(function(){
		$.contextLoad('/cluster/nodelist.html?clusterid='+pclusterid+'&clustername='+pclustername);
	});
});
  