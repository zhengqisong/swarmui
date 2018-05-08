$(function() {
	pclusterid = $.getContextLoadParam("clusterid");
	pclustername = $.getContextLoadParam("clustername");
	ppvid = $.getContextLoadParam("pvid");
	
	var navigation_html = '<strong class="am-text-primary am-text-lg">系统管理</strong>'
		+' / <small><a href="javascript:$.contextLoad(\'/cluster/clusterlist.html\')">集群管理</a></small>'
		+' / <small><a href="javascript:$.contextLoad(\'/cluster/storagepvlist.html?clusterid='+pclusterid+'&clustername='+pclustername+'\')">存储管理('+pclustername+')</span></a></small>'
		+' / <small>详情</small> ';
	$('#navigationId').html(navigation_html);
	
	function storagePvInfo(){
		var aj = $.ajax({
			url:'/api/storagepv/'+pclusterid+'/info/'+ppvid,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    //async: false,
		    success:function(data) {
		    	if(data.status != 0) {
		    		$.showMessage("warning",data.error);
		    	} else {
		    		//data.name
		    		//data.storagetype
		    		//data.localpath
		    		//data.storagesize
		    		//data.volumes
		    		//data.remark
		    		$('#storagePvForm [name=name]').val(data.message.name);
		    		$('#storagePvForm [name=containername]').val(data.message.containername);
		    		$('#storagePvForm [name=localpath]').val(data.message.localpath);
		    		$('#storagePvForm [name=storagesize]').val(data.message.storagesize);
		    		$('#storagePvForm [name=volumes]').val(data.message.volumes);
		    		$('#storagePvForm [name=remark]').val(data.message.remark);
		    		$('#storagePvForm').find("[name=storagetype]").each(function(){
		    	        if($(this).val()==data.message.storagetype){
		    	            $(this).prop("checked",true);
		    	        }
		    	    });
		    		//console.log(data);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	storagePvInfo();
	$("#storagePvForm").submit(function(e){
		e.preventDefault();
		
		var jsonObj = $("#storagePvForm").serializeJson();
		$('body').scrollTop(0);
		var aj = $.ajax({
			url:'/api/storagepv/'+pclusterid+'/modify/'+ppvid,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    data:JSON.stringify(jsonObj),
		    cache:false,
		    dataType:'json',
		    async: false,
		    success:function(data) {
		    	if(data.status != 0) {
		    		$.showMessage("warning",data.error);
		    	} else {
		    		$.showMessage("success","修改存储成功。");
		    		$.contextLoad("/cluster/storagepvlist.html?clusterid="+pclusterid+"&clustername="+pclustername);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	});
	
	$('#new_storagepv_cancel').click(function(){
		$.contextLoad("/cluster/storagepvlist.html?clusterid="+pclusterid+"&clustername="+pclustername);
	});
	
		
});