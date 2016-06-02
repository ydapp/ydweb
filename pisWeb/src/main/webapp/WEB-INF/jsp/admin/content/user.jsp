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
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true" id="update">修改用户</a>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true" id="remove">删除用户</a>
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
	<!-- 弹出窗口,新增 -->
	<div id="mydialog" title="新增加用于" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<form id="add_form">
					<div class="fitem">
						
						<font color="red">*</font><label>电话：</label> <input id="tel" validType="maxLen['#tel',20]"  required="required" class="easyui-textbox" style="width: 452px;"></p>
							<font color="red">*</font><label>名称：</label><input id="nick" validType="maxLen['#nick',20]" required="required" class="easyui-textbox" style="width: 452px;"></p>
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
	<!-- 弹出窗口 修改-->
	<div id="mydialogUpdate" title="修改用户" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<form id="update_Form">
					<input id="groupId_update" type="hidden">
					<input id="userId_update" type="hidden">
					<div class="fitem">
						<font color="red">*</font><label>电话：</label> <input id="tel_update" class="easyui-textbox" validType="maxLen['#tel_update',20]" required="required" style="width: 452px;"></p>
						<font color="red">*</font><label>名称：</label><input  id="nick_update" class="easyui-textbox" validType="maxLen['#nick_update',20]" required="required" style="width: 452px;"></p>
						<font color="red">*</font><label>类型：</label> 
						<select id="groupType_update">
							<option value="appAdmin" >APP管理员</option>
							<option value="commissioner">案场专员</option>
							<option value="brokingFirm" >经纪公司</option>
							<option value="salesman">业务员</option>
						</select></p>
						<div id="brokingFirmDiv_update" style="display: none;">
							<label>经纪公司:</label><input id="brokingFirmList_update"></input>
						</div>
						<div id="buildingDiv_update" style="display: none;">
							<label>城市:</label><select id="cityList_update"> </select>
							<label>楼盘:</label><select id="buildingList_update"></select>
						</div>
					</div>
				</form>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" href="javascript:void(0)" onclick="updateUser();" style="width:80px;">提交</a>
				<a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="javascript:$('#mydialogUpdate').dialog('close');" style="width:80px">关闭</a>
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
	/**
	 *初始化
	 */
	 var adminId="f77e0b96-1b88-4f58-a818-604fd33c61e1";
	$(function(){
		//验证内置管理下，是否有app管理，如果有则隐藏新增按钮
		if("${CURRENT_USER.userId}"==adminId){
			$.ajax({
				url:'<%=path%>/api/vaildAppManager.json',
				dataType: "json",
				cache : false,
				success: function(data){
					if(!data.success){
						$("#add").hide();
					}
				}
			});
		}else{
			//判断登录用户的类型来确定是否展示用户新增按钮
			$.ajax({
				url:'<%=path%>/api/loginUserType.json',
				dataTpe:'json',
				cache:false,
				success:function(data){
					if(!data.success){
						$("#add").hide();
					}
				}
				
			});
		}
	});
	/**
	 *执行新增用户操作
	 */
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
			 $("#groupType").append("<option value='channelManager'>渠道经理</option>");
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
	/**
	 *打开修改用户页面
	 */
	$("#update").click(function(){
		var row = $('#user_grid').datagrid('getSelected');
		if(null==row){
			$.messager.alert('温馨提示',"请选中需要修改的用户!");
			return;
		}
		if("A"==row.type){
			$.messager.alert('温馨提示',"无法修改内置管理员!");
			return;
		}
		$.ajax({
			url:'<%=path%>/api/getUserByUserId.json',
			dataType: "json",
			data : 'userId=' + row.userId,
			cache : false,
			success: function(data){
				if(null!=data){
					  $('#mydialogUpdate').dialog("open");
					  //修改窗口赋值 
					  $("#tel_update").textbox('setValue',data.tel);
					  $("#nick_update").textbox('setValue',data.nick);
					  $("#groupId_update").val(data.groupId);
					  $("#userId_update").val(data.userId);
					  
					  //app管理员修改
					  if("appAdmin"==data.groupType){
						 $("#groupType_update").empty();
						 $("#groupType_update").append("<option value='appAdmin'>APP管理员</option>");
						 $("#brokingFirmDiv_update").hide();
						 $("#buildingDiv_update").hide();
						 return;	
					  }
					  //经纪公司
				 	  if("brokingFirm"==data.groupType){
						  $("#groupType_update").empty();
						  $("#groupType_update").append("<option value='commissioner'  >案场专员</option>");
						  $("#groupType_update").append("<option value='brokingFirm' selected='selected'>经纪公司</option>");
						  $("#groupType_update").append("<option value='channelManager'>渠道经理</option>");
						  $("#brokingFirmList_update").val(data.brokingFirm)
						  //主动出发一次组选择变化
						  $("#groupType_update").change();
						  return;
					  	}
				 		//渠道经理
				 	 	if("channelManager"==data.groupType){
						  $("#groupType_update").empty();
						  $("#groupType_update").append("<option value='commissioner'  >案场专员</option>");
						  $("#groupType_update").append("<option value='brokingFirm' >经纪公司</option>");
						  $("#groupType_update").append("<option value='channelManager' selected='selected'>渠道经理</option>");
						  $("#brokingFirmList_update").val(data.brokingFirm)
						  //主动出发一次组选择变化
						  $("#groupType_update").change();
						  return;
					  	 }
					  //报备人
					  if("commissioner"==data.groupType){
						  $("#groupType_update").empty();
						  $("#groupType_update").append("<option value='commissioner'selected='selected'  >案场专员</option>");
						  $("#groupType_update").append("<option value='brokingFirm' >经纪公司</option>");
						  $("#groupType_update").append("<option value='channelManager'>渠道经理</option>");
						  //主动出发一次组选择变化
						  $("#groupType_update").change();
						  $("#buildingList_update").find("option[value='"+data.building+"']").attr("selected","selected");
						  setTimeout("$('#buildingList_update').find('option[value="+data.building+"]').attr('selected','selected');",600)
						  return;
					  }
					  //业务员
					  if("salesman"==data.groupType){
						  $("#groupType_update").empty();
						  $("#groupType_update").append("<option value='salesman'>业务员</option>");
						  $('#brokingFirmList_update').val(data.brokingFirm);
						  $("#brokingFirmDiv_update").show();
						  $("#buildingDiv_update").hide();
						  return;
					  }
					 
				}
			}
		});
	});
	/**
	 * 执行修改用户操作 
	 */
	var updateUser=function(){
		var tel_update=$('#tel_update').textbox("isValid");
		var nick_update=$("#nick_update").textbox("isValid");
		var groupType_update = $("#groupType_update").val();
		if(tel_update&&nick_update&&""!=groupType_update){
			console.log("form:", $('#update_Form'));
			$.restPost({
				  url: '<%=path%>/api/updateUserByUserId.json',
				  data:{
					  tel:$('#tel_update').val(),
					  nick:$('#nick_update').val(),
					  groupType:$('#groupType_update').val(),
					  brokingFirm:$('#brokingFirmList_update').val(),
					  building:$('#buildingList_update').val(),
					  groupId:$("#groupId_update").val(),
					  userId:$("#userId_update").val()
				  },
				  success: function(data){
					  if (data.success){
						  	$.messager.alert('温馨提示',data.message);
				        	$('#user_grid').datagrid('reload');
				        	$('#mydialogUpdate').dialog('close');
						  } else {
							  $.messager.alert('错误',data.message);
						  }
				  }
				});
		}
	};
	//城市选型发生变化
	 $("#cityList_update").change(function(e){
		 loadBuildingList($("#cityList_update").val(),"buildingList_update");
	 });
	//用户组类型值改变
	 $("#groupType_update").change(function(e){
		 if ("commissioner" == $("#groupType_update").val()){
		 	$("#buildingDiv_update").show();
		 	$("#brokingFirmDiv_update").hide();
		 	//加载城市列表
		 	loadCityList("cityList_update");
		 } else if ("brokingFirm" == $("#groupType_update").val()){
			 $("#buildingDiv_update").hide();
		 	 $("#brokingFirmDiv_update").show();
		 } else if("channelManager"==$("#groupType_update").val()){
			 $("#buildingDiv_update").show();
			 $("#brokingFirmDiv_update").hide();
			 	//加载城市列表
			 loadCityList("cityList_update");
		 }
		 
	 });
	/**
	 *执行删除用户操作
     */
	$('#remove').click(function(){
		var row = $('#user_grid').datagrid('getSelected');
		if(null==row){
			$.messager.alert('提示信息','请选择需要删除的用户！','warning');
			return;
		}
		if("${CURRENT_USER.userId}"==row.userId){
			$.messager.alert('提示信息','无法删除当前登录用户！','warning');
			return;
		}
		$.messager.confirm("操作提示", "您确定要执行删除操作吗？", function (data) {  
            if (data){ 
	            	$.ajax({
	        			url: "<%=path%>/api/removeUser.json",
	        			dataType: "json",
	        			data : 'userId=' + row.userId,
	        			cache : false,
	        			success: function(data){
	        				if(true == data.success){
	        					$('#user_grid').datagrid('reload');
	        					$.messager.alert('提示信息','操作成功!');
	        				}else{
	        					$('#user_grid').datagrid('reload');
	        					$.messager.alert('提示信息','操作失败!');
	        				}
	        			}
	        		});
            }  
        });  
	});
	
	//用户组类型值改变
	 $("#groupType").change(function(e){
		 if ("commissioner" == $("#groupType").val()){
		 	$("#buildingDiv").show();
		 	$("#brokingFirmDiv").hide();
		 	//加载城市列表
		 	loadCityList("cityList");
		 } else if ("brokingFirm" == $("#groupType").val()){
			$("#buildingDiv").hide();
		 	$("#brokingFirmDiv").show();
		 } else if("channelManager"==$("#groupType").val()){
			 $("#buildingDiv").show();
			 $("#brokingFirmDiv").hide();
			 //加载城市列表
			 loadCityList("cityList");
		 }
		 
	 });
	//城市选型发生变化
	 $("#cityList").change(function(e){
		 loadBuildingList($("#cityList").val(),"buildingList");
	 });
	var loadCityList = function (cityId){
		$.ajax({
			url: "<%=path%>/api/getCitys.json",
			dataType: "json",
			cache : false,
			success: function(data){
				console.log("citysdata", data);
				if(data && data.success == true){
					if(data.total > 0){
						$("#"+cityId).empty();
						for (var i = 0; i < data.rows.length; i++){
							$("#"+cityId).append("<option value='" + data.rows[i].cityId + "'>" + data.rows[i].cityName + "</option>");
						}
						$("#"+cityId).change();
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
	var loadBuildingList = function (cityId,buildingId){
		$.ajax({
			url: "<%=path%>/api/buildings/cityId/" + cityId + ".json",
			dataType: "json",
			cache : false,
			success: function(data){
				console.log("buildingsdata", data);
				if(data && data.success == true){
					if(data.result){
						$("#"+buildingId).empty();
						for (var i = 0; i < data.result.length; i++){
							$("#"+buildingId).append("<option value='" + data.result[i].buildingId + "'>" + data.result[i].buildingName + "</option>");
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
		var tel=$('#tel').textbox("isValid");
		var nick=$("#nick").textbox("isValid");
		var groupType = $("#groupType").val();
		if(tel&&nick&&""!=groupType){
			console.log("form:", $('#add_form'));
			$.restPost({
				  url: '<%=path%>/api/userInfo.json',
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