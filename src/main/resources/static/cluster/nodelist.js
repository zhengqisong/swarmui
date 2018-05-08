$(function() {
	pclusterid = $.getContextLoadParam("clusterid");
	pclustername = $.getContextLoadParam("clustername");
	$('#channel_clusternameId').html("("+pclustername+")");
	$.extend({ 
		deleteAccessKey:function(keyid){
	    	var aj = $.ajax({    
				url:'/api/userkey/'+puserid+'/delete/'+keyid,// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    dataType:'json',
			    async: true,
			    success:function(data) {
			    	if(data.status != 0){
			    		$.showMessage("warning",data.error);
			    	}else{
			    		$.showMessage("success","成功删除accesskey。");
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
				url:'/api/node/'+pclusterid+'/list',// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
			          {"name":"ID","class":"am-hide-sm-only"},
			          {"name":"Description","format":function(Description, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+Description.Hostname;
			          }},
			          {"name":"Status","class":"am-hide-sm-only","format":function(Status, row){
			        	  return Status.State;
			          }},
			          {"name":"Spec","format":function(Spec, row){
			        	  return Spec.Availability;
			          }},
			          {"name":"ManagerStatus","format":function(ManagerStatus, row){
			        	  if(ManagerStatus==null){
			        		  return "";
			        	  }
			        	  if(ManagerStatus.Leader==true){
			        		  return "Leader";
			        	  }
			        	  return ManagerStatus.Reachability;
			          }},
			          {"name":"ID","buttons":function(id, row){
			        	  return [
								{"onClick":"javascript:$.contextLoad('/cluster/nodeinspect.html?clusterid="+pclusterid+"&nodeid="+id+"&clustername="+pclustername+"')","buttonClass":"success","icon":"th","text":"详情"},
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
});