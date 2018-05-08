$(function() {
	
	$("#userForm").submit(function(e){
		e.preventDefault();
		
		var jsonObj = $("#userForm").serializeJson();
		if(jsonObj.passwd != jsonObj.reppasswd){
			$.showMessage("warning","两次密码输入不一致。");
			return;
		}
		$('body').scrollTop(0);
		var aj = $.ajax({    
			url:'/api/user/create',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',
		    data:JSON.stringify(jsonObj),
		    cache:false,
		    dataType:'json',
		    async: false,
		    success:function(data) {
		    	if(data.status != 0){
		    		$.showMessage("warning",data.error);
		    	}else{
		    		$.showMessage("success","添加成功。");
		    		$.contextLoad("/user/userlist.html");
		    	}
		    	return false;
		    },
		    error : function() {
		    	$.showMessage("warning","连接服务器失败。");
		    }
  	  	});
	});
	
	$('#new_user_cancel').click(function(){
		$.contextLoad("/user/userlist.html");
	});
	
		
});