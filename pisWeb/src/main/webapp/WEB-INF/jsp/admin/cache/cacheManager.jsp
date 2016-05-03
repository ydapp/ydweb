<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" session="false"%>
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
<meta name="apple-mobile-web-app-title" content="缓存管理" />
<%-- <jsp:include page="header.jsp" /> --%>
<link rel="stylesheet" type="text/css"
	href="<%=path%>/public/style/metro_blue.css" />
<link rel="stylesheet" type="text/css"
	href="<%=path%>/public/style/icon.css" />
<link rel="stylesheet" type="text/css"
	href="<%=path%>/public/jsonformat/jsonformat.css" />
<title>主布局</title>
</head>
<body class="easyui-layout">
	<div region="west" split="true" title="缓存名" style="width: 33%;">
		<div id="tbCacheName">
			<a href="javascript:void(0)" class="easyui-linkbutton"
				iconCls="icon-remove" plain="true"
				onclick="javascript:clearByCacheName()">清空缓存</a>
		</div>
		<table id="cacheNameTb" class="easyui-datagrid" style="height: 520px"
			data-options="iconCls:'icon-edit',singleSelect:true,fit:true,pagination:true,pageSize:100,pageList:[100]">
			<thead>
				<tr>
					<th data-options="field:'cacheName',editor:'text'" align="left"
						width="33%">缓存名</th>
					<th data-options="field:'cacheSource',editor:'text'" align="left"
						width="33%">来源</th>
					<th data-options="field:'keySize',editor:'text'" align="left"
						width="33%">缓存数量</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="keyDiv" region="center" title="键列表" style="width: 33%;">
		<div id="tbKey">
			<a href="javascript:void(0)" class="easyui-linkbutton"
				iconCls="icon-remove" plain="true" onclick="javascript:removeByKey()">删除缓存</a>
		</div>
		<table id="keyTb" class="easyui-datagrid"
			data-options="iconCls:'icon-edit',singleSelect:true,fit:true,pagination:true,pageSize:100,pageList:[100,200,500]">
			<thead>
				<tr>
					<th field='key' align="left" width="100%">键</th>
				</tr>
			</thead>
		</table>
	</div>
	<div data-options="region:'east',title:'缓存值',split:true"
		style="width: 33%;" id="valueDiv">
		<div id="valueSpan"></div>
	</div>
	<script type="text/javascript"
		src="<%=path%>/public/script/jquery-1.10.2.min.js"></script>
	<script type="text/javascript"
		src="<%=path%>/public/script/jquery.easyui.min.js"></script>
	<script type="text/javascript"
		src="<%=path%>/public/script/easyui-lang-zh_CN.js"></script>
	<script type="text/javascript"
		src="<%=path%>/public/jsonformat/jsonformat.js"></script>
		
	<script type="text/javascript">
		$(function() {
			initDataGridTB();
			loadCacheNames();
		});
		
		function initDataGridTB() {
			$('#cacheNameTb').datagrid({
				toolbar: '#tbCacheName'
			});
			$('#keyTb').datagrid({
				toolbar: '#tbKey'
			});
		}
		
		function loadCacheNames() {
			$('#cacheNameTb').datagrid({
				url : '<%=path%>/api/CacheManager/getCacheNames.json',
				onClickRow:function(index,data) {
					$('#valueSpan').html('');
					loadKeysByCacheName();
				}
			});
		}
		
		function loadKeysByCacheName() {
			var row = $('#cacheNameTb').datagrid('getSelected');
			$('#keyTb').datagrid({
				url : '<%=path%>/api/CacheManager/getCacheKeysByCacheName.json',
				queryParams:{cacheName:row.cacheName,cacheSource:row.cacheSource},
				onClickRow:function(index,data) {
					$('#valueSpan').html('');
					loadCacheValue();
				}
			});
		}
		
		function loadCacheValue() {
			var row = $('#keyTb').datagrid('getSelected');
			$.ajax({
				type:'post',
				dataType:'text',
				url:'<%=path%>/api/CacheManager/getValueByKey.json',
				data:'cacheName='+row.cacheName+'&cacheSource='+row.cacheSource+'&key='+row.key,
		  		success:function(data){
		  			JsonFormat.process("valueSpan",data);
		 		}
			})
		}
		
		function clearByCacheName() {
			var row = $('#cacheNameTb').datagrid('getSelected');
			if(row == null) {
				$.messager.alert('提示信息','请选择一个缓存名！','warning');
				return;
			}
			$.messager.confirm('确认','您确认清空该缓存吗？',function(r){
				if(r) {
					$.ajax({
						type:'post',
						url:'<%=path%>/api/CacheManager/clearByCacheName.json',
						data:'cacheName='+row.cacheName+'&cacheSource='+row.cacheSource,
				  		success:function(data){
				  			if(data == true || data == 'true') {
				  				$.messager.alert('提示信息','清空缓存成功！','warning');
				  				$('#valueSpan').html('');
				  				$('#cacheNameTb').datagrid('reload');
				  				$('#keyTb').datagrid('reload');
				  			} else {
				  				$.messager.alert('提示消息','清空缓存失败！','warning');
				  			}
				 		}
					})
				}
			})
			
		}
		
		function removeByKey() {
			var row = $('#keyTb').datagrid('getSelected');
			if(row == null) {
				$.messager.alert('提示信息','请选择一个缓存key！','warning');
				return;
			}
			$.messager.confirm('确认','您确认清空该缓存吗？',function(r){
				if(r) {
					$.ajax({
						type:'post',
						url:'<%=path%>/api/CacheManager/removeByKey.json',
						data:'cacheName='+row.cacheName+'&cacheSource='+row.cacheSource+'&key='+row.key,
				  		success:function(data){
				  			if(data == true || data == 'true') {
				  				$.messager.alert('提示信息','删除缓存成功','warning');
				  				$('#valueSpan').html('');
				  				$('#cacheNameTb').datagrid('reload');
				  				$('#keyTb').datagrid('reload');
				  			} else {
				  				$.messager.alert('提示消息','删除缓存失败！','warning');
				  			}
				 		}
					})
				}
			})
		}
	</script>
</body>
</html>