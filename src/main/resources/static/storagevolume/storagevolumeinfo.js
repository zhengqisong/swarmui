$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	pvolumeid = $.getContextLoadParam("volumeid");
	$.extend({ 
		showPvRemark:function(){	  
			remark = $("#storageVolumeForm [name=pvid]").find("option:selected").attr("remark");
			remark = encodeURI(remark);
			$('#pvRemarkPanelId').html(remark);
		  }
	});
	function loadStoragePvSelect(){
		var aj = $.ajax({
			url:'/api/storagepv/'+currentCluster.clusterid+'/list',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: false,
		    success:function(data) {
		    	if(data.status != 0) {
		    		$.showMessage("warning",data.error);
		    	} else {
		    		option_html ='<select name="pvid" data-am-selected="{btnSize: \'sm\'}" disabled onChange="$.showPvRemark()">'
	    			$.each(data.message, function(idx, pvInfo){
	    				option_html +='<option value="'+pvInfo.pvid+'" remark="'+encodeURI(pvInfo.remark)+'">'+pvInfo.name+'('+pvInfo.storagetype+')'+'</option>';
	    			});	                
	                option_html +='</select>';
	                
	                $("#pvPanelId").html(option_html);
	                $.showPvRemark();
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	function loadStorageVolumeInfo(){
		var aj = $.ajax({
			url:'/api/storagevolume/'+currentCluster.clusterid+'/info/'+pvolumeid,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: false,
		    success:function(data) {
		    	if(data.status != 0) {
		    		$.showMessage("warning",data.error);
		    	} else {
		    		volumeInfo = data.message;
		    		$("#storageVolumeForm [name=volumeid]").val(volumeInfo.volumeid);
		    		$("#storageVolumeForm [name=code]").val(volumeInfo.code);
		    		$("#storageVolumeForm [name=name]").val(volumeInfo.name);
		    		$("#storageVolumeForm [name=storagesize]").val(volumeInfo.storagesize);
		    		$("#storageVolumeForm [name=pvid]").val(volumeInfo.pvid);
	                $.showPvRemark();
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	loadStoragePvSelect();
	loadStorageVolumeInfo();
	$("#storageVolumeForm").submit(function(e){
		e.preventDefault();
		
		var jsonObj = $("#storageVolumeForm").serializeJson();
		$('body').scrollTop(0);
		var aj = $.ajax({
			url:'/api/storagevolume/'+currentCluster.clusterid+'/modify/'+pvolumeid,// 跳转到 action
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
		    		$.contextLoad("/storagevolume/storagevolumelist.html");
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	});
	
	$('#new_storagevolume_cancel').click(function(){
		$.contextLoad("/storagevolume/storagevolumelist.html");
	});
	
		
});