$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	
	pconfigid = $.getContextLoadParam("configid");
	pname = $.getContextLoadParam("name");
	$('#channel_confignameId').html("("+pname+")");
	$.extend({ 
		deleteUserRights:function(userid){
	    	var aj = $.ajax({    
				url:'/api/config/'+currentCluster.clusterid+'/setright/'+pconfigid,// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    data:JSON.stringify({"userid":userid}),
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
				url:'/api/config/'+currentCluster.clusterid+'/rights/'+pconfigid+'/user/list',// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
			          {"name":"username","format":function(username, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+username;
			          }},
			          {"name":"account","class":"am-hide-sm-only"},
			          {"name":"isowner","class":"am-hide-sm-only"},
			          {"name":"rights", "format":function(rights,row){
			        	  var rights_name = "";
			        	  if(rights.indexOf("r")!=-1){
			        		  rights_name += "读";
			        	  }
			        	  if(rights.indexOf("w")!=-1){
			        		  rights_name += "写";
			        	  }
			        	  if(rights.indexOf("d")!=-1){
			        		  rights_name += "删";
			        	  }
			        	  return rights_name;
			          }},
			          {"name":"userid","buttons":function(userid, row){
			        	  return [
								{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回, 您确认要删除？\',this, \'$.deleteUserRights(\\\''+userid+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
								];
				        }}
			          ],
			"paging":false
		 });
	}
	loadTableData();
	$('#backupId').click(function(){
		$.contextLoad('/config/configlist.html');
	});
	$('#newUserId').click(function(){
		$.contextLoad('/config/userrightadd.html?configid='+pconfigid+'&name='+pname);
	});
});