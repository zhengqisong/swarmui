$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	$.extend({ 
		userpage:function(pageNumber, pageSize){
			loadTableData(pageNumber, pageSize);  
	    },
	    deleteSecret:function(secretid){
	    	var aj = $.ajax({    
				url:'/api/secret/'+currentCluster.clusterid+'/delete/'+secretid,// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    dataType:'json',
			    async: true,
			    success:function(data) {
			    	if(data.status != 0){
			    		$.showMessage("warning",data.error);
			    	}else{
			    		$.showMessage("success","成功删除敏感数据。");
			    		loadTableData(1,20);
			    	}
			    	return false;
			    },
			    error : function() {
			    	$.showMessage("warning","连接服务器失败。");
			    }
	  	  	});
	    }
	});
	
	function loadTableData(pageNum, pageSize){
		
		$.loadTableData({
			"tableId":"listTab",
			"status":0,
			"statusName":"status",
			"beforeListFunc":function(data, flag){
				data['items'] = data['list'];
			},
			"ajax":{
				url:'/api/secret/'+currentCluster.clusterid+'/list',// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
			          {"name":"name","format":function(name, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+name;
			          }},
			          {"name":"code"},
			          {"name":"secretid","buttons":function(secretid, row){
			        	  return [
								{"onClick":"javascript:$.contextLoad('/secret/secretinspect.html?secretid="+secretid+"&name="+row['name']+"')","buttonClass":"success","icon":"th","text":"详情"},
								{"onClick":"javascript:$.contextLoad('/secret/userlist.html?secretid="+secretid+"&name="+row['name']+"')","buttonClass":"success","icon":"th","text":"授权"},
								{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回，您确认要删除？\',this, \'$.deleteSecret(\\\''+secretid+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
								];
				        }}
			          ],
			"paging":false
		 });
	}
	
	$('#newSecretId').click(function(){
		$.contextLoad("/secret/secretcreate.html");
	});
	loadTableData(1, 20);
});