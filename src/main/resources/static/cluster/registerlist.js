$(function() {
	pclusterid = $.getContextLoadParam("clusterid");
	pclustername = $.getContextLoadParam("clustername");
	$('#channel_clusternameId').html("("+pclustername+")");
	$.extend({ 
		deleteRegister:function(registerid){
	    	var aj = $.ajax({    
				url:'/api/register/'+pclusterid+'/delete/'+registerid,// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    dataType:'json',
			    async: true,
			    success:function(data) {
			    	if(data.status != 0){
			    		$.showMessage("warning",data.error);
			    	}else{
			    		$.showMessage("success","成功删除镜像中心。");
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
				url:'/api/register/'+pclusterid+'/list',// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
			          {"name":"name","format":function(name, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+name;
			          }},
			          {"name":"address"},
			          {"name":"isauth","format":function(isauth, row){
			        	  if(isauth=='yes'){
			        		  return "是";
			        	  }
			        	  if(isauth=='no'){
			        		  return "否";
			        	  }
			        	  return '未知';
			          }},
			          {"name":"registerid","buttons":function(registerid, row){
			        	  return [
								{"onClick":"javascript:$.contextLoad('/cluster/registerinfo.html?clusterid="+pclusterid+"&registerid="+registerid+"&clustername="+pclustername+"')","buttonClass":"success","icon":"th","text":"详情"},
								{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回,<br>且可能导致正在使用此镜像中心的应用出现异常，<br>您确认要删除？\',this, \'$.deleteRegister(\\\''+registerid+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
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
		$.contextLoad('/cluster/registercreate.html?clusterid='+pclusterid+'&clustername='+pclustername);
	});
});