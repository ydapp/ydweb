<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
<link rel="Shortcut Icon"
	href="<%=request.getContextPath()%>/public/images/login/logo.ico" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/public/style/login.css" />
<link rel="stylesheet" type="text/css"
	href="<%=path%>/public/style/loading.css" />
</head>
<body class="login-container">
	<div class="login-top">
		<div class="login-title">${systemName}</div>
	</div>
	<div class="login-body">
		<div class="form-wrapp">
			<div class="form-container">
				<div class="form-header">登录</div>
				<div class="form-error-wrap">
					<span class="form-error">${msg}</span>
				</div>
				<form id="loginForm" name="loginForm" action="login.html"
					method="post">
					<div class="form-group">
						<label for="username">用户名：</label> <input id="username"
							name="username" class="form-control" type="text"
							placeholder="<spring:message code='enter_username'/>" />
					</div>
					<div class="form-group">
						<label for="password">密&nbsp;码：</label> <input id="password"
							name="password" class="form-control" type="password"
							placeholder="<spring:message code='enter_password'/>" />
					</div>
					<div class="form-group" style="padding-left: 60px;">
						<input class="btn form-submit" type="submit"
							value="<spring:message code='sign_in'/>" /> <input
							class="btn form-reset" type="reset" value="重置" />
					</div>
				</form>
			</div>
			<div style="clear: both; height: 1px;"></div>
		</div>
	</div>
	<div class="login-foot">
		<span class="coryright">Copyright ${copyright}</span><br>
		<span class="coryright">版本(0.9.8)</span>
	</div>
	<div id="loading-mask" style="display: none;">
		<div id="loading">
			<div class="loading-indicator">
				<img src="<%=path%>/public/images/loading.gif" />${systemName} <br />
				<span id="loading-msg">正在登录系统...</span>
			</div>
		</div>
	</div>
	<script
		src="<%=request.getContextPath()%>/public/script/jquery-1.10.2.min.js"
		type="text/javascript"></script>
	<script type="text/javascript">
		var errorEl = $(".form-error");
		var flag = true;
		function mySubmit() {
			var username = $("#username").val();
			if (!username) {
				errorEl.html("用户名不能为空");
				return false;
			}
			var password = $("#password").val();
			if (!password) {
				errorEl.html("密码不能为空");
				return false;
			}
			if (flag) {
				$("#loading-mask").fadeIn();
				setTimeout(function() {
					$("#loading-msg").html("检查用户的有效性...");
					document.loginForm.submit();
				}, 500);
			}
			flag = !flag;
			return false;
		}
		$(function() {
			$("#username").focus();
			// 页面布局
			var bodyEl = $(document.body);
			var topEl = $(".login-top");
			var titleEl = $(".login-title");
			var footEl = $(".login-foot");
			var coryrightEl = $(".coryright");
			function layout() {
				var totalHeight = $(document.body).height();
				if (totalHeight <= 580) {
					// 使用默认的值
					topEl.css("height", "120px");
					titleEl.css({
						"height" : "120px",
						"line-height" : "120px"
					});
					footEl.css("height", "60px");
					coryrightEl.css("top", "20px");
				} else if (totalHeight < 640) {
					var height = (totalHeight - 400 - 60) + "px";
					topEl.css("height", height);
					titleEl.css({
						"height" : height,
						"line-height" : height
					});
					footEl.css("height", "60px");
					coryrightEl.css("top", "20px");
				} else {
					var height = totalHeight - 400 - 180;
					var top = 20;
					if (height > 100) {
						top = height - 60;
					} else if (height > 70) {
						top = height - 50;
					}
					topEl.css("height", "180px");
					titleEl.css({
						"height" : "180px",
						"line-height" : "180px"
					});
					footEl.css("height", height);
					coryrightEl.css("top", top);
				}
			}
			$(window).resize(layout);
			layout();
			$("#loginForm").submit(function(e) {
				return mySubmit();
			});
			// 异常信息
			$(".form-control").focus(function() {
				errorEl.html("");
			});
		})
	</script>
</body>
</html>
