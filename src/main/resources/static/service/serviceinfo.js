$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	pServiceName = $.getContextLoadParam("serviceName");
	$('#channel_serviceNameId').html(pServiceName);
	function serviceInfo(){
		var aj = $.ajax({ 
			url:'/api/service/'+currentCluster.clusterid+'/info/'+pServiceName,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		
		    		$("#serviceForm [name=serviceJson]").val(JSON.stringify(JSON.parse(data.message.config), null, 4));
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	serviceInfo();
	
	$('#serviceForm').submit(function(e){
		e.preventDefault();
		var serviceJson = null;
		try{
			serviceJson = JSON.parse($("#serviceForm [name=serviceJson]").val());
		} catch(error) {
			$.showMessage("warning","json格式错误，无法解析。");
			return;
		}
		var aj = $.ajax({
			url:'/api/service/'+currentCluster.clusterid+'/update',// 跳转到 action
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
		    		$.showMessage("success","服务升级中。");
		    		$.contextLoad("/service/servicelist.html");
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	});
	$('#serviceForm2').submit(function(e){
		e.preventDefault();
		
		
	});
	$('#backupId').click(function(){
		$.contextLoad("/service/servicelist.html");
	});
	$('#new_service_cancel').click(function(){
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