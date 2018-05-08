$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	
	function resourceCount(){
		$('#configForm [name=name]').val("加载中...");
		var aj = $.ajax({    
			url:'/api/dashboard/'+currentCluster.clusterid+'/resource/count',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		$('#resourceNetwork').html(data.message.network);
		    		$('#resourceVolume').html(data.message.volume.count+", "+(data.message.volume.storagesize==null?"0":data.message.volume.storagesize)+'G');
		    		$('#resourceConfig').html(data.message.config);
		    		$('#resourceSecret').html(data.message.secret);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	
	function nodeCount(){
		$('#configForm [name=name]').val("加载中...");
		var aj = $.ajax({    
			url:'/api/dashboard/'+currentCluster.clusterid+'/node/count',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    success:function(data) {
		    	if(data.status != 0){
		    		$('#nodeNumId').html(data.error);
		    	}else{
		    		$('#nodeNumId').html(data.message.nodeNum);
		    		$('#managerNumId').html(data.message.managerNum);
		    		$('#statusOkNumId').html(data.message.statusOkNum);
		    		$('#statusErrorNumId').html(data.message.statusErrorNum);
		    		$('#cpuNumId').html((data.message.useCpuNum)+"/"+(data.message.cpuNum));
		    		$('#memNumId').html(((data.message.useMemNum/1024.0).toFixed(1))+"/"+((data.message.memNum).toFixed(1)));
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	function serviceCount(){
		$('#configForm [name=name]').val("加载中...");
		var aj = $.ajax({    
			url:'/api/dashboard/'+currentCluster.clusterid+'/service/count',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    success:function(data) {
		    	if(data.status != 0){
		    		$('#serviceNumId').html(data.error);
		    	}else{
		    		$('#serviceNumId').html(data.message.serviceNum);
		    		$('#serviceTaskNumId').html(data.message.taskNum);
		    		$('#serviceStatusOkNumId').html(data.message.statusOkNum);
		    		$('#serviceStatusWarnNumId').html(data.message.statusWarnNum);
		    		$('#serviceStatusErrorNumId').html(data.message.statusErrorNum);		    		
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	resourceCount();
	nodeCount();
	serviceCount();
});