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
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" id="add">新增咨询信息</a>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true" id="update">修改咨询信息</a>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true" id="remove">删除咨询信息</a>
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
	<!-- 弹出窗口  新增-->
	<div id="mydialog" title="新增资讯" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<form id="add_form" action="<%=path%>/admin/article/add.json" method="post" enctype="multipart/form-data">
					<div class="fitem">
						<font color="red">*</font><label>文章标题：</label> <input id="tile" name="tile" validType="maxLen['#tile',200]"  required="required" class="easyui-textbox" style="width: 452px;">
					</div>
					<div class="fitem">
						<font color="red">*</font><label>封面图片：</label> <input id="cover"
							name="cover"  required="required" class="easyui-filebox" style="width: 452px;" data-options="prompt:'选择一张图片'">
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
	<!-- 弹出窗口  修改-->
	<div id="mydialogUpdate" title="修改资讯" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<form id="update_form" action="<%=path%>/admin/article/update.json" method="post" enctype="multipart/form-data">
					<input id="id_update" name="id" type="hidden">
					<input id="cover_update" name="cover" type="hidden">
					<div class="fitem">
						<font color="red">*</font><label>文章标题：</label><input id="tile_update" name="tile" validType="maxLen['#tile_update',200]" class="easyui-textbox"required="required" style="width: 452px;">
					</div>
					<div class="fitem">
						<label>封面图片：</label><input name="cover" class="easyui-filebox" style="width: 452px;" data-options="prompt:'选择一张图片'">
					</div>
					<script id="editor_update" type="text/plain" style="width:900px;height:200px;z-index:999"></script>
				</form>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" href="javascript:void(0)" onclick="saveArticle_update();" style="width:80px;">提交</a>
				<a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="javascript:$('#mydialogUpdate').dialog('close');" style="width:80px">关闭</a>
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
	/**
	 * 提交
	  */
	var saveArticle = function(){
		var tile=$('#tile').textbox("isValid");
		var cover=$('#cover').filebox("isValid");
		if(tile&&cover){
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
	}
	
	/**
	  *打开修改页面 
	  */
	  $("#update").click(function(){
		  //判断是否选中行
		  var row = $('#article_grid').datagrid('getSelected');
		  if(null==row){
			 $.messager.alert('温馨提示',"请选中需要修改的咨询信息!");
			 return;
			}
		  //根据主键ID从库中提取数据 
		  $.ajax({
			  url:'<%=path%>/api/article/'+row.id+'.json',
			  dataType: "json",
			  cache : false,
			  success:function(data){
				  //判断数据不为空
				  if(null!=data&&null!=data.pisArticleVo){
					  //获取数据
					  var article = data.pisArticleVo;
					  //赋值 
					  $("#cover_update").val(article.cover);
					  $("#id_update").val(article.id);
					  $("#tile_update").textbox('setValue',article.tile);
					  UE.getEditor('editor_update').setContent(article.content);
				  }
			  }
		  });
		  //打开修改页面
		  $('#mydialogUpdate').dialog('open');
		  UE.getEditor('editor_update');
	  });
	/**
	 * 执行修改操作
	 */
	var saveArticle_update=function(){
		var tile_update=$('#tile_update').textbox("isValid");
		if(tile_update){
			$("#update_form").form('submit',{
				 success:function(data){
					 var data = eval('(' + data + ')');
				    	if(data.success){
				        	$.messager.alert('温馨提示',data.message);
				        	$('#article_grid').datagrid('reload');
				        	$('#mydialogUpdate').dialog('close');
				        }else{
				        	$.messager.alert('温馨提示',data.message);
				        }
				 }
			});
		}
	};
	/**
	 *执行删除操作
	 */
	$("#remove").click(function(){
		//判断是否选中行
		  var row = $('#article_grid').datagrid('getSelected');
		  if(null==row){
			 $.messager.alert('温馨提示',"请选中需要删除的咨询信息!");
			 return;
		  }
		  $.messager.confirm("操作提示", "您确定要执行删除操作吗？", function (data) { 
			  if (data){ 
				  $.ajax({
						 url:'<%=path%>/admin/article/remove.json',
						 dataType: "json",
						 data : 'id=' + row.id+'&cover='+row.cover,
					     cache : false,
					     success:function(data){
					    	if(null!=data){
					    		if(true==data.success){
					    			$('#article_grid').datagrid('reload');
			    					$.messager.alert('提示信息','操作成功!');
					    		}else{
					    			$('#article_grid').datagrid('reload');
			    					$.messager.alert('提示信息','操作失败!');
					    		}
					    	} 
					     }
					 });				  
			  }
		  });
	});
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