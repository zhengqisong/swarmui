$(function() {
	pclusterid = $.getContextLoadParam("clusterid");
	pclustername = $.getContextLoadParam("clustername");
	$('#channel_clusternameId').html("("+pclustername+")");
	$.extend({ 
		deleteStoragePv:function(pvid) {
	    	var aj = $.ajax({    
				url:'/api/storagepv/'+pclusterid+'/delete/'+pvid,// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    dataType:'json',
			    async: true,
			    success:function(data) {
			    	if(data.status != 0) {
			    		$.showMessage("warning",data.error);
			    	} else {
			    		$.showMessage("success","删除集群存储成功。");
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
				url:'/api/storagepv/'+pclusterid+'/list',// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
					  {"name":"name"},
			          {"name":"storagetype","format":function(storagetype, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+storagetype;
			          }},
			          {"name":"localpath"},
			          {"name":"storagesize"},
			          {"name":"volumes"},
			          {"name":"pvid","buttons":function(pvid, row){
			        	  return [
								{"onClick":"javascript:$.contextLoad('/cluster/storagepvinfo.html?clusterid="+pclusterid+"&pvid="+pvid+"&clustername="+pclustername+"')","buttonClass":"success","icon":"th","text":"详情"},
								{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回,您确认要删除？\',this, \'$.deleteStoragePv(\\\''+pvid+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
								];
				        }}
			          ],
			"paging":false
		 });
	}
	loadTableData();
	$('#backupId').click(function(){
		$.contextLoad('/cluster/clusterlist.html');
	});
	$('#newStorageId').click(function(){
		$.contextLoad('/cluster/storagepvcreate.html?clusterid='+pclusterid+'&clustername='+pclustername);
	});
});