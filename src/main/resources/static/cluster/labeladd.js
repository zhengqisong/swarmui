$(function() {
	pclusterid = $.getContextLoadParam("clusterid");
	pclustername = $.getContextLoadParam("clustername");
	
	var navigation_html = '<strong class="am-text-primary am-text-lg">系统管理</strong>'
		+' / <small><a href="javascript:$.contextLoad(\'/cluster/clusterlist.html\')">集群管理</a></small>'
		+' / <small><a href="javascript:$.contextLoad(\'/cluster/labellist.html?clusterid='+pclusterid+'&clustername='+pclustername+'\')">集群标签管理('+pclustername+')</span></a></small>'
		+' / <small>新增</small> ';
	$('#navigationId').html(navigation_html);
	
	function loadLableSelect(){
		var aj = $.ajax({    
			url:'api/cluster/'+pclusterid+'/nodes/labels',// 跳转到 action
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
		    		var selectHtml = '<select data-am-selected="{btnSize: \'sm\'}" name="labelkey" required>';
		    		$.each(data.message, function(idx, option){
		    			selectHtml +="<option value='"+option.value+"' hostname='"+JSON.stringify(option.hostname)+"'>"+option.key+"("+option.value+")"+"</option>";
		    		});
		    		selectHtml +="</select>";
		    		selectHtml +="标签值，可以在节点详情中添加";
		    		$("#lableSelectPanelId").html(selectHtml);
		    		
		    		$("#labelForm [name=labelkey]").change(function(e){
		    			e.preventDefault();
		    			$('#lableValuePanelId').html($(this).children('option:selected').val());
		    			$('#lableNodePanelId').html(JSON.parse($(this).children('option:selected').attr("hostname")).join("<br>"));
		    		});
	    			$('#lableValuePanelId').html($("#labelForm [name=labelkey]").children('option:selected').val());
	    			$('#lableNodePanelId').html(JSON.parse($("#labelForm [name=labelkey]").children('option:selected').attr("hostname")).join("<br>"));
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	}
	loadLableSelect();
	$("#labelForm").submit(function(e){
		e.preventDefault();
		
		var jsonObj = $("#labelForm").serializeJson();
		jsonObj['clusterid'] = pclusterid;
		jsonObj['labelvalue'] = $("#labelForm [name=labelkey]").children('option:selected').val();
		jsonObj['labelkey'] = $("#labelForm [name=labelkey]").children('option:selected').text();
		
		$('body').scrollTop(0);
		var aj = $.ajax({
			url:'/api/cluster/label/'+pclusterid+'/create',// 跳转到 action
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
		    		$.contextLoad("/cluster/labellist.html?clusterid="+pclusterid+"&clustername="+pclustername);
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	});
	
	$('#new_label_cancel').click(function(){
		$.contextLoad("/cluster/labellist.html?clusterid="+pclusterid+"&clustername="+pclustername);
	});
	
		
});