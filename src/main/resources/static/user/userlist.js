$(function() {
	
	$.extend({ 
		userpage:function(pageNumber, pageSize){
			loadTableData(pageNumber, pageSize);  
	    },
	    deleteUser:function(userid){
	    	var aj = $.ajax({    
				url:'/api/user/delete/'+userid,// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    type:'post',
			    cache:false,
			    dataType:'json',
			    async: true,
			    success:function(data) {
			    	if(data.status != 0){
			    		$.showMessage("warning",data.error);
			    	}else{
			    		$.showMessage("success","成功删除用户。");
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
				url:'/api/user/list?pageNum='+pageNum+"&pageSize="+pageSize,// 跳转到 action    
			    type:'get',    
			    cache:false,
			    dataType:'json' 
			},
			"column":[
			          {"name":"username"},
			          {"name":"account","format":function(username, row){
			        	  return "<a rel=\"popover\" class='targetp'></a>"+username;
			          }},
			          {"name":"email","class":"am-hide-sm-only"},
			          {"name":"telephone","class":"am-hide-sm-only"},
			          {"name":"role","class":"am-hide-sm-only"},		          
			          {"name":"status","class":"am-hide-sm-only"},
			          {"name":"userid","buttons":function(userid, row){
			        	  return [
								{"onClick":"javascript:$.contextLoad('/user/accesskeylist.html?userid="+userid+"&username="+row.username+"')","buttonClass":"success","icon":"th","text":"accesskey"},
								{"onClick":'javascript:showConfirmPanel(\'删除确认\',\'删除将不能找回，您确认要删除？\',this, \'$.deleteUser(\\\''+userid+'\\\')\')',"buttonClass":"danger","icon":"trash","text":"删除"},
								];
				        }}
			          ],
			"paging":true,
			"gotoPage":"$.userpage"
		 });
	}
	
	$('#newUserId').click(function(){
		$.contextLoad("/user/usercreate.html");
	});
	loadTableData(1, 20);
});