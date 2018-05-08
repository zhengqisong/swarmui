$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	psecretid = $.getContextLoadParam("secretid");
	pname = $.getContextLoadParam("name");
	$('#channel_secretnameId').html(pname);
	function inspect(secretid){
		$('#secretForm [name=name]').val("加载中...");
		var aj = $.ajax({    
			url:'/api/secret/'+currentCluster.clusterid+'/inspect/'+secretid,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		$('#secretForm [name=name]').val(pname);
		    		$('#secretForm [name=secretid]').val(psecretid);
		    		$('#secretForm [name=code]').val(data.message.Spec.Name);
		    		$('#secretForm [name=CreatedAt]').val(data.message.CreatedAt);
		    		$('#secretForm [name=UpdatedAt]').val(data.message.UpdatedAt);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	inspect(psecretid);
	$('#secretForm').submit(function(e){
		e.preventDefault();
	});
	$('#backupId').click(function(){
		$.contextLoad("/secret/secretlist.html");
	});
	
		
});