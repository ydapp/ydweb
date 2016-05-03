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
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/metro_blue.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/icon.css" />
<script type="text/javascript" src="<%=path%>/public/script/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="<%=path%>/public/script/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=path%>/public/script/easyui-lang-zh_CN.js"></script>

<script type="text/javascript">
	function deleteAttr() {
		$.messager.confirm('删除', '确定删除？', function deleteAttrOperat(res) {
			if (res) {
				var row = $('#tt').datagrid('getSelected');
				$.post('<%=path%>/api/dictRemove.json', {
					dictId : row.dictId
				}, function(result) {
// 					if (result.success) {
// 						$.messager.show({
// 							title : 'Info',
// 							msg : result.msg,
// 							showType : 'fade',
// 							style : {
// 								right : '',
// 								bottom : ''
// 							}
// 						});
						$('#tt').datagrid('reload');
// 					}
				});
			}
		});
	}

	function deleteAttrValue() {
		$.messager.confirm('删除', '确定删除？', function deleteAttrOperat(res) {
			if (res) {
				var row = $('#dt').datagrid('getSelected');
				$.post('<%=path%>/api/dictValueDelete.json', {
					dictId : row.dictId,
					text : row.text
				}, function(result) {
						$('#dt').datagrid('reload');
				});
			}
		});
	}

	function saveAttr() {
		$("#ff").form("submit", {
			url : '<%=path%>/api/dictAdd.json',
			onsubmit : function() {
				return $(this).form("validate");
			},
			success : function(result) {
					$("#dlg").dialog("close");
					$("#tt").datagrid("reload");
// 				}
			}
		});
	}

	function saveAttrValue() {
		$("#ffright").form("submit", {
			url : '<%=path%>/api/dictValueAdd.json',
			onsubmit : function() {
				return $(this).form("validate");
			},
			success : function(result) {
// 				if (result == "1") {
// 					$.messager.alert("提示信息", "操作成功");
					$("#dlgright").dialog("close");
					$("#dt").datagrid("load");
// 				} else {
// 					$.messager.alert("提示信息", "操作失败");
// 				}
			}
		});
	}

	function updateAttr() {
		var row = $('#tt').datagrid('getSelected');
		if (row) {
            $("#dlg_edit").dialog("open").dialog('setTitle', 'Edit');
            $("#ff_edit").form("load", row);
        }		
	}
	
	function updateAttrValue() {
		var row = $('#dt').datagrid('getSelected');
		if (row) {
            $("#dlg_edit_right").dialog("open").dialog('setTitle', 'Edit');
            $("#ff_edit_right").form("load", row);
        }		
	}
	
	function updateAttrOpera() {
		$("#ff_edit").form("submit", {
			url : '<%=path%>/api/dictUpdate.json',
			onsubmit : function() {
				return $(this).form("validate");
			},
			success : function(result) {
					$("#dlg_edit").dialog("close");
					$("#tt").datagrid("reload");
			}
		});
	}
	
	function updateAttrValueOpera() {
		$("#ff_edit_right").form("submit", {
			url : '<%=path%>/api/dictUpdate.json',
			onsubmit : function() {
				return $(this).form("validate");
			},
			success : function(result) {
					$("#dlg_edit_right").dialog("close");
					$("#dt").datagrid("reload");
			}
		});
	}
	
	function doSearch(value) {
		$('#tt').datagrid({
			url : '<%=path%>/api/dictsFindByName.json',
			queryParams: {
				dictName: value
			}
		});
	}
	
	$(function() {
		var row = $('#tt').datagrid('getSelected');
		var index = $('#tt').datagrid('getRowIndex', row);
		
		var options = $('#tt').datagrid('getPager').data("pagination").options;

		$("#tt").datagrid({
			url : '<%=path%>/api/dicts.json',
			pagination : true,
			fit:true,
            pageNumber:1,
            pageSize:15,
            pageList:[10,15,20,30,50],
            nowrap: false,
            striped: true,
            collapsible:true,
            remoteSort: false,
            singleSelect:true,
            rownumbers:true,
			onClickRow : function(index, row) {
				var id = row.dictId;
				$('#dt').datagrid({
					url : '<%=path%>/api/dictValues.json',
					queryParams: {
						dictId: id
					}
				});
			}
		});
	});

</script>
<title>主布局</title>
</head>
<body class="easyui-layout">

	<div region="west" split="true" title="字典列表" style="width: 660px;">
		<!-- 		<form action="javascript:doSearch()"> -->
		<!-- 			字典名称: <input id="dictNameSerarch" class="easyui-textbox" type="text" name="dictNameSerarch" data-options="required:true">   -->
		<!--    			<input type="submit" class="easyui-linkbutton" iconCls="icon-search" value="serarch"> -->
		<!-- 		</form> -->
		<table id="tt" class="easyui-datagrid" style="height: 520px"
			toolbar="#tb"
			data-options="iconCls:'icon-edit',singleSelect:true,idField:'dictId',pagination:true,				
			pageSize:10">
			<thead>
				<tr>
					<th data-options="field:'dictId'" align="center" width="80">ID</th>
					<th data-options="field:'dictName',editor:'text'" width="160"
						align="center">字典名称</th>
					<th data-options="field:'intro',editor:'text'" width="160"
						align="center">字典介绍</th>
					<th data-options="field:'creater',editor:'text'" width="160"
						align="center">创建者</th>
					<th data-options="field:'status',editor:'text'" width="65"
						align="center">状态</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="content" region="center" title="字典值列表">
		<table id="dt" class="easyui-datagrid" style="height: 520px"
			toolbar="#tbright"
			data-options="iconCls:'icon-edit',singleSelect:true">
			<thead>
				<tr>
					<th data-options="field:'dictId',width:85" align="center">字典ID</th>
					<th field='text' width=160 align="center">字典值</th>
					<th field='intro' width=160 align="center">介绍</th>
				</tr>
			</thead>
		</table>
	</div>

	<!--以下是左边增加按钮和删除按钮相关信息，包括左边按钮弹出来的form表单  -->
	<div id="tb">
		<a href="javascript:void(0)" class="easyui-linkbutton"
			iconCls="icon-add" plain="true" onclick="$('#dlg').window('open')">Add</a>
		<a href="javascript:void(0)" class="easyui-linkbutton"
			iconCls="icon-remove" plain="true" onclick="javascript:deleteAttr()">Delete</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="javascript:updateAttr()">Edit</a>
		<input class="easyui-searchbox" data-options="prompt:'输入查找的字典名称',searcher:doSearch" style="width:200px"></input>
	</div>

	<div id="dlg" class="easyui-dialog"
		style="width: 350px; height: 220px; padding: 10px 30px;" title="字典增加"
		buttons="#dlg-buttons" closed="true">
		<h3>增加字典</h3>
		<form id="ff" method="post">
			<table>
				<tr>
					<td>字典名称:</td>
					<td><input type="text" name="dictName" style="width: 200px;" /></td>
				</tr>
				<tr>
					<td>字典说明:</td>
					<td><input type="text" name="intro" style="width: 200px;" /></td>
				</tr>
				<tr>
					<td>创建者:</td>
					<td><input type="text" name="creater" style="width: 200px;" /></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="dlg-buttons">
		<a href="#" class="easyui-linkbutton" iconCls="icon-ok"
			onclick="saveAttr()">Submit</a> <a href="#" class="easyui-linkbutton"
			iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')">Cancel</a>
	</div>

	<div id="dlg_edit" class="easyui-dialog"
		style="width: 350px; height: 220px; padding: 10px 30px;" title="字典修改"
		buttons="#dlg-buttons-edit" closed="true">
		<h3>修改字典</h3>
		<form id="ff_edit" method="post">
			<input type="hidden" name="dictId">
			<table>
				<tr>
					<td>字典名称:</td>
					<td><input type="text" name="dictName" style="width: 200px;" /></td>
				</tr>
				<tr>
					<td>字典说明:</td>
					<td><input type="text" name="intro" style="width: 200px;" /></td>
				</tr>
				<tr>
					<td>创建者:</td>
					<td><input type="text" name="creater" style="width: 200px;" /></td>
				</tr>
				<tr>
					<td>状态:</td>
<!-- 					<td><input type="text" name="status" style="width: 200px;" /></td> -->
					<td><select name="status"> <option value="A">有效</option> 
					<option value="X">禁用</option> </select></td>
					
				</tr>
			</table>
		</form>
	</div>
	<div id="dlg-buttons-edit">
		<a href="#" class="easyui-linkbutton" iconCls="icon-ok"
			onclick="updateAttrOpera()">Submit</a> <a href="#" class="easyui-linkbutton"
			iconCls="icon-cancel" onclick="javascript:$('#dlg_edit').dialog('close')">Cancel</a>
	</div>
	
	<!--以下是右边增加和删除按钮相关信息  -->
	<div id="tbright">
		<a href="javascript:void(0)" class="easyui-linkbutton"
			iconCls="icon-add" plain="true"
			onclick="$('#dlgright').window('open')">Add</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="javascript:updateAttrValue()">Edit</a>
			<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteAttrValue()">Delete</a>
	</div>

	<div id="dlgright" class="easyui-dialog"
		style="width: 350px; height: 240px; padding: 10px 30px;" title="字典增加"
		buttons="#dlg-buttons-right" closed="true">
		<h3>增加字典值</h3>
		<form id="ffright" method="post">
			<table>
				<tr>
					<td>字典ID:</td>
					<td><input type="text" name="dictId" style="width: 200px;" /></td>
				</tr>
				<tr>
					<td>字典值:</td>
					<td><input type="text" name="value" style="width: 200px;" /></td>
				</tr>
				<tr>
					<td>字典值文本:</td>
					<td><input type="text" name="text" style="width: 200px;" /></td>
				</tr>
				<tr>
					<td>说明:</td>
					<td><input type="text" name="intro" style="width: 200px;" /></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="dlg-buttons-right">
		<a href="#" class="easyui-linkbutton" iconCls="icon-ok"
			onclick="saveAttrValue()">Submit</a> <a href="#"
			class="easyui-linkbutton" iconCls="icon-cancel"
			onclick="javascript:$('#dlgright').dialog('close')">Cancel</a>
	</div>

	<div id="dlg_edit_right" class="easyui-dialog"
		style="width: 350px; height: 170px; padding: 10px 30px;" title="字典修改"
		buttons="#dlg-buttons-edit-right" closed="true">
		<h3>修改字典值</h3>
		<form id="ff_edit_right" method="post">
			<input type="hidden" name="dictId">
			<input type="hidden" name="value">
			<table>
				<tr>
					<td>字典值:</td>
					<td><input type="text" name="text" style="width: 200px;" /></td>
				</tr>
				<tr>
					<td>介绍:</td>
					<td><input type="text" name="intro" style="width: 200px;" /></td>
				</tr>
			</table>
		</form>
	</div>
	<div id="dlg-buttons-edit-right">
		<a href="#" class="easyui-linkbutton" iconCls="icon-ok"
			onclick="updateAttrValueOpera()">Submit</a> <a href="#" class="easyui-linkbutton"
			iconCls="icon-cancel" onclick="javascript:$('#dlg_edit_right').dialog('close')">Cancel</a>
	</div>
</body>
</html>