$(function() {
	puserid = $.getContextLoadParam("userid");
	pusername = $.getContextLoadParam("username");
	$('#channel_usernameId').html("("+pusername+")");
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
				url:'/api/userkey/'+puserid+'/list',// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
			          {"name":"keyid"},
			          {"name":"appsecret","format":function(appsecret, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+appsecret;
			          }},
			          {"name":"keyid","buttons":function(keyid, row){
			        	  return [
								{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回，您确认要删除？\',this, \'$.deleteAccessKey(\\\''+keyid+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
								];
				        }}
			          ],
			"paging":false
		 });
	}
	
	$('#newAccesskeyId').click(function(){
		var aj = $.ajax({    
			url:'/api/userkey/'+puserid+'/create',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    cache:false,
		    dataType:'json',
		    async: true,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		$.showMessage("success","创建accesskey成功。");
		    		loadTableData();
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	});
	loadTableData();
	$('#backupId').click(function(){
		$.contextLoad('/user/userlist.html');
	});
});