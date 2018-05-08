$(function() {
	
	$.extend({
		deleteCluster:function(clusterid){
	    	var aj = $.ajax({    
				url:'/api/cluster/'+clusterid+'/delete',// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    dataType:'json',
			    async: true,
			    success:function(data) {
			    	if(data.status != 0){
			    		$.showMessage("warning",data.error);
			    	}else{
			    		$.showMessage("success","成功删除集群。");
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
	
	function loadTableData(){
		$.loadTableData({
			"tableId":"listTab",
			"status":0,
			"statusName":"status",
			"beforeListFunc":function(data, flag){
				data['items'] = data['list'];
			},
			"ajax":{
				url:'/api/cluster/list',// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
			          {"name":"clusterid","format":function(clusterid, row){
			        	  return "<input type='radio' name='clusterid' value='"+clusterid+"'/>";
			          }},
					  {"name":"name","format":function(name, row){
							  return "<a rel=\"popover\" class='targetp'></a><span id='cluster_name_"+row['clusterid']+"'>"+name+"</span>";
					   }},
			          {"name":"maxcpus","class":"am-hide-sm-only"},
			          {"name":"maxmem","class":"am-hide-sm-only"},
			          {"name":"maxinstance","class":"am-hide-sm-only"},		          
			          {"name":"status","class":"am-hide-sm-only"},
			          {"name":"clusterid","buttons":function(clusterid, row){
			        	  return [
								{"onClick":"javascript:$.contextLoad('/cluster/info_cluster.html?clusterid="+clusterid+"')","buttonClass":"success","icon":"th","text":"详情"},
								{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回，您确认要删除？\',this, \'$.deleteCluster(\\\''+clusterid+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
								//{"href":"javascript:$.contextLoad('/cluster/info_cluster.html?clusterid="+clusterid+"')","text":"详情"},
								//{"href":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回，您确认要删除？\',this, \'$.deleteCluster(\\\''+clusterid+'\\\')\')',"text":"删除"}
								];
				        }}
			          ],
			"paging":false
		 });
	}
	
	$('#newClusterId').click(function(){
		$.contextLoad("/cluster/new_cluster.html");
	});
	function getSelectCluster(){
		var sClusterId = $('#clusterListFormId [name="clusterid"]:checked').val();
		if(sClusterId){
			return {"clusterid":sClusterId, "clustername":$('#cluster_name_'+sClusterId).html()}
		}else{
			$.showMessage("warning","请选择需要操作的集群。");
			return null;
		}
	}
	$('#nodesId').click(function(){
		var selectCluster = getSelectCluster();
		if(selectCluster != null){
			$.contextLoad("/cluster/nodelist.html?clusterid="+selectCluster.clusterid+"&clustername="+selectCluster.clustername);
		}
	});
	$('#registersId').click(function(){
		var selectCluster = getSelectCluster();
		if(selectCluster != null){
			$.contextLoad("/cluster/registerlist.html?clusterid="+selectCluster.clusterid+"&clustername="+selectCluster.clustername);
		}
	});
	$('#labelsId').click(function(){
		var selectCluster = getSelectCluster();
		if(selectCluster != null){
			$.contextLoad("/cluster/labellist.html?clusterid="+selectCluster.clusterid+"&clustername="+selectCluster.clustername);
		}
	});
	$('#storepvsId').click(function(){
		var selectCluster = getSelectCluster();
		if(selectCluster != null){
			$.contextLoad("/cluster/storagepvlist.html?clusterid="+selectCluster.clusterid+"&clustername="+selectCluster.clustername);
		}
	});
	$('#usersId').click(function(){
		var selectCluster = getSelectCluster();
		if(selectCluster != null){
			$.contextLoad("/cluster/userlist.html?clusterid="+selectCluster.clusterid+"&clustername="+selectCluster.clustername);
		}
	});
	loadTableData();
});