<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String path = request.getContextPath();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统参数页面</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/metro_blue.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/icon.css" />
</head>
<body class="easyui-layout">
	<div data-options="region:'center',border:false" style="padding:5px;" >
	<table class="easyui-datagrid" title="系统参数"
		data-options="url:'<%=path%>/admin/systemParams.json',fitColumns:true,rownumbers:true,fit:true,singleSelect:true,iconCls:'title-icon'">
		<thead>
			<tr>
				<th data-options="field:'paramName',width:100">PARAM_NAME</th>
				<th data-options="field:'mask',width:100">MASK</th>
				<th data-options="field:'currentValue',width:100">CURRENT_VALUE</th>
				<th data-options="field:'comments',width:100">COMMENTS</th>
				<th data-options="field:'cz',width:40,align:'center',formatter:function(value, row, index){return '<a href=javascript:update()>修改<a/>'}">操作</th>
			</tr>
		</thead>
	</table>
	</div>
	<div id="sysdialog" style="width: 500px;height: 340px;"title="修改窗口" class="easyui-dialog" 
		data-options="iconCls:'icon-save',closable:false,
          collapsible:false,minimizable:false,resizable:true,closed:true,maximized:false,
         buttons:[{
				text:'确定',
				handler:function(){
				 save();
				}
			},{
				text:'取消',
				handler:function(){
				center()
				}
			}]" >
		<form id="sysform">
			<table width="100%" height="100%" cellspacing="10px;">
				<tr>
					<td style="text-align: right;width: 120px;">
						PARAM_NAME
					</td>
					<td>
						<input type="text" name="paramName" style="height: 30px;width: 250px;" class="easyui-textbox" disabled="disabled"></input>
					</td>
				</tr>
				<tr>
					<td style="text-align: right;">
						MASK
					</td>
					<td>
						<input type="text" id="mask" name="mask" style="height: 30px;width: 250px;" class="easyui-textbox" disabled="disabled"></input>
					</td>
				</tr>
				<tr>
					<td style="text-align: right;">
						CURRENT_VALUE
					</td>
					<td>
						<input type="text" id="currentValue" name="currentValue" style="height: 30px;width: 250px;" class="easyui-textbox"></input>
					</td>
				</tr>
				<tr>
					<td style="text-align: right;">
						COMMENTS
					</td>
					<td>
						<textarea  type="text" id="comments" name="comments" style="height: 75px;width: 250px;" name="comments" class="easyui-textbox" data-options="multiline:true" ></textarea>
					</td>
				</tr>
				<tr>
					<td colspan="2" height="25px;"></td>
				</tr>
			</table>
		</form>
	</div>  
	<script type="text/javascript"
		src="<%=path%>/public/script/jquery-1.10.2.min.js"></script>
	<script type="text/javascript"
		src="<%=path%>/public/script/jquery.easyui.min.js"></script>
	<script type="text/javascript"
		src="<%=path%>/public/script/easyui-lang-zh_CN.js"></script>
	<script type="text/javascript">
		function update(){
		var row = $(".easyui-datagrid").datagrid("getSelected");
		$("#sysform").form('load',{
			paramName:row.paramName,
			mask:row.mask,
			currentValue:row.currentValue,
			comments:row.comments
		});
		$("#sysdialog").dialog("open");
		}
		function save(){
			var currentValue = $("#currentValue").val();
			var comments = $("#comments").val();
			var mask = $("#mask").val();
			$.ajax({
				type:'POST',
				url:'<%=path%>/admin/updataSystemParams.json',
				data:{currentValue:currentValue,comments:comments,mask:mask},
				dataType:'json',
				success:function(data){
					if(data.success == false){
						$.messager.alert('提示信息',data.message,'warning');
					}else{
						$("#sysdialog").dialog("close");
						$(".easyui-datagrid").datagrid('reload');
					}
				}
				
			});
			
		}
		function center(){
			$("#sysdialog").dialog("close");
		}
	</script>
</body>
</html>