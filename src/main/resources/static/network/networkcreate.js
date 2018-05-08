$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	
	$('#networkForm').submit(function(e){
		e.preventDefault();
		var jsonObj = $("#networkForm").serializeJson();
		var aj = $.ajax({    
			url:'/api/network/'+currentCluster.clusterid+'/create',// 跳转到 action
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
		    		$.showMessage("success","成功添加网络。");
		    		$.contextLoad("/network/networklist.html");
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	});
	$('#new_network_cancel').click(function(){
		$.contextLoad("/network/networklist.html");
	});	
});