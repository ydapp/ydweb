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
<meta name="apple-mobile-web-app-title" content="资讯管理" />
<title>资讯信息管理界面</title>
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
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" id="add">新增文章</a>
		</div>
		<table id="article_grid" class="easyui-datagrid" title="资讯信息列表" 
		data-options="singleSelect:true,url:'<%=path%>/admin/articles.json',method:'get',toolbar:'#tb',rownumbers:'true',pagination:'true',fit:'true',fitColumns: true">
			<thead>
				<tr>
					<th data-options="field:'tile',width:180">标题</th>
					<th data-options="field:'pubTime',width:180,fixed:true">发布时间</th>
				</tr>
			</thead>
		</table>
	</div>
	<!-- 弹出窗口 -->
	<div id="mydialog" title="新增资讯" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<form id="add_form" action="<%=path%>/admin/article/add.json" method="post" enctype="multipart/form-data">
					<div class="fitem">
						<font color="red">*</font><label>文章标题：</label> <input
							name="tile" class="easyui-textbox" style="width: 452px;">
					</div>
					<div class="fitem">
						<font color="red">*</font><label>封面图片：</label> <input
							name="cover" class="easyui-filebox" style="width: 452px;" data-options="prompt:'选择一张图片'">
					</div>
					<script id="editor" type="text/plain" style="width:900px;height:200px;z-index:999"></script>
				</form>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" href="javascript:void(0)" onclick="saveArticle();" style="width:80px;">提交</a>
				<a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="javascript:$('#mydialog').dialog('close');" style="width:80px">关闭</a>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript" src="<%=path%>/public/script/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="<%=path%>/public/script/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=path%>/public/script/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">var _basePath ="<%=path%>"; </script>
<script type="text/javascript" src="<%=path%>/public/baiduueditor/ueditor.config.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=path%>/public/baiduueditor/ueditor.all.min.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=path%>/public/baiduueditor/lang/zh-cn/zh-cn.js" charset="utf-8"></script>
<script type="text/javascript">
	$('#add').click(function(){
		 $('#mydialog').dialog('open');
		 //$('#add_form').form('clear');
		 UE.getEditor('editor').setContent('');
	});
	var saveArticle = function(){
		$('#add_form').form('submit', {    
		    success:function(data){
		    	var data = eval('(' + data + ')');
		    	if(data.success){
		        	$.messager.alert('温馨提示','新增成功');
		        	$('#article_grid').datagrid('reload');
		        	$('#mydialog').dialog('close');
		        }else{
		        	$.messager.alert('温馨提示',data.message);
		        }
		    }    
		});
	}
</script>
</html>