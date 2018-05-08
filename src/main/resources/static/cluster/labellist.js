$(function() {
	pclusterid = $.getContextLoadParam("clusterid");
	pclustername = $.getContextLoadParam("clustername");
	$('#channel_clusternameId').html("("+pclustername+")");
	$.extend({ 
		deleteLabel:function(labelid){
	    	var aj = $.ajax({    
				url:'/api/cluster/label/'+pclusterid+'/delete/'+labelid,// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    dataType:'json',
			    async: true,
			    success:function(data) {
			    	if(data.status != 0){
			    		$.showMessage("warning",data.error);
			    	} else {
			    		$.showMessage("success","删除集群标签成功。");
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
				url:'/api/cluster/label/'+pclusterid+'/list',// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
					  {"name":"labelname"},
			          {"name":"labelkey","format":function(labelkey, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+labelkey;
			          }},
			          {"name":"labelvalue"},
			          {"name":"labelid","buttons":function(labelid, row){
			        	  return [
								{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回,您确认要删除？\',this, \'$.deleteLabel(\\\''+labelid+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
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
	$('#newLabelId').click(function(){
		$.contextLoad('/cluster/labeladd.html?clusterid='+pclusterid+'&clustername='+pclustername);
	});
});