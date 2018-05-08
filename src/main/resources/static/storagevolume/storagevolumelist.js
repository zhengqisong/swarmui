$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	$.extend({ 
		deleteStorageVolume:function(columeid) {
	    	var aj = $.ajax({    
				url:'/api/storagevolume/'+currentCluster.clusterid+'/delete/'+columeid,// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    dataType:'json',
			    async: true,
			    success:function(data) {
			    	if(data.status != 0) {
			    		$.showMessage("warning",data.error);
			    	} else {
			    		$.showMessage("success","删除存储成功。");
			    		loadTableData();
			    	}
			    	return false;
			    },
			    error : function() {
			    	$.showMessage("warning","连接服务器失败。");
			    }
	  	  	});
	    }
	});
	function loadTableData(){
		$.loadTableData({
			"tableId":"listTab",
			"status":0,
			"statusName":"status",
			"beforeListFunc":function(data, flag){
				data['items'] = data['list'];
			},
			"ajax":{
				url:'/api/storagevolume/'+currentCluster.clusterid+'/list',// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
					  {"name":"name","format":function(name, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+name;
			          }},
			          {"name":"code", class:"am-hide-sm-only"},
			          {"name":"storagesize"},
			          {"name":"volumeid","buttons":function(volumeid, row){
			        	  return [
								{"onClick":"javascript:$.contextLoad('/storagevolume/storagevolumeinfo.html?volumeid="+volumeid+"')","buttonClass":"success","icon":"th","text":"详情"},
								{"onClick":"javascript:$.contextLoad('/storagevolume/userlist.html?volumeid="+volumeid+"&name="+row['name']+"')","buttonClass":"success","icon":"th","text":"授权"},
								{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回,您确认要删除？\',this, \'$.deleteStorageVolume(\\\''+volumeid+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
								];
				        }}
			          ],
			"paging":false
		 });
	}
	loadTableData();
	$('#newStorageId').click(function(){
		$.contextLoad('/storagevolume/storagevolumecreate.html');
	});
});