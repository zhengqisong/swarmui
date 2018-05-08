function UserSvc(){
	var currentUser = new Object();
	currentUser.info = null;
	currentUser.cluster = null;
	currentUser.menu = null;
	currentUser.status = 0;
	currentUser.error = "";
	
	currentUser.getMenu = function(){
		var serviceResourceChildMenu=[{"name":"网络管理","url":"javascript:$.contextLoad('/network/networklist.html')","class":"am-icon-random"},
		      		                {"name":"存储管理","url":"javascript:$.contextLoad('/storagevolume/storagevolumelist.html')","class":"am-icon-cubes"},
		      		                {"name":"配置管理","url":"javascript:$.contextLoad('/config/configlist.html')","class":"am-icon-files-o"},
		      		                {"name":"敏感数据管理","url":"javascript:$.contextLoad('/secret/secretlist.html')","class":"am-icon-lock"}
		      		                ];
		var userMenu = [{"name":"仪表盘","url":"javascript:$.contextLoad('/dashboard.html')","class":"am-icon-dashboard"},
  	                    {"name":"服务管理","url":"javascript:$.contextLoad('/service/servicelist.html')","class":"am-icon-th"},
		                {"name":"服务资源","url":"","class":"am-icon-th-large","childMenu":serviceResourceChildMenu}
	                ];
		if(this.status==0 && this.info.role=="system"){
			childMenu = [];
			childMenu.push({"name":"用户管理","url":"javascript:$.contextLoad('/user/userlist.html')","class":"am-icon-user"});
			childMenu.push({"name":"集群管理","url":"javascript:$.contextLoad('/cluster/clusterlist.html')","class":"am-icon-server"});
			userMenu.push({"name":"系统管理","url":"","class":"am-icon-cloud","childMenu":childMenu});
			
		}else if(this.status==0 && this.info.role=="admin"){
			childMenu = [];
			childMenu.push({"name":"集群管理","url":"javascript:$.contextLoad('/cluster/clusterlist.html')","class":"am-icon-server"});
			userMenu.push({"name":"系统管理","url":"","class":"am-icon-cloud","childMenu":childMenu});
		}
		
		this.menu = userMenu;
		return 0;
	};
	
	currentUser.loadCurrentUserInfo = function(){
		var status = 0;
		var pthis = this;
		var aj = $.ajax({    
			url:'/api/auth/userinfo',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',    
		    cache:false,    
		    dataType:'json',
		    async: false,
		    success:function(data) {
		    	status = data.status;
		    	if(data.status != 0){		    		
		    	}else{
		    		pthis.info = data.message;
		    	}
		    	return false;
		    },
		    error : function() {
		    	status = -1;
		    }
  	  	});
		return status;
	};
  
	currentUser.loadCluster = function(){
		var status = 0;
		var pthis = this;
		var aj = $.ajax( {
		    url:'/api/user/cluster/listself',// 跳转到 action
		    contentType: "application/json;charset=utf-8",
		    type:'post',    
		    cache:false,    
		    dataType:'json',
		    async: false,
		    success:function(data) {
		    	status = data.status;
		    	if(data.status != 0){
		    	} else {
		    		pthis.cluster = data.message;
		    	}
		    	return false;
		    },
		    error : function() {	    	
		    	status = -1;		        
		    }
  		});
		return status;
    };
    currentUser.init = function(){
		this.status = this.loadCurrentUserInfo();
		if(this.status == 0){
			this.status = this.loadCluster();
		}
		
		if(this.status == 0){
			this.status = this.getMenu();
		}
	};
  return currentUser;
}

