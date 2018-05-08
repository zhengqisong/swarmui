
$(function() {
	$("#clusterForm").submit(function(e){
		e.preventDefault();
		
		var jsonObj = $("#clusterForm").serializeJson();
		console.log(JSON.stringify(jsonObj));
		
		$('body').scrollTop(0);
		var aj = $.ajax({    
			url:'/api/cluster/create',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    data:JSON.stringify(jsonObj),
		    cache:false,
		    dataType:'json',
		    async: false,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		$.showMessage("success","添加成功。");
		    		$.contextLoad('/cluster/clusterlist.html');
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	});
	
	$('#new_cluster_cancel').click(function(){ 
		$.contextLoad('/cluster/clusterlist.html');
	});
	
});
  