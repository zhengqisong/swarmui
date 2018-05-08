$(function() {
	pclusterid = $.getContextLoadParam("clusterid");
	pclustername = $.getContextLoadParam("clustername");
	$('#channel_clusternameId').html("("+pclustername+")");
	$.extend({ 
		deleteUserRights:function(userid){
	    	var aj = $.ajax({    
				url:'/api/user/cluster/'+userid+'/rmright/'+pclusterid,// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    dataType:'json',
			    async: true,
			    success:function(data) {
			    	if(data.status != 0){
			    		$.showMessage("warning",data.error);
			    	} else {
			    		$.showMessage("success","删除用户权限成功。");
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
				url:'/api/cluster/'+pclusterid+'/rights/user/list',// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
			          {"name":"username","format":function(username, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+username;
			          }},
			          {"name":"account","class":"am-hide-sm-only","format":function(account, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+account;
			          }},
			          {"name":"rights"},
			          {"name":"userid","buttons":function(userid, row){
			        	  return [
								{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回,<br>且可能导致正在使用此镜像中心的应用出现异常，<br>您确认要删除？\',this, \'$.deleteUserRights(\\\''+userid+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
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
	$('#newRegisterId').click(function(){
		$.contextLoad('/cluster/userrightadd.html?clusterid='+pclusterid+'&clustername='+pclustername);
	});
});