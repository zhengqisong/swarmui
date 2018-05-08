$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	
	$.extend({ 
		servicepage:function(pageNumber, pageSize){
			loadTableData(pageNumber, pageSize);  
	    },
	    deleteService:function(serviceid){
	    	var aj = $.ajax({    
				url:'/api/service/'+currentCluster.clusterid+'/delete/'+serviceid,// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    dataType:'json',
			    async: true,
			    success:function(data) {
			    	if(data.status != 0){
			    		$.showMessage("warning",data.error);
			    	}else{
			    		$.showMessage("success","成功删除服务。");
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
				url:'/api/service/'+currentCluster.clusterid+'/list',// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
			          {"name":"name","format":function(name, row){
			        	  return "<input type='radio' name='name' value='"+name+"'/>";
			          }},
			          {"name":"name","format":function(name, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+name;
			          }},
			          {"name":"cpus","class":"am-hide-sm-only","format":function(cpus, row){
			        	  if(cpus==null){
			        		  return "不限";
			        	  }else{
			        		  return cpus;
			        	  }
			          }},
			          {"name":"mems","class":"am-hide-sm-only","format":function(mems, row){
			        	  if(mems==null){
			        		  return "不限";
			        	  }else{
			        		  return mems;
			        	  }
			          }},
			          {"name":"replicas","class":"am-hide-sm-only"},
			          {"name":"name","buttons":function(name, row){
			        	  return [
								{"onClick":"javascript:$.contextLoad('/service/serviceinspect.html?name="+name+"')","buttonClass":"success","icon":"th","text":"详情"},
								{"onClick":"javascript:$.contextLoad('/service/serviceInfo.html?serviceName="+name+"')","buttonClass":"success","icon":"th","text":"升级"},
								{"onClick":"javascript:$.contextLoad('/service/userlist.html?serviceid="+row.serviceid+"&name="+name+"')","buttonClass":"success","icon":"th","text":"授权"},
								{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回，您确认要删除？\',this, \'$.deleteService(\\\''+name+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
								];
				        }}
			          ],
			"paging":false,
			"gotoPage":"$.servicepage"
		 });
	}
	function getSelectService(){
		var sServiceName = $('#serviceListFormId [name="name"]:checked').val();
		if(sServiceName != null){
			return sServiceName;
		}else{
			$.showMessage("warning","请选择需要操作的服务。");
			return null;
		}
	}
	$('#newServiceId').click(function(){
		$.contextLoad("/service/servicecreate.html");
	});
	$('#upgradeId').click(function(){
		var sServiceName = getSelectService();
		if(sServiceName != null){
			$.contextLoad("/service/serviceInfo.html?serviceName="+sServiceName);
		}
	});
	$('#authId').click(function(){
		var sServiceName = getSelectService();
		if(sServiceName != null){
			$.contextLoad("/service/serviceInfo.html?serviceName="+sServiceName);
		}
	});
	loadTableData(1, 20);
});