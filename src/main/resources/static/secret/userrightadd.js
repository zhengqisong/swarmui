$(function() {
	var currentCluster = $.getCurrentCluster();
	if(currentCluster == null){
		$.showMessage("warning","请选择集群。");
		return;
	};
	
	psecretid = $.getContextLoadParam("secretid");
	pname = $.getContextLoadParam("name");
	
	var navigation_html = '<strong class="am-text-primary am-text-lg">首页</strong>'
		+' / <small><a href="javascript:$.contextLoad(\'/secret/secretlist.html\')">配置管理</a></small>'
		+' / <small><a href="javascript:$.contextLoad(\'/secret/userlist.html?secretid='+psecretid+'&name='+pname+'\')">配置授权管理('+pname+')</span></a></small>'
		+' / <small>新增</small> ';
	$('#navigationId').html(navigation_html);
	
	function loadUserSelect(){
		var aj = $.ajax({    
			url:'/api/user/listselectoption',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    //data:JSON.stringify(jsonObj),
		    cache:false,
		    dataType:'json',
		    async: false,
		    success:function(data) {
		    	if(data.status != 0) {
		    		$.showMessage("warning",data.error);
		    	} else {
		    		var selectHtml = '<select data-am-selected="{btnSize: \'sm\'}" name="userid" required>';
		    		$.each(data.message, function(idx, option){
		    			selectHtml +="<option value='"+option.value+"'>"+option.text+"</option>";
		    		});
		    		selectHtml +="</select>";
		    		$("#userSelectPanelId").html(selectHtml);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	loadUserSelect();
	$("#userrightForm").submit(function(e){
		e.preventDefault();
		
		var jsonObj = $("#userrightForm").serializeJson();
		//$('body').scrollTop(0);
		var aj = $.ajax({    
			url:'/api/secret/'+currentCluster.clusterid+'/setright/'+psecretid,// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    data:JSON.stringify(jsonObj),
		    cache:false,
		    dataType:'json',
		    async: false,
		    success:function(data) {
		    	if(data.status != 0) {
		    		$.showMessage("warning",data.error);
		    	} else {
		    		$.showMessage("success","添加成功。");
		    		$.contextLoad("/secret/userlist.html?secretid="+psecretid+"&name="+pname);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	});
	
	$('#new_userright_cancel').click(function(){
		$.contextLoad("/secret/userlist.html?secretid="+psecretid+"&name="+pname);
	});
	
		
});