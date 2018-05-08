(function($) {
  'use strict';
  //alert("ddd");
  
  $(function() {
	  function auth(account, password){
		  $('#message').html("认证中...");
		  $("#submit").prop("disabled",true);
		  var aj = $.ajax( {    
			    url:'/api/auth/login',// 跳转到 action
			    contentType: "application/json;charset=utf-8",
			    data:JSON.stringify({    
			    	account : account,
			    	passwd : password
			    }),
			    type:'post',    
			    cache:false,    
			    dataType:'json',    
			    success:function(data) {
			    	if(data.status != 0){
			    		$("#submit").prop("disabled",false);
			    		$('#message').html(data.error);
			    	}else{
			    		//认证成功
			    		var userInfo = data.message;
			    		$('#message').html("认证成功");
			    		$(location).attr('href', '/main.html');
			    		
			    	}
			    	return false;
			    },
			    error : function() {
			    	$("#submit").prop("disabled",false);
			        var error_html ='<li class="ui-border-t">'
				           +'<p>认证失败</p>'
				           +'</li>';
			        $('#message').html(error_html);
			        
			    }
		  });
	  }
	  $("form").submit(function(e){
		  e.preventDefault();
		  var account = $(this).find("#account").val();
		  var password = $(this).find("#password").val();
		  auth(account, password);
		  
	  });
	  
	  function userInfo(){
		  var aj = $.ajax( {    
			    url:'/api/auth/userinfo',// 跳转到 action
			    type:'post',    
			    cache:false,    
			    dataType:'json',    
			    success:function(data) {
			    	if(data.status != 0){
			    		$('#message').html(data.error);
			    	}else{
			    		//认证成功
			    		var userInfo = data.message;
			    		$('#message').html("<<"+JSON.stringify(userInfo)+">>");
			    	}
			    	return false;
			    },
			    error : function() {
			        var error_html ='<li class="ui-border-t">'
				           +'<p>认证失败</p>'
				           +'</li>';
			        $('#message').html(error_html);
			    }
		  });
	  }
	  $("#forgetpwd").click(function(e){
		  e.preventDefault();
		  userInfo();
	  });
  });  
})(jQuery);
//localStorage.setItem("dsds", JSON.stringify({"dsadsa":"dsadsa"}));
//localStorage.getItem("dsds");