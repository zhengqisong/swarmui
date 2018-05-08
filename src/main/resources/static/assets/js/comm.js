Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
function addDate(dd,dadd){
	var a = new Date(dd)
	a = a.valueOf()
	a = a + dadd * 24 * 60 * 60 * 1000
	a = new Date(a)
	return a;
}
function isNumber(num){
	if(!isNaN(num) == true && num.trim() != ''){
		return true;
	}else{
		return false;
	}
}
function isInteger(num){
	if(num.trim() != '' && num%1==0){
		return true;
	}else{
		return false;
	}
}

function closeConfirmPanel(ele){
	$('.isShow').each(function(){
		closeConfirmPopoverPanel(this)
	});
}

function closeConfirmPopoverPanel(ele){
	$(ele).removeClass("isShow");
	$(ele).popover('close');
}

function showConfirmPanel(title, message, ele, callback){
	$('.isShow').each(function(){
		closeConfirmPopoverPanel(this)
	});
	
	html = message+"<br><br><input type='button' class='am-btn am-btn-warning' onClick=\"closeConfirmPanel(this);"+callback+"\" value='继续执行'>"
			+"&nbsp;&nbsp;&nbsp;"
			+"<input type='button' class='am-btn am-btn-secondary' value='取消' onClick=\"closeConfirmPanel(this)\">"
	flag = false;
	ele_a = null;
	for(var i=0;i<10;i++){
		if($(ele).find('.targetp').length>0){
			ele_a = $(ele).find('.targetp').first();
			break;
		}else{
			ele = $(ele).parent();
		}
	}
	if(ele_a==null){
		return;
	}
	//ele_a =$(ele).parent().parent().find('.targetp')
	if(ele_a.attr('title') == undefined){
		flag = true;
	}
	ele_a.popover({theme: "default", "title":title,"html":true,  content:html})
	//if(flag){
	ele_a.popover("open")
	//}
	$(ele_a).addClass("isShow");
}

function jsonSyntaxHighlight(json) {
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        var cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
}

$(document).ready(function () {
	$.extend({'writePage':function(pageId, pageSize, pageNumber, total, gotoPageFunc){
		if(gotoPageFunc == undefined || gotoPageFunc == null || gotoPageFunc==''){
			gotoPageFunc = "gotoPage";
		}
		totalPage = 0;
		totalPage = Math.ceil(total/pageSize);

		html = '';
		
		beginPage = pageNumber - 4;
		endPage = pageNumber + 5;
		if(beginPage < 1){
			beginPage = 1;
			endPage = 10;
		}
		if(endPage > totalPage){
			endPage = totalPage;
		}
	    if(endPage < beginPage){
	    	endPage = beginPage;
	    }
		
	    if(beginPage == pageNumber){
			//html +='<li class="prev disabled"><a href="#">← Previous</a></li>'
			html +='<li class="am-disabled"><a href="#">«</a></li>'
	    }else{
			html +='<li><a href="javascript:'+gotoPageFunc+'('+(pageNumber-1)+','+pageSize+')">«</a></li>'
		}
		for(i=beginPage; i<=endPage; i++){
			if(pageNumber==i){
				html += '<li class="am-active"><a href="javascript:'+gotoPageFunc+'('+i+','+pageSize+')">'+i+'</a></li>'
			}else{
				html += '<li><a href="javascript:'+gotoPageFunc+'('+i+','+pageSize+')">'+i+'</a></li>'
			}
		}
		if(endPage == pageNumber){
			html += '<li class="am-disabled"><a href="#">»</a></li>'
		}else{
			html += '<li><a href="javascript:'+gotoPageFunc+'('+(pageNumber+1)+','+pageSize+')">»</a></li>'
		}
		if(pageId==''){
			return html;
		}
		$('#'+pageId).html(html)
	}});
	
	$.extend({'clearTableBody':function(table){
		$('#'+table).html('')
	}});
	
	$.extend({'getUrlParam':function(name){
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]); return null; //返回参数值
	}})
	
	$.extend({'showMessage':function(message){
		//$.noty({text: message});
		$.fn.notifyMe(
				'top',
				'info',
				'提示信息',
				message,
				300
			);
			
		//$("#errormsg").html(message).show(300).delay(3000).hide(3000); 
	}
	});
	
	function drawTable(obj, data){
		tableId = obj['tableId'];
		ajaxData = obj['ajax'];
		jsData = obj['data'];
		columns = obj['column'];
		pageSize = obj['pageSize'];
		pageNumber = obj['pageNumber'];
		paging = obj['paging'];
		addTableRow = obj['addTableRow'];
		rowformat = obj['row'];
		_status = obj['status'];
		statusName = obj['statusName'];
		cleartable = obj['cleartable'];
		beforeListFunc = obj['beforeListFunc'];
		gotoPageFunc = obj['gotoPage'];
		
		if(statusName == undefined){
			statusName = "status";
		}
		if(_status == undefined){
			_status = 0;
		}
		if(data && data[statusName] == _status){
			data = data['message'];
			Object.prototype.toString.call(obj)
			
			if(beforeListFunc != undefined){
				beforeListFunc(data, true);
			}
			if(Object.prototype.toString.call(data) == '[object Object]') {
				rows = data['items'];
			} else {
				rows = data;
			}
	    	if (rows.length<1) {
	    		if(cleartable){
	    			var tr_num = $('#'+tableId+" thead tr th").length;
		    		$('#'+tableId+" tbody").append('<tr><td colspan="'+tr_num+'">没有数据</td></tr>');
	    		}
	    	}
	    	$.each(rows, function(i, row) {
	    		if(addTableRow != undefined)
	    			addTableRow(i, row);
	    		else{
	    			kclass = "";
	    			kstyle = "";
	    			if(rowformat!=undefined && rowformat['class']!=undefined){
	    				kclass = rowformat['class'](row);
	    			}
	    			if(rowformat!=undefined && rowformat['style']!=undefined){
	    				kstyle = rowformat['style'](row);
	    			}
	    			trHtml = "<tr class=\""+kclass+"\" style=\""+kstyle+"\">";
	    			
	    			for(var index in columns){
	    				columnName = columns[index]['name'];
	    				columnFormat = columns[index]['format'];
	    				columnTools = columns[index]['tools'];
	    				columnClass = columns[index]['class'];
	    				columnButtons = columns[index]['buttons'];
	    				if(row==null)
	    				  continue;
	    				kclass = "";
	    				if(columnClass !=undefined){
	    					
	    					kclass = columnClass;//columnClass(row[columnName], row);
	    				}
	    				
	    			    if(columnFormat != undefined){
	    			    	trHtml +="<td class=\""+kclass+"\">"+columnFormat(row[columnName], row)+"</td>";
	    			    }else if(columnTools !=undefined){
	    			    	toolsData = columnTools(row[columnName], row);
	    			    	toolsHtml ='<div class="btn-group pull-right">'
	    			          //+'<a class="btn btn-default" href="javascript:void(0);">执行</a>'
	    			          +'<a class="btn btn-default dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);"><i class="glyphicon glyphicon-chevron-down">执行</i></a>'
	    			          +'<ul class="dropdown-menu dropdown-default" aria-labelledby="dropdownMenu">';
	    			    	for(var t in toolsData) {
	    			    		if(toolsData[t]['href']=='' && toolsData[t]['text']==''){
	    			    			toolsHtml+='<li class="divider"></li>';
	    			    		} else {
	    			    			toolsHtml+='<li><a href="'+toolsData[t]['href']+'">'+toolsData[t]['text']+'</a></li>';
	    			    		}
	    			    	}
	    			    	toolsHtml +='</ul></div>';
	    			    	trHtml += "<td class=\""+kclass+"\">"+toolsHtml+"</td>";
	    			    }else if(columnButtons!=undefined){
	    			    	buttonsData = columnButtons(row[columnName], row);
	    			    	buttonsHtml = "";
	    			    	for(var t in buttonsData){
	    			    		onClick = buttonsData[t]['onClick'];
	    			    		buttonClass = buttonsData[t]['buttonClass'];
	    			    		icon = buttonsData[t]['icon'];
	    			    		text = buttonsData[t]['text'];
	    			    		if(buttonsHtml != ''){
	    			    			buttonsHtml += "&nbsp;&nbsp;";
	    			    		}
	    			    		
	    			    		buttonsHtml +="<a class=\"btn btn-"+buttonClass+" btn-sm\" onClick=\""+onClick+"\"><i class=\"glyphicon glyphicon-"+icon+" icon-white\"></i>"+text+"</a>"
	    			    	}
	    			    	trHtml += "<td class=\""+kclass+"\">"+buttonsHtml+"</td>";
	    			    }
	    			    else{
	    			    	trHtml +="<td class=\""+kclass+"\">"+row[columnName]+"</td>";
	    			    }
	    			}
	    			trHtml += "</tr>";	
	    			$('#'+tableId+" tbody").append(trHtml);
	    		}
	    	});
	    	if(paging){
	    		pageNumber = data['pageNum']
	    		totalRecordCount = data['totalRecode']
	    		pageSize = data['pageSize']
		    	pageItemHtml = $.writePage("", pageSize, pageNumber, totalRecordCount, gotoPageFunc);
		    	from = (pageNumber-1) * pageSize + 1;
		    	to = pageNumber * pageSize + 1;
		    	if(to>totalRecordCount){
		    		to = totalRecordCount;
		    	}
		    	$("#"+tableId).next('.row').remove();
		    	pageHtml = '<div class="row am-cf">'
		    		+'共 '+totalRecordCount+' 条记录'
		    		+'<div class="am-fr">'
		    		+'<ul class="am-pagination">'
		    		+pageItemHtml
		    		+'</ul>'
		    		+'</div>'
		    		+'</div>'
		    	$("#"+tableId).after(pageHtml);
	    	}else{
	    		//$("#"+tableId).after("<br>");
	    	}
		}else{
			$("#"+tableId+" tbody").html("");
			if(beforeListFunc != undefined){
				beforeListFunc(data, false);
			}
			var tr_num = $('#'+tableId+" thead tr th").length;
    		$('#'+tableId+" tbody").append('<tr><td colspan="'+tr_num+'">'+data["error"]+'</td></tr>');
		}
	}
	
	$.extend({'loadTableData':function(obj){
		tableId = obj['tableId'];
		ajaxData = obj['ajax'];
		jsData = obj['data'];
		endFunc = obj['endFunc'];
		cleartable = obj['cleartable'];
		if(cleartable==undefined){
			cleartable = true;
			obj['cleartable'] = cleartable;
		}
		if(cleartable){
			$("#"+tableId+" tbody").html("");
		}
		if(ajaxData != undefined){
			ajaxData["success"]=function(data) {
				if(cleartable){
					$("#"+tableId+" tbody").html("");
				}else{
					$('#'+tableId+' tbody tr')[$('#'+tableId+' tbody tr').length-1].remove();
				}
				drawTable(obj, data);
				if(endFunc != undefined){
					endFunc();
				}
			};
			ajaxData["error"]=function() {
				if(cleartable){
					$("#"+tableId+" tbody").html("");
				}else{
					$('#'+tableId+' tbody tr')[$('#'+tableId+' tbody tr').length-1].remove();
				}
				var tr_num = $('#'+tableId+" thead tr th").length;
				$('#'+tableId+' tbody').append('<tr><td colspan="'+tr_num+'">加载出错</td></tr>');
			};
			var tr_num = $('#'+tableId+" thead tr th").length;
			$('#'+tableId+" tbody").append('<tr><td colspan="'+tr_num+'">加载中....</td></tr>');
			var aj = $.ajax(ajaxData);
		}
		else if(jsData!= undefined){
			if(cleartable){
				$("#"+tableId+" tbody").html("");
			}else{
				$('#'+tableId+' tbody tr')[$('#'+tableId+' tbody tr').length-1].remove();
			}
			drawTable(obj, jsData);
			if(endFunc != undefined){
				endFunc();
			}
		}else{
			if(cleartable){
				$("#"+tableId+" tbody").html("");
			}else{
				$('#'+tableId+' tbody tr')[$('#'+tableId+' tbody tr').length-1].remove();
			}
			var tr_num = $('#'+tableId+" thead tr th").length;
			$('#'+tableId+' tbody').append('<tr><td colspan="'+tr_num+'">加载出错</td></tr>');
		}
		
	}});
	
	function syntaxHighlight(json) {
        json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
            var cls = 'number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'key';
                } else {
                    cls = 'string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'boolean';
            } else if (/null/.test(match)) {
                cls = 'null';
            }
            return '<span class="' + cls + '">' + match + '</span>';
        });
    };
});
