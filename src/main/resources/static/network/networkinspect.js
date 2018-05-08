$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	pnetworkid = $.getContextLoadParam("networkid");
	pname = $.getContextLoadParam("name");
	$('#channel_networknameId').html(pname);
	function inspect(networkid){
		$('#networkForm [name=name]').val("加载中...");
		var aj = $.ajax({    
			url:'/api/network/'+currentCluster.clusterid+'/inspect/'+networkid,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		$('#networkForm [name=name]').val(pname);
		    		$('#networkForm [name=networkid]').val(pnetworkid);
		    		$('#networkForm [name=code]').val(data.message.Name);
		    		$('#networkForm [name=subnet]').val(data.message.IPAM.Config[0].Subnet);
		    		$('#networkForm [name=gateway]').val(data.message.IPAM.Config[0].Gateway);
		    		$('#networkForm [name=iprange]').val(data.message.IPAM.Config[0].IPRange);
		    		$('#networkForm').find("[name=driver]").each(function(){
		    	        if($(this).val()==data.message.Driver){
		    	            $(this).prop("checked",true);
		    	        }
		    	    });
		    		$('#networkForm').find("[name=ipv6]").each(function(){
		    			var EnableIPv6 = data.message.EnableIPv6;
	    	        	if(EnableIPv6==true){
	    	        		EnableIPv6 = "yes";
	    	        	}else{
	    	        		EnableIPv6 = "no";
	    	        	}
		    	        if($(this).val()==EnableIPv6){
		    	            $(this).prop("checked",true);
		    	        }
		    	    });
		    		$('#networkForm').find("[name=internal]").each(function(){
		    			var Internal = data.message.Internal;
	    	        	if(Internal==true){
	    	        		Internal = "yes";
	    	        	}else{
	    	        		Internal = "no";
	    	        	}
		    			if($(this).val()==Internal){
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
	inspect(pnetworkid);
	$('#networkForm').submit(function(e){
		e.preventDefault();
	});
	$('#backupId').click(function(){
		$.contextLoad("/network/networklist.html");
	});
	
		
});