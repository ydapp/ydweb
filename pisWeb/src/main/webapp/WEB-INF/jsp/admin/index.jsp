<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> 
<!DOCTYPE html>
<%
	String path = request.getContextPath();
%>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="renderer" content="webkit">
<!-- Add to homescreen for Chrome on Android -->
<meta name="mobile-web-app-capable" content="yes">
<!-- Add to homescreen for Safari on iOS -->
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="apple-mobile-web-app-title" content="${systemName}" />
<title>${systemName}</title>
<link rel="Shortcut Icon" href="<%=path%>/public/images/login/logo.ico"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/metro_blue.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/icon.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/main.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/loading.css" />
</head>
<body class="easyui-layout">
	<div id="loading-mask">
	<div id="loading">
		<div class="loading-indicator">
			<img src="<%=path%>/public/images/loading.gif"/>${systemName}<br /> 
			<span id="loading-msg">正在加载系统资源...</span>
		</div>
	</div>
	</div>
	<script type="text/javascript" src="<%=path%>/public/script/jquery-1.10.2.min.js"></script>
	<script type="text/javascript" src="<%=path%>/public/script/jquery.easyui.min.js"></script>
	<!--顶部位置start-->
	<div id="header" data-options="region:'north',border:false" style="height: 65px;">
	<!-- 修改密码 窗体 打开 这个div 会遮住 原先这块区域上的内容 -->
		<div class="header-logo">
		<div class="header-title">${systemName}</div>
		</div>
		<div class="header-navbar">
			<ul class="nav navbar-nav">
		      <c:forEach items="${menus_kind}" var="item" varStatus="status">
			    <li class="menu-${item.id}"><a <c:if test="${status.index==0}">class="selected"</c:if> href="javascript:void(0);" rel="${item.id}">${item.text}</a></li>
			  </c:forEach>
			</ul>
			<div class="clear"></div>
		</div>
		<span class="header-user">
			<a href="javascript:void(0);" id="user-info" class="easyui-menubutton"
			    data-options="menu:'#user-menu',iconCls:'icon-user'">
			    ${CURRENT_USER.loginName}&nbsp;&nbsp;${CURRENT_USER.userModel.groupTypeTitle}
			    <c:if test="${CURRENT_USER.userModel.groupType=='brokingFirm'}"><br>${CURRENT_USER.userModel.brokingFirm}</c:if>
			    <c:if test="${CURRENT_USER.userModel.groupType=='saleman'}"><br>${CURRENT_USER.userModel.brokingFirm}</c:if>
			    <c:if test="${CURRENT_USER.userModel.groupType=='commissioner'}"><br>${CURRENT_USER.userModel.building}</c:if>
			    </a>
		</span>
		<div id="user-menu" class="easyui-menu" style="width: 130px;">
			<div class="user-refresh" data-options="iconCls:'icon-reload'">刷新</div>
			<div class="menu-sep"></div>
			<div class="user-refresh" data-options="iconCls:'icon-lock'">修改密码</div>
			<div calss="user-logout" data-options="iconCls:'icon-man'">登出</div>
		</div>
		<script type="text/javascript">
			var app = {};
			$(function(){
				$("#loading-msg").html("初始化用户界面...");
				// 顶部下拉菜单
				$('#user-menu').menu({
				    onClick:function(item){
				    	var text = item.text;
				    	if(text == "刷新"){
				    		window.location.href = "<%=path%>/admin/index.html";
				    	} else if(text == "修改密码"){
				    		$("#changePwd").window('open');
				    	} else if(text == "登出"){
				    		window.location.href = "<%=path%>/logout.html";
				    	}
				    }
				});
				
				// 左侧菜单处理
				var sideBarEl = $("#sidebar");
				var itmes = $(".nav li");
				var active = null;
				var activeMenu = null;
				if(itmes.length > 0){
					// 存在一级菜单继续
					active = itmes[0];
					if(active != null){
						loadMenus(active);
					}
					itmes.click(function(){
						if(this == active){
							return;
						}
						loadMenus(this);
					})
				}
				
				function loadMenus(navEl){
					var className = navEl.className;
					$(active).removeClass("active");
					active = navEl;
					$(active).addClass("active");
					var el = $("#"+className);
					if(el != null){
						var width = 0;
						var height = 0;
						if(activeMenu != null){
							width = activeMenu.width();
							height = activeMenu.height();
							activeMenu.panel("close");
						}
						activeMenu = el;
						activeMenu.panel("open");
						if(width !=0 || height !=0){
							activeMenu.panel('resize',{
								width: width,
								height: height
							});
						}
					}
				}
				function addPanel(subtitle,url,icon){
					var mainTabs = $('#main-tabs');
					if(mainTabs.tabs('exists',subtitle)){
						mainTabs.tabs('select',subtitle);
					}else{
						mainTabs.tabs('add',{
							title: subtitle,
							content: createFrame(url),
							closable: true
						});
					}
				}
				app.addPanel = addPanel;
				function createFrame(url){
					var s = '<iframe scrolling="auto" frameborder="0"  src="'+url+'" style="width:100%;height:100%;"></iframe>';
					return s;
				}
				// 延时执行
			    setTimeout(function() { 
			    	$("#loading").fadeOut();
			    	$("#loading-mask").fadeOut();
			    }, 500);
				// 定时器检测登录状态，每二十分钟执行一次
			    setInterval(function(){
			    	$.ajax({
			    		url: "<%=path%>/api/currentUser.json",
			    		dataType: "json",
			    		cache : false,
			    		success: function(data){
			    			if(data && data.success === true){
			    				if(data.result && data.result.type == "staff"){
			    					// 管理员用户登录
			    				}else{
			    					window.location.href = "<%=path%>/logout.html";
			    				}
			    			}else {
			    				window.location.href = "<%=path%>/logout.html";
			    			}
			    		},
			    		error: function(){
			    			// 出错时登出
			    			window.location.href = "<%=path%>/logout.html";
			    		}
			    	});
			    },1220000); 
			});
		</script>
	</div>
	<!--顶部位置start-->

	<!-- 左侧导航栏start  -->
	<div id="sidebar" data-options="region:'west',split:true,border:true,noheader:true"
		style="width: 205px;">
	  <c:forEach items="${menus_kind}" var="item" varStatus="status">
	    <div id="menu-${item.id}" class="easyui-panel sidebar-nav" data-options="fit:true,border:false,closed:true,iconCls:'menu-${item.menuCode}'" title="${item.text}">
			<ul id="menu-tree-${item.id}" class="easyui-tree">
			</ul>
			<script type="text/javascript">
				$(function(){
					var menuTree = $("#menu-tree-${item.id}");
					var index = ${status.index};
					// 菜单加载时添加延时，解决卡顿
					setTimeout(function () { 
						menuTree.tree({
							url:'<%=path%>/admin/usermenus.json',
							onBeforeLoad: function(node, param){
								if(node == null){
									param.id='${item.id}';
								}
							},
							onClick: function(node){
								if(menuTree.tree("isLeaf",node.target)){
									// 子节点打开叶签
									app.addPanel(node.text, '<%=path%>'+node.attributes.url); 
								}else{
									menuTree.tree("expand",node.target)
								}
							}
						});
				    }, 250 * index);
				});
			</script>
		</div>
	  </c:forEach>
	</div>
	<!-- 左侧导航栏end -->
	<div id="main-panel" data-options="region:'center',border:true">
		<div id="main-tabs" class="easyui-tabs" data-options="fit:true,border:false,tabHeight:31" >
			<div title="欢迎使用">
				<h1>系统开发中</h1>
				<%-- <iframe scrolling="auto" frameborder="0"  src="<%=path%>/monitoring" style="width:100%;height:100%;"></iframe> --%>	
			</div>
		</div>
	</div>
	
	<!-- 修改密码 start  -->
	<div id="changePwd"  class="easyui-dialog" title="密码重置窗口"
		style="width:400px;height: 250px"
		data-options="iconCls:'icon-save',closable:false,
          collapsible:false,minimizable:false,resizable:true,closed:true,maximized:false,modal:true,
         buttons:[{
				text:'确定',
				handler:function(){
				if (!$('#checkPwd').form('validate')) {
						return;
					}
					$('#checkPwd').form('submit', {
						url : '<%=request.getContextPath()%>/admin/staff/changePassword.json',
						success : function(data) {
							$('#changePwd').window('close');
							$('#checkPwd').form('clear');
						}
					});
				}
			},{
				text:'取消',
				handler:function(){
				$('#changePwd').window('close');
				$('#checkPwd').form('clear');
				}
			}]">
	
          <br/>
		<form id="checkPwd" method="post">
        <table>
	        <tr >
	        	<td>当前密码：</td>
	        	<td><input type="password" name="oldPwd" id="oldPwd" class="easyui-textbox" data-options="validType:'pwdIsExist'"/></td>
	        </tr>
	        <tr >
	        	<td>新密码：</td>
	        	<td><input type="password" name="pwd1" class="easyui-textbox" validType="equalToOldPwd['#oldPwd']" /></td>
	        </tr>
	        <tr><td></td>
	        	<td><font color="#C4C4C4">密码由5-25个字符组成，区分大<br/>小写，新密码不能与原密码相同</font></td>
	        </tr>
	        <tr >
	        	<td>确认密码：</td>
	        	<td><input type="password"  name='pwd2' class="easyui-textbox" validType="equalTo['#checkPwd input[name=pwd1]']" /></td>
	        </tr>
		</table>     
		</form> 
    </div>
	<script type="text/javascript">
		// 扩展easyui表单的验证
		$.extend($.fn.validatebox.defaults.rules,{
			equalTo : {
				validator : function(value, param) {
	
					return $(param[0]).val() == value;
				},
				message : "两次输入的密码不匹配！"
			},
			equalToOldPwd : {
				validator : function(value, param) {
					if (value.length < 5 || value.length > 25) {
						return false;
					}
					return $(param[0]).val() != value;
				},
				message : "密码格式不正确！"
			},
			pwdIsExist : {
				validator : function(value) {
					var flag;
					$.ajax({
						type : 'POST',
						url : '<%=request.getContextPath()%>/admin/staff/checkPassword.json',
						data : 'oldPwd=' + value,
						async : false,
						success : function(data) {
							flag = data;
						}
					});
					return flag;
				},
				message : '您输入的密码不正确！'
			}
		});
    </script>
	<!-- 修改密码 end -->
</body>
</html>