(function($) {
  'use strict';
  
	
  $(function() {
	  var page_storage_info = {
				userCluster:[],
			    currentUser:null,
			    currentCluster:null,
			    menu:[],
			    contentUrl:null
	  };
	  
	  $.fn.serializeJson= function (){
		  var serializeObj={};
		  $( this .serializeArray()).each( function (){
		  serializeObj[ this.name]= this.value;
		  });
		  return serializeObj;
	  };
	  $.extend({ 
		  showMessage:function(type, message){	  
			  //type=success,warning,danger,secondary
			  var alert_html = '<div id="alert_message_id" class="am-alert am-alert-'+type+'">'
				  	+'<button type="button" class="am-close">&times;</button>'
			       +'<p id="message">'+message+'</p>'
			       +'</div>'
			       +'<script>window.setTimeout("$(\'#alert_message_id\').alert(\'close\')",10000);</script>';
			  $('#alertPanel').html(alert_html);
			  $('#alert_message_id').alert();
			  $('.admin-content').scrollTop(0,0);
		  },
		  reloadMain:function(){
			  $(location).attr('href', '/main.html');
		  },
		  getCurrentCluster:function(){
			  return page_storage_info.currentCluster;
		  },
		  contextLoad:function(url){
			  page_storage_info.contentUrl = url;
			  $.ajaxSetup({cache: false });
			  $("#contentId").load(url,function(responseTxt,statusTxt,xhr){
//	    		  if(statusTxt=="success")
//	    			  alert("外部内容加载成功！");
//	    		  if(statusTxt=="error")
//	    			  alert("Error: "+xhr.status+": "+xhr.statusText);
	    	  });
		  },
		  getContextLoadURL:function(){
			  return page_storage_info.contentUrl; 
		  },
		  getContextLoadParam:function(name){
			  //return page_storage_info.contentUrl; 
			  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	          if(page_storage_info.contentUrl==null || page_storage_info.contentUrl.indexOf('?')==-1){
	        	  return null;
	          }
			  var r = page_storage_info.contentUrl.substr(page_storage_info.contentUrl.indexOf('?')+1).match(reg);  //匹配目标参数
	          if (r != null) return unescape(r[2]); return null; //返回参数值
		  }
	  });
	  
	  
	  function checkLogin(status){
		  if(status == -204){
			  $(location).attr('href', '/index.html');
		  }
	  }
	  function closeClusterMenu(){
		  $("#menu_clusterId").dropdown('close');
	  }
	  
      var $fullText = $('.admin-fullText');
      $('#admin-fullscreen').on('click', function() {
           $.AMUI.fullscreen.toggle();
      });

      $(document).on($.AMUI.fullscreen.raw.fullscreenchange, function() {
         $fullText.text($.AMUI.fullscreen.isFullscreen ? '退出全屏' : '开启全屏');
      });
      
      $.extend({'changeCluster':function(clusterId){
    	localStorage.setItem("currentCluster", JSON.stringify({"clusterid":clusterId}));
    	closeClusterMenu();
    	page_storage_info.currentCluster = null;
    	$.each(page_storage_info.userCluster, function(idx, clusterInfo){
			if(clusterInfo.clusterid == clusterId){
				$('#menu_clusternameId').html(clusterInfo.name);
	    		page_storage_info.currentCluster = clusterInfo;
			}
		});
    	$.reloadMain();
      }});
      
      $.extend({'showCluster':function(){
    	  closeClusterMenu();
    	  $.contextLoad("/cluster/new_cluster.html");
      }});
      
      function drawClusterMenu(){
    	  var storage_str = localStorage.getItem("currentCluster");
    	  var currentCluster = null;
    	  if(storage_str){
    		var storage_Cluster = $.parseJSON(storage_str); 
    		$.each(page_storage_info.userCluster, function(idx, clusterInfo){
    			if(clusterInfo.clusterid == storage_Cluster.clusterid){
    				currentCluster = clusterInfo;
    			}
    		});
    	  }
    	
    	  if(currentCluster == null && page_storage_info.userCluster.length > 0){
    		  currentCluster = page_storage_info.userCluster[0];
    	  }
    	  var showClusterName = "";
    	  if(currentCluster == null){
    		  showClusterName = "没有可用域";
    	  }else{
    		  showClusterName = currentCluster.name;
    		  page_storage_info.currentCluster = currentCluster;
    	  }
    	  var cluster_menu_html = '<a class="am-dropdown-toggle" data-am-dropdown-toggle href="javascript:;">'
			  +'<span class="am-icon-cubes"></span>'
			  + '<span id="menu_clusternameId">'+showClusterName+'</span>'
			  +'<span class="am-icon-caret-down"></span>'
			  +'</a>\n'
			  +'<ul class="am-dropdown-content">\n';
    	
    	  $.each(page_storage_info.userCluster, function(idx, clusterInfo){
    		  cluster_menu_html += '<li><a onClick="$.changeCluster(\''+clusterInfo.clusterid+'\')"><span class="am-icon-server"></span> '+clusterInfo.name+'</a></li>\n';
    	  });
    	  if(page_storage_info.currentUser.role == 'admin' || page_storage_info.currentUser.role == 'system'){
    		  cluster_menu_html += '<li><a onClick="$.showCluster()"><span class="am-icon-plus"></span> 添加集群</a></li>\n';
    	  }
    	  cluster_menu_html +='</ul>\n';
    	  $('#menu_clusterId').html(cluster_menu_html);
    	  $('#menu_clusterId').dropdown({justify: '#menu_clusterId'});
      }
      function drawUserMenu(){
    	$('#currentUserNameId').html(page_storage_info.currentUser.username);
      }
      
      function drawLeftChildMenu(pidx, childMenu){
    	  var leftMenu_child_html='';
    	  leftMenu_child_html +='<ul class="am-list admin-sidebar-sub am-collapse" id="collapse-nav'+pidx+'">';
		  $.each(childMenu, function(idx, menu){
			  if(menu.childMenu){
				  leftMenu_child_html += '<li><a class="am-cf am-collapsed" data-am-collapse="{target: \'#collapse-nav'+pidx+""+idx+'\'}"><span class="'+menu['class']+'"></span> '
	  		 		+ menu['name']+'<span class="am-icon-angle-right am-fr am-margin-right"></span></a></li>\n';
    			  drawLeftChildMenu(pidx+""+idx, menu.childMenu);
    		  }else{
    			  leftMenu_child_html += '<li><a href="'+menu['url']+'"><span class="'+menu['class']+'"></span> '
	  		 		+ menu['name']+'</a></li>\n';
    		  }
    	  });
		  leftMenu_child_html +='</ul>';
		  return leftMenu_child_html;
      }
      
      function drawLeftMenu(){
    	  var leftMenu_html = "";
    	  $.each(page_storage_info.menu, function(idx, menu){
    		  if(menu.childMenu){
    			  leftMenu_html += '<li><a class="am-cf am-collapsed" data-am-collapse="{target: \'#collapse-nav'+idx+'\'}"><span class="'+menu['class']+'"></span> '
	  		 		+ menu['name']+'<span class="am-icon-angle-right am-fr am-margin-right"></span></a></li>\n';
    			  leftMenu_html += drawLeftChildMenu(idx, menu.childMenu);
    		  }else{
	    		  leftMenu_html += '<li><a href="'+menu['url']+'"><span class="'+menu['class']+'"></span> '
	  		 		+ menu['name']+'</a></li>\n';
    		  }
    		  
    	  });
    	  $('#leftMenuId').html(leftMenu_html);
      }
      
      $("#logoutId").click(function(){    	  
    	  var aj = $.ajax( {    
			    url:'/api/auth/logout',// 跳转到 action
			    type:'post',    
			    cache:false,    
			    dataType:'json', 
			    async: false,
			    success:function(data) {
			    	if(data.status != 0){
			    		//$('#message').html(data.error);
			    	}else{
			    		//认证成功
			    		
			    	}
			    	return false;
			    },
			    error : function() {
			    }
		  });
    	  $(location).attr('href', '/index.html');
      });
      //
      var userSvc = new UserSvc();
	  userSvc.init();	  
	  checkLogin(userSvc.status);
	  page_storage_info.currentUser = userSvc.info;
	  page_storage_info.userCluster = userSvc.cluster;
	  page_storage_info.menu = userSvc.menu;
	  drawClusterMenu();
      drawUserMenu();
      drawLeftMenu();
  	});
  
})(jQuery);
