$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	pconfigid = $.getContextLoadParam("configid");
	pname = $.getContextLoadParam("name");
	$('#channel_confignameId').html(pname);
	function inspect(configid){
		$('#configForm [name=name]').val("加载中...");
		var aj = $.ajax({    
			url:'/api/config/'+currentCluster.clusterid+'/inspect/'+configid,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		$('#configForm [name=name]').val(pname);
		    		$('#configForm [name=configid]').val(pconfigid);
		    		$('#configForm [name=code]').val(data.message.Spec.Name);		    		
		    		$('#configForm [name=CreatedAt]').val(data.message.CreatedAt);
		    		$('#configForm [name=UpdatedAt]').val(data.message.UpdatedAt);
		    		$('#configForm [name=Data]').val(data.message.Spec.Data);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	inspect(pconfigid);
	$('#configForm').submit(function(e){
		e.preventDefault();
	});
	$('#backupId').click(function(){
		$.contextLoad("/config/configlist.html");
	});
	
		
});