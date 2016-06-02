<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" session="false"%>
<%
	String path = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="renderer" content="webkit">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<!-- Add to homescreen for Chrome on Android -->
<meta name="mobile-web-app-capable" content="yes">
<!-- Add to homescreen for Safari on iOS -->
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="apple-mobile-web-app-title" content="城市信息" />
<title>城市信息管理页面</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/metro_blue.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/icon.css" />
<style type="text/css">
.fitem{
	height: 36px;
	line-height: 36px;
}
</style>
</head>
<body class="easyui-layout">
	<div data-options="region:'center',border:false" style="padding:5px 5px 5px 5px;" >
	    <!-- 表格头部标签 -->
		<div id="tb" style="padding:2px 5px;text-align:right">
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" id="add">新增城市</a>
		</div>
		<table id="city_grid" class="easyui-datagrid" title="城市列表" 
		data-options="singleSelect:true,url:'<%=path%>/api/getCitys/null.json',method:'get',toolbar:'#tb',rownumbers:'true',pagination:'true',fit:'true',fitColumns: true">
			<thead>
				<tr>
					<th data-options="field:'cityName',width:180">城市名称</th>
				</tr>
			</thead>
		</table>
	</div>
	<!-- 弹出窗口 -->
	<div id="mydialog" title="新增城市" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<form id="add_form">
					<div class="fitem">
						<font color="red">*</font><label>城市：</label> <input	id="cityName" validType="maxLen['#cityName',255]" required="required" class="easyui-textbox" style="width: 452px;">
					</div>
				</form>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" href="javascript:void(0)" onclick="saveCity();" style="width:80px;">提交</a>
				<a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="javascript:$('#mydialog').dialog('close');" style="width:80px">关闭</a>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript" src="<%=path%>/public/script/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="<%=path%>/public/script/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=path%>/public/script/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=path%>/public/script/jquery-rest.js"></script>
<script type="text/javascript">var _basePath ="<%=path%>"; </script>
<script type="text/javascript" src="<%=path%>/public/baiduueditor/ueditor.config.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=path%>/public/baiduueditor/ueditor.all.min.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=path%>/public/baiduueditor/lang/zh-cn/zh-cn.js" charset="utf-8"></script>
<script type="text/javascript">
	$('#add').click(function(){
		 $('#mydialog').dialog('open');
		 //$('#add_form').form('clear');
		 //UE.getEditor('editor').setContent('');
	});
	var saveCity = function(){
		var cityName=$('#cityName').textbox("isValid");
		if(cityName){
			console.log("form:", $('#add_form'));
			$.restPost({
				  url: '/pisWeb/api/addCity.json',
				  data:{cityName:$('#cityName').val()},
				  success: function(data){
					  $.messager.alert('温馨提示','新增成功');
			        	$('#city_grid').datagrid('reload');
			        	$('#mydialog').dialog('close');
				  }
				});
		}
	}
	$.extend($.fn.validatebox.defaults.rules,{
		maxLen:{
			validator:function(value,arrays){
				if(""!=value){
					if(value.length>arrays[1]){
						return false;
					}
					return true;
				}
			},
			message : "输入内容超过最大长度!"
		}
	});
</script>
</html>