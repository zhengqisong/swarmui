
$(function() {
	pclusterid = $.getContextLoadParam("clusterid");
	
	function loadClusterInfo(){
		var aj = $.ajax({    
			url:'/api/cluster/'+pclusterid+'/info',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		pclusterInfo = data.message;
		    		$('#clusterForm').find("[name=name]").val(pclusterInfo.name);
		    		$('#clusterForm').find("[name=baseUrl]").val(pclusterInfo.baseUrl);
		    		$('#clusterForm').find("[name=maxcpus]").val(pclusterInfo.maxcpus);
		    		$('#clusterForm').find("[name=maxmem]").val(pclusterInfo.maxmem);
		    		$('#clusterForm').find("[name=maxinstance]").val(pclusterInfo.maxinstance);
		    		$('#clusterForm').find("[name=capem]").val(pclusterInfo.capem);
		    		$('#clusterForm').find("[name=certpem]").val(pclusterInfo.certpem);
		    		$('#clusterForm').find("[name=keypem]").val(pclusterInfo.keypem);
		    		$('#clusterForm').find("[name=remark]").val(pclusterInfo.remark);
		    		$('#clusterForm').find("[name=status]").each(function(){
		    	        if($(this).val()==pclusterInfo.status){
		    	            $(this).prop("checked",true);
		    	        }
		    	    });
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	loadClusterInfo();
	
	$("#clusterForm").submit(function(e){
		e.preventDefault();
		
		var jsonObj = $("#clusterForm").serializeJson();
		jsonObj['clusterid'] = pclusterid;
		console.log(JSON.stringify(jsonObj));
		
		$('body').scrollTop(0);
		var aj = $.ajax({    
			url:'/api/cluster/'+pclusterid+'/modify',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    data:JSON.stringify(jsonObj),
		    cache:false,
		    dataType:'json',
		    async: false,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	} else {
		    		$.showMessage("success","修改集群成功。");
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
  