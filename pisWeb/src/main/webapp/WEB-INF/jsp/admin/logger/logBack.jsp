<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
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
<meta name="apple-mobile-web-app-title" content="日志信息" />
<title>日志信息</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/metro_blue.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/icon.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/public/syntaxhighlighter/styles/shCoreDefault.css" />
<style type="text/css">
.datagrid-btable .datagrid-cell {
	line-height: 18px;
	white-space: nowrap;
	text-overflow: ellipsis;
	overflow: hidden;
}
.log_view{
	color: blue;
	cursor: pointer;
	text-decoration : underline;
}
#log_cotnent .syntaxhighlighter{
	overflow:visible !important;
}
#log_cotnent .syntaxhighlighter .toolbar{
	display: none;
}
</style>
</head>

<body class="easyui-layout">
	<div data-options="region:'north',collapsible:false,border:false,split:true" style="height:123px; padding:5px 5px 0;">
		<div class="easyui-panel" data-options="fit:true,title:'日志信息查询',iconCls:'title-icon'">
			<form id="mysearch" method="post" class="search-form">
				<table>
					<tr>
						<td>日志级别：<select name="logLevel" class="easyui-combobox" style="width: 166px;" data-options="panelHeight:'auto',editable:false" > 
								<option value="">请选择</option>
								<option value="DEBUG">DEBUG</option>
								<option value="INFO">INFO</option>
								<option value="WARN">WARN</option>
								<option value="ERROR">ERROR</option>
								<option value="FATAL">FATAL</option>
							</select>
						</td>
						<td>日志说明：<input type="text" name="comments" class="easyui-textbox" style="width: 160px;"/>
						</td>
						<td>发生位置：<input type="text" name="genPos" class="easyui-textbox" style="width: 160px;"/>
						</td>
						<td></td>
					</tr>
					<tr>
						<td>开始日期：<input style="width: 166px;" class="easyui-datetimebox"
							editable="false" type="text" name="startTime" />
						</td>
						<td>结束时间：<input style="width: 166px;" class="easyui-datetimebox"
							editable="false" type="text" name="endTime" />
						</td>
						<td>
							<div style="width:226px;text-align: right;">
								<a id="searchbtn" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
								<a id="clearbtn" class="easyui-linkbutton" data-options="iconCls:'icon-reload'">重置</a>
							</div>
						</td>
						<td></td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<div data-options="region:'center',border:false" style="padding:0 5px 5px 5px;" >
		<div id="dlg-toolbar" style="padding:2px 0;text-align: right">
			<table style="width:100%">
				<tr>
					<td style="padding-left:2px">
						<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" id="stndel">删除</a>
						<a href="#" class="easyui-linkbutton" iconCls="icon-cancel" plain="true" id="stndela">全部删除</a>
					</td>
				</tr>
			</table>
		</div>
		<table id="sys-log-grid"></table>
	</div>
	<!-- 弹出窗口 -->
	<div id="mydialog" title="日志详情" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<div id="log_cotnent"></div>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="javascript:$('#mydialog').dialog('close');" style="width:80px">关闭</a>
			</div>
		</div>
	</div>
<script type="text/javascript"
	src="<%=path%>/public/script/jquery-1.10.2.min.js"></script>
<script type="text/javascript"
	src="<%=path%>/public/script/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="<%=path%>/public/script/easyui-lang-zh_CN.js"></script>
<script type="text/javascript"
	src="<%=path%>/public/syntaxhighlighter/scripts/shCore.js"></script>
<script type="text/javascript"
	src="<%=path%>/public/syntaxhighlighter/scripts/shBrushJava.js"></script>
	<script type="text/javascript">

		function logsel(rowData){
			var logCotnent = rowData.logCotnent;
			if(logCotnent && logCotnent.replace){
				logCotnent = logCotnent.replace(/\t/g, "\r\n\t");
			}
			var message = [ '<pre class="brush: java" >',rowData.createDate, '&nbsp;[',rowData.threadName,']&nbsp;',
					rowData.logLevel,"&nbsp;", rowData.genPos, '\r\n\t&nbsp;-&nbsp;', rowData.comments, '\r\n', logCotnent, '</pre>' ]
					.join('');
			
			$("#log_cotnent").html(message);
			$('#mydialog').dialog('open');
			SyntaxHighlighter.highlight();
		};
		$(function() {

			var _formatter = function(value, row, index) {
				if (value) {
					return '<span title="' + value + '">' + value + '</span>';
				} else {
					return "--";
				}
			}
			/**
			 *	初始化数据表格  
			 */
			$('#sys-log-grid').datagrid({
				fit : true,
				fitColumns : true,
				singleSelect : true,
				toolbar : "#dlg-toolbar",
				url : '<%=path%>/admin/logList.json',
				rownumbers : true,
				columns : [ [{
					fixed : true,
					field : 'createDate',
					title : '时间',
					width : 130
				}, {
					fixed : true,
					field : 'threadName',
					title : '线程名称',
					width : 180
				}, {
					fixed : true,
					field : 'logLevel',
					title : '日志级别',
					width : 60
				}, {
					field : 'genPos',
					title : '发生位置',
					width : 100,
					formatter : _formatter
				}, {
					field : 'comments',
					title : '日志说明',
					width : 100,
					formatter : _formatter
				}, {
					field : 'logCotnent',
					title : '异常信息',
					width : 100,
					formatter : function(value, row, index) {
						return value?value:"--";
					}
				}, {
					fixed : true,
					field : 'op',
					title : '查看详情',
					width : 60,
					align : 'center',
					formatter : function(value, row, index) {
						return '<span class="log_view">查看</span>';
					}
				} ] ],
				onClickCell : function(rowIndex, field, value){
					if(field == 'op'){
						var data = $(this).datagrid('getData');
						var rowData = data.rows[rowIndex];
						logsel(rowData);
					}
				},
				onDblClickRow: function(rowIndex, rowData){
					logsel(rowData);
				},
				pagination : true,
				pageSize : 10,
				pageList : [ 10, 15, 20, 30, 40 ]
			});
			
			$('#clearbtn').click(function() {
				$('#mysearch').form('clear');
			});
			$('#searchbtn').click(function() {
				$('#sys-log-grid').datagrid('load', serializeForm($('#mysearch')));
			});
			
			$('#stndel').click(function(){
				var arr =$('#sys-log-grid').datagrid('getSelections');
				if(arr.length>0){
				$.messager.confirm('提示信息' , '确认删除?' , function(r){
					if(r){
						var sysLogIds = '';
						for(var i= 0;i<arr.length;i++){
							sysLogIds += arr[i].sysLogId + ',' ;
						}
						sysLogIds = sysLogIds.substring(0 , sysLogIds.length-1);
						$.ajax({
							type:'post' , 
							url : '<%=path%>/admin/delBatchLog.json' ,
							cache:false ,
							async: false ,		//同步请求
							data:{sysLogIds:sysLogIds},
							success:function(result){
								$('#sys-log-grid').datagrid('reload');
							}
						});
					}
				})
				}else{
					$.messager.alert('提示信息','至少选择一行记录进行删除！','warning');
				}
			});
			$('#stndela').click(function(){
				$.messager.confirm('提示信息' , '确认删除所有日志吗?' , function(r){
					if(r){
						$.ajax({
							type:'post' , 
							url : '<%=path%>/admin/delAllLog.json' ,
							cache:false ,
							async: false ,		//同步请求
							success:function(result){
								$('#sys-log-grid').datagrid('load');
							}
						});
					}
				})
			});
		});
		//js方法：序列化表单 			
		function serializeForm(form) {
			var obj = {};
			$.each(form.serializeArray(), function(index) {
				if (obj[this['name']]) {
					obj[this['name']] = obj[this['name']] + ',' + this['value'];
				} else {
					obj[this['name']] = this['value'];
				}
			});
			return obj;
		}
	</script>
</body>
</html>