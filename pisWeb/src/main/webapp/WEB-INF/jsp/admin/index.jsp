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
	<script type="text/javascript" src="<%=path%>/public/script/easyui-lang-zh_CN.js"></script>
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
			<div class="user-refresh" data-options="iconCls:'icon-edit'">维护个人信息</div>
			<div class="user-refresh" data-options="iconCls:'icon-lock'">修改密码</div>
			<div class="user-logout" data-options="iconCls:'icon-man'">登出</div>
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
				    	} else if("维护个人信息" == text){
				    		maintainUser();
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
			<div title="欢迎使用"><p><p>
				${systemName} 后台管理主要侧重于集团人员管理和楼盘信息维护以及发布
				

				<h1>YD经纪人员管理主要分为四类</h1>
	<ul>
    <li>APP管理员，可以分配经纪公司账号和驻场专员账号，可以导出所有推荐</li>
   <li> 经纪公司账号，可以分配经纪人账号，该经纪人自动挂载到该公司下面，可以导出该公司下面的所有推荐</li>
    <li>经纪人，不能分配任何账号，可以创建推荐</li>
    <li>驻场专员，不能分配任何账号，可以确认推荐</li>

				</ul>
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
						url : '<%=path%>/api/staff/changePassword.json',
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
		<input name="loginName" type="hidden" value="${CURRENT_USER.loginName}"/>
        <table>
	        <tr>
	        	<td>当前密码：<font style="color:red">*</font></td>
	        	<td><input type="password" name="oldPwd" id="oldPwd" class="easyui-textbox" data-options="validType:'pwdIsExist'" required="required" /></td>
	        </tr>
	        <tr >
	        	<td align="right">新密码：<font style="color:red">*</font></td>
	        	<td><input type="password" name="pwd1" class="easyui-textbox" validType="equalToOldPwd['#oldPwd']"  required="required" /></td>
	        </tr>
	        <tr><td></td>
	        	<td><font color="#C4C4C4">密码由5-25个字符组成，区分大<br/>小写，新密码不能与原密码相同</font></td>
	        </tr>
	        <tr >
	        	<td>确认密码：<font style="color:red">*</font></td>
	        	<td><input type="password"  name='pwd2' class="easyui-textbox" validType="equalTo['#checkPwd input[name=pwd1]']" required="required" /></td>
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
						type :'POST',
						dataType : "json",
						url : '<%=path%>/api/staff/checkPassword.json',
						data : 'oldPwd=' + value+'&loginName=${CURRENT_USER.loginName}',
						async : false,
						success : function(data) {
							flag = data.success;
						} 
					});
					return flag;
				},
				message : '您输入的密码不正确！'
			}
		});
    </script>
	<!-- 修改密码 end -->
	<!-- 维护个人信息 start -->
	<div id="maintainUserInfo"  class="easyui-dialog" title="维护个人信息" style="width:600px;height:250px" 
		data-options="iconCls:'icon-edit',closable:false,
          collapsible:false,minimizable:false,resizable:true,closed:true,maximized:false,modal:true,
         buttons:[{
				text:'确定',
				handler:function(){
				var validate = $('#maintainUserInfoForm').form('validate');
					if(validate){
						$('#maintainUserInfoForm').form('submit', {
							success : function(data) {
								if(null!=data){
									var obj = eval('(' + data + ')');
									if(obj.success){
										$.messager.alert('温馨提示','操作成功!');
										$('#maintainUserInfo').window('close');
									}else{
										$.messager.alert('温馨提示','操作失败!');
									}
								}
							}
						});
					}
				}
			},{
				text:'取消',
				handler:function(){
				$('#maintainUserInfo').window('close');
				}
			}]">
		<form id="maintainUserInfoForm" action="<%=path%>/api/updateUserAndUserInfo.json" method="post" enctype="multipart/form-data">
			<input id="userId" name="userId" type="hidden"/>
			<input id="userIcon" name="userIcon" type="hidden"/>
			<input id="frontPhoto" name="frontPhoto" type="hidden"/>
			<table style="width:100%;height:100%">
				<tr>
					<td align="right">个人头像：</td>
					<td colspan="4"><input name="userIcon" id="userIcon" class="easyui-filebox" style="width: 400px;" data-options="prompt:'选择一张图片'"></td>
				</tr>
				<tr>
					<td align="right">身份证照片：</td>
					<td colspan="4"><input name="cardPhoto" id="cardPhoto" class="easyui-filebox" style="width: 400px;" data-options="prompt:'选择一张图片'"></td>
				</tr>
				<tr>
					<td align="right"><font color="red">*</font>身份证号：</td>
					<td width="30%"><input type="text" name="cardId" id="cardId" class="easyui-textbox" data-options="validType:'idNumberValid'"  required="required" /></td>
					<td align="right"><font color="red">*</font>姓名：</td>
					<td ><input type="text" name="name" id="userName" class="easyui-textbox" required="required"></td>
				</tr>
				<tr>
					<td align="right"><font color="red">*</font>手机号码：</td>
					<td><input type="text" name="tel" id="tel" class="easyui-textbox" required="required"></td>
					<td align="right"><font color="red">*</font>性别：</td>
					<td>
						<select id="sex" name="sex">
							<option id="sex_0" value="0" >男</option>
							<option id="sex_1" value="1">女</option>
							<option id="sex_2"value="2">未知</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">地址：</td>
					<td colspan="4"><input type="text" name="address" id="address" class="easyui-textbox" required="required"></td>
				</tr>
			</table>
		</form>
	</div>
	<script type="text/javascript">
	// 扩展easyui表单的验证
	$.extend($.fn.validatebox.defaults.rules,{
		idNumberValid:{
			validator : function(value, param) {
				if(""!=value){
					var re = new RegExp(/^\d{15}(\d{2}[A-Za-z0-9])?$/);
					var result=value.match(re);
					if(null==result){
						return false;
					}
					return true;
				}
			},
			message : "身份证格式错误!"
		}
	});
	/**
	 * 读取当前用户个人信息 
	 */
	function maintainUser(){
		$.ajax({
			 url:'<%=path%>/api/getUserInfoByUserId.json',
			 dataType: "json",
			 data : 'userId=${CURRENT_USER.userId}',
		     cache : false,
		     success:function(data){
		    	 if(null!=data){
		    		 $("#maintainUserInfo").window('open');
		    		 $("#cardId").textbox('setValue',data.idNumber);
		    		 $("#userName").textbox('setValue',data.name);
		    		 $("#tel").textbox('setValue',data.tel);
		    		 $("#address").textbox('setValue',data.address);
		    		 $("#userId").val(data.userId);
		    		 $("#userIcon").val(data.userIcon);
		    		 $("#frontPhoto").val(data.frontPhoto);
		    		 $("#sex_"+data.sex).attr("selected",true);
		    	 }
		     }
		});
	}
	</script>
	<!-- 维护个人信息 end -->
</body>
</html>