$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	
	$('#configForm').submit(function(e){
		e.preventDefault();
		var jsonObj = $("#configForm").serializeJson();
		var aj = $.ajax({    
			url:'/api/config/'+currentCluster.clusterid+'/create',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    data:JSON.stringify(jsonObj),
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		$.showMessage("success","成功添加配置。");
		    		$.contextLoad("/config/configlist.html");
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	});
	$('#backupId').click(function(){
		$.contextLoad("/config/configlist.html");
	});
	$('#new_config_cancel').click(function(){
		$.contextLoad("/config/configlist.html");
	});	
});