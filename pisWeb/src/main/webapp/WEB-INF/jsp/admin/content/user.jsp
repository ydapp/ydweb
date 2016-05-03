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
<meta name="apple-mobile-web-app-title" content="用户信息" />
<title>用户信息管理页面</title>
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
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" id="add">增加用户</a>
		</div>
		<table id="user_grid" class="easyui-datagrid" title="用户列表" 
		data-options="singleSelect:true,url:'<%=path%>//api/userInfos.json',method:'get',toolbar:'#tb',rownumbers:'true',pagination:'true',fit:'true',fitColumns: true">
			<thead>
				<tr>
					<th data-options="field:'nick',width:180">用户</th>
					<th data-options="field:'tel',width:180">电话</th>
					<th data-options="field:'groupTypeTitle',width:180">类型</th>
					<th data-options="field:'brokingFirm',width:180">所属经纪公司</th>
					<th data-options="field:'building',width:180">所属楼盘</th>
				</tr>
			</thead>
		</table>
	</div>
	<!-- 弹出窗口 -->
	<div id="mydialog" title="新增加用于" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<form id="add_form">
					<div class="fitem">
						
						<font color="red">*</font><label>电话：</label> <input
							id="tel" class="easyui-textbox" style="width: 452px;"></p>
							<font color="red">*</font><label>名称：</label> <input
							id="nick" class="easyui-textbox" style="width: 452px;"></p>
						<font color="red">*</font><label>类型：</label> 
						<select id="groupType">
							<option value="appAdmin">APP管理员</option>
							<option value="commissioner">案场专员</option>
							<option value="brokingFirm">经纪公司</option>
							<option value="salesman">业务员</option>
						</select></p>
						<div id="brokingFirmDiv" style="display: none;">
							<label>经纪公司:</label><input id="brokingFirmList"></input>
						</div>
						<div id="buildingDiv" style="display: none;">
							<label>城市:</label><select id="cityList"> </select>
							<label>楼盘:</label><select id="buildingList"></select>
						</div>
					</div>
				</form>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" href="javascript:void(0)" onclick="saveUser();" style="width:80px;">提交</a>
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
		 /**
		     *如果当前是管理员，可以增加APP管理员
		     *如果当前是APP管理员，可以增加经纪公司和报备专员
		     *如果当前是经纪公司，可以增加业务员
		 */
		 if (currentUserModel.nick=="admin"){
			 $("#groupType").empty();
			 $("#groupType").append("<option value='appAdmin'>APP管理员</option>");
			 $("#brokingFirmDiv").hide();
			 $("#buildingDiv").hide();
			 return;
		 }
		 //app管理员，可以添加经纪公司和报备员
		 if (currentUserModel.groupType=='appAdmin'){
			 $("#groupType").empty();
			 $("#groupType").append("<option value='commissioner'>案场专员</option>");
			 $("#groupType").append("<option value='brokingFirm'>经纪公司</option>");
			 //主动出发一次组选择变化
			 $("#groupType").change();
			 return;
		 }
		//经纪公司，可以添加业务员
		 if (currentUserModel.groupType=='brokingFirm'){
			 $("#groupType").empty();
			 $("#groupType").append("<option value='salesman'>业务员</option>");
			 $('#brokingFirmList').val(currentUserModel.brokingFirm);
			 $('#brokingFirmList').attr("readonly","readonly");
			 $("#brokingFirmDiv").show();
			 $("#buildingDiv").hide();
			 return;
		 }
		 
	});
	//用户组类型值改变
	 $("#groupType").change(function(e){
		 if ("commissioner" == $("#groupType").val()){
		 	$("#buildingDiv").show();
		 	$("#brokingFirmDiv").hide();
		 	//加载城市列表
		 	loadCityList();
		 } else if ("brokingFirm" == $("#groupType").val()){
			 $("#buildingDiv").hide();
		 	$("#brokingFirmDiv").show();
		 }
		 
	 });
	//城市选型发生变化
	 $("#cityList").change(function(e){
		 loadBuildingList($("#cityList").val());
	 });
	var loadCityList = function (){
		$.ajax({
			url: "<%=path%>/api/getCitys.json",
			dataType: "json",
			cache : false,
			success: function(data){
				console.log("citysdata", data);
				if(data && data.success == true){
					if(data.total > 0){
						$("#cityList").empty();
						for (var i = 0; i < data.rows.length; i++){
							$("#cityList").append("<option value='" + data.rows[i].cityId + "'>" + data.rows[i].cityName + "</option>");
						}
						$("#cityList").change();
					}
				}else {
					alert("获取城市列表失败");
				}
			},
			error: function(){
				alert("发生异常");
			}
		});
	}
	var loadBuildingList = function (cityId){
		$.ajax({
			url: "<%=path%>/api/buildings/cityId/" + cityId + ".json",
			dataType: "json",
			cache : false,
			success: function(data){
				console.log("buildingsdata", data);
				if(data && data.success == true){
					if(data.result){
						$("#buildingList").empty();
						for (var i = 0; i < data.result.length; i++){
							$("#buildingList").append("<option value='" + data.result[i].buildingId + "'>" + data.result[i].buildingName + "</option>");
						}
					}
				}else {
					alert("获取城市列表失败");
				}
			},
			error: function(){
				alert("发生异常");
			}
		});
	}
	var saveUser = function(){
		console.log("form:", $('#add_form'));
		$.restPost({
			  url: '/pisWeb/api/userInfo.json',
			  data:{
				  tel:$('#tel').val(),
				  nick:$('#nick').val(),
				  groupType:$('#groupType').val(),
				  brokingFirm:$('#brokingFirmList').val(),
				  building:$('#buildingList').val()
			  },
			  success: function(data){
				  if (data.success){
				  $.messager.alert('温馨提示','新增成功');
		        	$('#user_grid').datagrid('reload');
		        	$('#mydialog').dialog('close');
				  } else {
					  $.messager.alert('错误',data.message);
				  }
			  }
			});
	}
	var currentUserModel;
	$.ajax({
		url: "<%=path%>/api/currentUserModel.json",
		dataType: "json",
		cache : false,
		success: function(data){
			if(data && data.success === true){
				if(data.result){
					console.log("data", data.result);
					currentUserModel = data.result;
				}
			}else {
				alert("获取当前登陆用户失败");
			}
		},
		error: function(){
			alert("发生异常");
		}
	});
</script>
</html>