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
<meta name="apple-mobile-web-app-title" content="楼市信息" />
<title>楼市信息管理页面</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/metro_blue.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/icon.css" />
<script type="text/javascript" src="<%=path%>/public/script/My97DatePicker/WdatePicker.js"></script>
<style type="text/css">
.fitem {
	height: 36px;
	line-height: 36px;
}

#add_form label {
	display: inline-block;
	width:80px;
}
</style>
</head>
<script type="text/javascript">
	function ChangeDateFormat(val,row,index){
		if (val){
			return val.substring(0,10);
		} else {
			return "";
		}
	}
						
</script>
<body class="easyui-layout">
	<div data-options="region:'center',border:false" style="padding:5px 5px 5px 5px;" >
	    <!-- 表格头部标签 -->
		<div id="tb" style="padding:2px 5px;text-align:right">
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" id="add">新增楼盘信息</a>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-edit" plain="true" id="update">修改楼盘信息</a>
			<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-remove" plain="true" id="remove">删除楼盘信息</a>
		</div>
		<table id="article_grid" class="easyui-datagrid" title="楼盘信息列表" 
		data-options="singleSelect:true,url:'<%=path%>/admin/properties.json',method:'get',toolbar:'#tb',rownumbers:'true',pagination:'true',fit:'true',fitColumns: true">
			<thead>
				<tr>
					<th data-options="field:'cityTitle',width:180">城市名称</th>
					<th data-options="field:'propertyName',width:180">楼盘名称</th>
					<th data-options="field:'address',width:180">楼盘地址</th>
					<th data-options="field:'propertyType',width:180,fixed:true">楼盘类型</th>
					<th data-options="field:'openDate',width:180,fixed:true,formatter:ChangeDateFormat">开盘时间</th>
					
					<th data-options="field:'avgPrice',width:180,fixed:true">均价</th>
					<th data-options="field:'subscriptionRules',width:180,fixed:true">游戏规则</th>
				</tr>
			</thead>
		</table>
	</div>
	<!-- 弹出窗口 新增 -->
	<div id="mydialog" title="新增楼盘" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<form id="add_form" action="<%=path%>/admin/property/add.json" method="post" enctype="multipart/form-data">
					<table>
						<tr>
							<td><div class="fitem">
									<label><font color="red">*</font>所在城市：</label> <select id="city" name="city"> </select>
								</div>
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>楼盘名称：</label> <input id="propertyName" name="propertyName" validType="maxLen['#propertyName',200]" required="required"  class="easyui-textbox" style="width: 400px;">
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>封面图片：</label> <input
										name="cover" id="cover_add" required="required" class="easyui-filebox" style="width: 400px;" data-options="prompt:'选择一张图片'">
								</div>
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>楼盘类型：</label> <input id="propertyType" name="propertyType"validType="maxLen['#propertyType',100]" required="required"  class="easyui-textbox" style="width: 400px;" data-options="prompt:'大型'">
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>楼盘地址：</label> <input id="address" name="address" validType="maxLen['#address',200]"  required="required"  class="easyui-textbox" style="width: 400px;" data-options="prompt:'南京鼓楼区xx号'">
								</div>		
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>游戏规则：</label> <input id="subscriptionRules" name="subscriptionRules" validType="maxLen['#subscriptionRules',500]" required="required"  class="easyui-textbox" style="width: 400px;" data-options="prompt:'首付2层'">
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>首期开盘：</label> <input id="openDate"	name="openDate" required="required"  class="Wdate" readonly="readonly" style="width: 400px;" data-options="prompt:'2016-1-1'"  onFocus="WdatePicker({maxDate:'#F{$dp.$D(\'deliveryTime\')}'})" >
								</div>		
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>楼盘特色：</label> <input id="characteristic" name="characteristic" validType="maxLen['#characteristic',500]" required="required"  class="easyui-textbox" style="width: 400px;" data-options="prompt:'靠近地铁'">
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>装修类型：</label> <input id="decoration" name="decoration" validType="maxLen['#decoration',100]" required="required"  class="easyui-textbox" style="width: 400px;" data-options="prompt:'精装'">
								</div>		
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>占地面积：</label> <input id="area" name="area" validType="maxLen['#area',100]" required="required"  class="easyui-textbox" style="width: 400px;" data-options="prompt:'20亩'">
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>产权年限：</label> <input id="years" name="years" required="required"  class="easyui-textbox" style="width: 400px;" data-options="prompt:'70'">
								</div>		
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>绿化率：</label> <input id="greenRate" 	name="greenRate" required="required" validType="greenRateNum['#greenRate']"  class="easyui-textbox" style="width: 400px;" data-options="prompt:'40'">
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>物业公司：</label> <input id="propertyCompany" name="propertyCompany" validType="maxLen['#propertyCompany',100]" required="required"  class="easyui-textbox" style="width: 400px;" data-options="prompt:'某某物业公司'">
								</div>		
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>交付时间：</label> <input id="deliveryTime"  name="deliveryTime"  required="required" readonly="readonly"  class="Wdate" style="width: 400px;" data-options="prompt:'2016-1-1'" onFocus="WdatePicker({minDate:'#F{$dp.$D(\'openDate\')}'})" >
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>均价：</label> <input id="avgPrice"	name="avgPrice" validType="valiNum['#avgPrice']"  required="required"  class="easyui-textbox" style="width: 400px;" data-options="prompt:'30000'">
								</div>		
							</td>
							<td>
								<!-- 
								<div class="fitem">
									<label><font color="red">*</font>佣金：</label> <input
										name="avgPrice" class="easyui-textbox" style="width: 400px;" data-options="prompt:'10'">
								</div>	
								 -->
								<div class="fitem">
									<label><font color="red">*</font>售楼电话：</label> <input id="propertyTel" name="propertyTel" validType="maxLen['#propertyTel',240]" required="required"  class="easyui-textbox" style="width: 400px;" data-options="prompt:'025-88888888'">
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>交通配套：</label> <textarea
										id="trafficFacilities" name="trafficFacilities" validType="maxLen['#trafficFacilities',1000]" required="required" readonly="readonly" style="width: 400px;height: 50px"></textarea><br>
										<button type="button" id="addRafficFacilities">添加一条交通配套</button>
								</div>
										
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>户型介绍：</label> <textarea
										id="houseType" name="houseType" validType="maxLen['#houseType',1000]"required="required"  readonly="readonly"  style="width: 400px;height: 50px"></textarea><br>
										<button type="button" id="addHouseType">添加一条户型</button>
								</div>		
							</td>
						</tr>
					</table>
				</form>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" href="javascript:void(0)" onclick="saveBuilding();" style="width:80px;">提交</a>
				<a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="javascript:$('#mydialog').dialog('close');" style="width:80px">关闭</a>
			</div>
		</div>
	</div>
	<div id="addRafficFacilitiesDialog" title="添加一个交通配套" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
					<div class="fitem">
						<label><font color="red">*</font>配套名称：</label> <input id="rafficFacilitiesName"
							name="rafficFacilitiesName" class="easyui-textbox" style="width: 400px;" data-options="prompt:'配套名称'">
					</div>		
					<div class="fitem">
						<label><font color="red">*</font>配套说明：</label> <input id="rafficFacilitiesDesc"
							name="rafficFacilitiesDesc" class="easyui-textbox" style="width: 400px;" data-options="prompt:'配套描述'">
					</div>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" href="javascript:void(0)" onclick="saveRafficFacilities();" style="width:80px;">提交</a>
				<a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="javascript:$('#addRafficFacilitiesDialog').dialog('close');" style="width:80px">关闭</a>
			</div>
		</div>
	</div>
	<div id="addHouseTypeDialog" title="添加一个户型" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<form id="houseType_form" action="<%=path%>/admin/property/houseType/add.json" method="post" enctype="multipart/form-data">
					<div class="fitem">
						<label><font color="red">*</font>户型名称：</label> <input id="houseTypeName"
							name="houseTypeName" class="easyui-textbox" style="width: 400px;" data-options="prompt:'户型名称'">
					</div>		
					<div class="fitem">
						<label><font color="red">*</font>户型图片：</label> <input id="houseTypeImage"
							name="houseTypeImage" class="easyui-filebox" style="width: 400px;" data-options="prompt:'户型图片'">
					</div>
				</form>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" href="javascript:void(0)" onclick="saveHouseType();" style="width:80px;">提交</a>
				<a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="javascript:$('#addHouseTypeDialog').dialog('close');" style="width:80px">关闭</a>
			</div>
		</div>
	</div>
	<!-- 弹出窗口  修改 -->
	<div id="mydialogUpdate" title="修改楼盘" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<form id="update_form" action="<%=path%>/admin/property/update.json"  method="post" enctype="multipart/form-data">
					<input id="propertyId_update" name="propertyId" type="hidden">
					<table id="update_table">
						<tr>
							<td><div class="fitem">
									<label><font color="red">*</font>所在城市：</label> <select id="city_update"  name="city"></select>
								</div>
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>楼盘名称：</label><input  id="propertyName_update" name="propertyName" class="easyui-textbox" validType="maxLen['#propertyName_update',200]" required="required" class="easyui-textbox" style="width: 400px;">
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label>封面图片：</label><input id="cover_update" name="cover" class="easyui-filebox" style="width: 400px;" data-options="prompt:'选择一张图片'">
								</div>
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>楼盘类型：</label><input id="propertyType_update"	name="propertyType" class="easyui-textbox" validType="maxLen['#propertyType_update',100]" required="required" style="width: 400px;" data-options="prompt:'大型'">
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>楼盘地址：</label><input id="address_update"	name="address" class="easyui-textbox"validType="maxLen['#address_update',200]"  required="required" style="width: 400px;" data-options="prompt:'南京鼓楼区xx号'">
								</div>		
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>游戏规则：</label><input id="subscriptionRules_update"name="subscriptionRules" class="easyui-textbox" validType="maxLen['#subscriptionRules_update',500]" required="required" style="width: 400px;" data-options="prompt:'首付2层'">
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>首期开盘：</label><input id="openDate_update" name="openDate" class="Wdate"   required="required" style="width: 400px;" data-options="prompt:'2016-1-1'" readonly="readonly"  onFocus="WdatePicker({maxDate:'#F{$dp.$D(\'deliveryTime_update\')}'})"/>
								</div>		
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>楼盘特色：</label><input id="characteristic_update" name="characteristic" class="easyui-textbox" validType="maxLen['#characteristic_update',500]"  required="required" style="width: 400px;" data-options="prompt:'靠近地铁'">
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>装修类型：</label><input id="decoration_update" 	name="decoration" class="easyui-textbox" validType="maxLen['#decoration_update',100]"  required="required" style="width: 400px;" data-options="prompt:'精装'">
								</div>		
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>占地面积：</label><input id="area_update" name="area" class="easyui-textbox" validType="maxLen['#area_update',100]" required="required" style="width: 400px;" data-options="prompt:'20亩'">
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>产权年限：</label><input id="years_update" name="years" class="easyui-textbox" required="required" style="width: 400px;" data-options="prompt:'70'">
								</div>		
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>绿化率：</label><input id="greenRate_update" name="greenRate" class="easyui-textbox" validType="greenRateNum['#greenRate_update']"     required="required" style="width: 400px;" data-options="prompt:'40'">
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>物业公司：</label><input id="propertyCompany_update"	name="propertyCompany" class="easyui-textbox" validType="maxLen['#propertyCompany_update',100]" required="required" style="width: 400px;" data-options="prompt:'某某物业公司'">
								</div>		
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>交付时间：</label><input id="deliveryTime_update" name="deliveryTime" class="Wdate" required="required" style="width: 400px;" data-options="prompt:'2016-1-1'" readonly="readonly"   onFocus="WdatePicker({minDate:'#F{$dp.$D(\'openDate_update\')}'})" >
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>均价：</label><input id="avgPrice_update"name="avgPrice" class="easyui-textbox"  validType="valiNum['#avgPrice_update']" required="required" style="width: 400px;" data-options="prompt:'30000'">
								</div>		
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>售楼电话：</label><input id="propertyTel_update"	name="propertyTel" class="easyui-textbox" validType="maxLen['#propertyTel_update',240]" required="required" style="width: 400px;" data-options="prompt:'025-88888888'">
								</div>		
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>失效期限：</label><input  id="passTime_update" name="passTime" class="easyui-textbox" validType="maxLen['#propertyName_update',200]" required="required" class="easyui-textbox" style="width: 400px;">
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>交通配套：</label> <textarea
										id="trafficFacilities_update" name="trafficFacilities" class="easyui-textbox" required="required" validType="maxLen['#trafficFacilities_update',1000]" readonly="readonly"  style="width: 400px;height: 50px"></textarea><br>
										<button type="button" id="addRafficFacilities_update">添加一条交通配套</button>
								</div>
										
							</td>
							<td>
								<div class="fitem">
									<label><font color="red">*</font>户型介绍：</label> <textarea
										id="houseType_update" name="houseType" class="easyui-textbox" validType="maxLen['#houseType_update',1000]"required="required" readonly="readonly"  style="width: 400px;height: 50px"></textarea><br>
										<button type="button" id="addHouseType_update">添加一条户型</button>
								</div>		
							</td>
						</tr>
					</table>
				</form>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" href="javascript:void(0)" onclick="saveBuilding_update();" style="width:80px;">提交</a>
				<a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="javascript:$('#mydialogUpdate').dialog('close');" style="width:80px">关闭</a>
			</div>
		</div>
	</div>
	<div id="addRafficFacilitiesDialog_update" title="添加一个交通配套" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
					<div class="fitem">
						<label><font color="red">*</font>配套名称：</label> <input id="rafficFacilitiesName_update"
							name="rafficFacilitiesName" class="easyui-textbox" style="width: 400px;" data-options="prompt:'配套名称'">
					</div>		
					<div class="fitem">
						<label><font color="red">*</font>配套说明：</label> <input id="rafficFacilitiesDesc_update"
							name="rafficFacilitiesDesc" class="easyui-textbox" style="width: 400px;" data-options="prompt:'配套描述'">
					</div>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" href="javascript:void(0)" onclick="saveRafficFacilities_update();" style="width:80px;">提交</a>
				<a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="javascript:$('#addRafficFacilitiesDialog_update').dialog('close');" style="width:80px">关闭</a>
			</div>
		</div>
	</div>
	<div id="addHouseTypeDialog_update" title="添加一个户型" class="easyui-dialog" data-options="maximized:true,modal:true,closed:true">
		<div class="easyui-layout" data-options="fit:true">
			<div data-options="region:'center',border:false">
				<form id="houseType_form_update" action="<%=path%>/admin/property/houseType/add.json" method="post" enctype="multipart/form-data">
					<div class="fitem">
						<label><font color="red">*</font>户型名称：</label> <input id="houseTypeName_update"
							name="houseTypeName" class="easyui-textbox" style="width: 400px;" data-options="prompt:'户型名称'">
					</div>		
					<div class="fitem">
						<label><font color="red">*</font>户型图片：</label> <input id="houseTypeImage_update"
							name="houseTypeImage" class="easyui-filebox" style="width: 400px;" data-options="prompt:'户型图片'">
					</div>
				</form>
			</div>
			<div data-options="region:'south',border:true" style="text-align:right;padding:5px;">
				<a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" href="javascript:void(0)" onclick="saveHouseType_update();" style="width:80px;">提交</a>
				<a class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="javascript:$('#addHouseTypeDialog_update').dialog('close');" style="width:80px">关闭</a>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript" src="<%=path%>/public/script/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="<%=path%>/public/script/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=path%>/public/script/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=path%>/public/script/jquery-rest.js"></script>
<script type="text/javascript">
	//点击增加一个交通配套按钮
	$("#addRafficFacilities").click(function(){
		$('#addRafficFacilitiesDialog').dialog('open');
		$("#rafficFacilitiesName").val("");
		$("#rafficFacilitiesDesc").val("");
		 return false;
	});
	//保存一条交通配套到textArea中
	var saveRafficFacilities = function(){
		var line = "***" + $("#rafficFacilitiesName").val() + ":::" + $("#rafficFacilitiesDesc").val();
		if ($("#trafficFacilities").val()){
			$("#trafficFacilities").val($("#trafficFacilities").val() + "\r\n" + line);
		} else {
			$("#trafficFacilities").val(line);
		}
		$('#addRafficFacilitiesDialog').dialog('close');
	};
	$('#add').click(function(){
		 $('#mydialog').dialog('open');
		 $('#add_form').form('clear');
		 loadCityList("city");
	});
	//点击增加一个户型按钮
	$("#addHouseType").click(function(){
		$('#addHouseTypeDialog').dialog('open');
		$("#houseTypeName").val("");
		$("#houseTypeImage").val("");
		 return false;
	});
	//保存一条交通配套到textArea中
	var saveHouseType = function(){
		$('#houseType_form').form('submit', {    
		    success:function(data){
		    	var data = eval('(' + data + ')');
		    	console.log("houseType:" + JSON.stringify(data));
		    	if(data.success){
		        	$.messager.alert('温馨提示','新增成功');
		        	var line = "***" + data.result.houseTypeName + ":::" + data.result.houseTypeImage;
		    		if ($("#houseType").val()){
		    			$("#houseType").val($("#houseType").val() + "\r\n" + line);
		    		} else {
		    			$("#houseType").val(line);
		    		}
		    		$('#addHouseTypeDialog').dialog('close');
		        }else{
		        	$.messager.alert('温馨提示',data.message);
		        }
		    }    
		});
	};
	var saveBuilding = function(){
		var validate = $("#add_form").form('validate');
		if(validate){
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
	$('#add').click(function(){
		 $('#mydialog').dialog('open');
		 $('#add_form').form('clear');
		 loadCityList("city");
	});
	/**
	 *打开修改页面 
	 */
	$("#update").click(function(){
		//选中table中一行数据 
		var row = $('#article_grid').datagrid('getSelected');
		if(null==row){
			$.messager.alert('温馨提示',"请选中需要修改的楼盘信息!");
			return;
		}
		$.ajax({
			url: "<%=path%>/api/property.json",
			dataType: "json",
			data : 'id=' + row.propertyId,
			cache : false,
			success:function(data){
				//判断获取楼盘信息是否为空 
				if(null!=data&&null!=data.building){
					var buildingObj = data.building;
					//获取修改页面中所有的输入框 
					var inputObj=$("#update_table input");
					//循环遍历组装输入框数据 
					for(var i=0;i<inputObj.size();i++){
						//判断需要组装输入框的数据 
				 		if(null!=inputObj[i]&&""!=inputObj[i].id&&"cover_update"!=inputObj[i].id&&"filebox_file_id_3"!=inputObj[i].id&&"openDate_update"!=inputObj[i].id&&"deliveryTime_update"!=inputObj[i].id){
				 			var len=inputObj[i].id.indexOf("_");
				 			var file=inputObj[i].id.substring(0,len);
				 			if(""!=buildingObj[file]){
					 			$("#"+inputObj[i].id).textbox('setValue',buildingObj[file]);
				 			}
				 		}
				 	}
					//手动组装数据 
					$("#propertyId_update").val(row.propertyId);
					$("#openDate_update").val(buildingObj['openDate']);
					$("#deliveryTime_update").val(buildingObj['deliveryTime']);
					$("#trafficFacilities_update").textbox('setValue',buildingObj['trafficFacilities']);
					$("#houseType_update").textbox('setValue',buildingObj['houseType']);
				}
			}
		});
		//打开页面 
		$("#mydialogUpdate").dialog('open');
		loadCityList("city_update");
	});
	/**
	 * 添加交通配套 
	 */
	$("#addRafficFacilities_update").click(function(){
		$('#addRafficFacilitiesDialog_update').dialog('open');
		$("#rafficFacilitiesName_update").val("");
		$("#rafficFacilitiesDesc_update").val("");
		 return false;
	});
	/**
	 * 保存一条交通配套到textArea中 
	 */
	var saveRafficFacilities_update = function(){
		var line = "***" + $("#rafficFacilitiesName_update").val() + ":::" + $("#rafficFacilitiesDesc_update").val();
		if ($("#trafficFacilities_update").val()){
			$("#trafficFacilities_update").textbox('setValue',$("#trafficFacilities").val() + "\r\n" + line);
		} else {
			$("#trafficFacilities_update").textbox('setValue',line);
		}
		$('#addRafficFacilitiesDialog_update').dialog('close');
	};
	/**
	 * 点击增加一个户型按钮
	 */
	$("#addHouseType_update").click(function(){
		$('#addHouseTypeDialog_update').dialog('open');
		$("#houseTypeName_update").val("");
		$("#houseTypeImage_update").val("");
		 return false;
	});
	/**
	 * 保存一条交通配套到textArea中
	 */
	var saveHouseType_update = function(){
		$('#houseType_form_update').form('submit', {    
		    success:function(data){
		    	var data = eval('(' + data + ')');
		    	console.log("houseType_update:" + JSON.stringify(data));
		    	if(data.success){
		        	$.messager.alert('温馨提示','新增成功');
		        	var line = "***" + data.result.houseTypeName + ":::" + data.result.houseTypeImage;
		    		if ($("#houseType_update").val()){
		    			$("#houseType_update").textbox('setValue',$("#houseType_update").val() + "\r\n" + line);
		    		} else {
		    			$("#houseType_update").textbox('setValue',line); 
		    		}
		    		$('#addHouseTypeDialog_update').dialog('close');
		        }else{
		        	$.messager.alert('温馨提示',data.message);
		        }
		    }    
		});
	};
	$.extend($.fn.validatebox.defaults.rules,{
		greenRateNum:{
			validator:function(value){
				if(value.indexOf(".")=="-1"&&value.length>=3){
					return false;
				}
				if("-1"!=value.indexOf(".")){
					var after = value.indexOf(".");
					var val = value.substring(after+1,value.length);
					if(val.length>2){
						return false;
					}
				}
				return true;
			},
			message : "绿化率输入数字格式不正确！"
		},
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
		},
		valiNum:{
			validator:function(value){
				var r = /^\+?[1-9][0-9]*$/;
			    return r.test(value);
			},
			message : "输入内容不能是非数字！"
		}
	});
	/**
	 * 执行修改操作 
	 */
	var saveBuilding_update=function(){
		//判断页面提交验证
		var validate = $("#update_form").form('validate');
		if(validate){
			$('#update_form').form('submit', {    
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
	var loadCityList = function (_cityId){
		$.ajax({
			url: "<%=path%>/api/getCitys.json",
			dataType: "json",
			cache : false,
			success: function(data){
				console.log("citysdata", data);
				if(data && data.success == true){
					if(data.total > 0){
						$("#"+_cityId).empty();
						for (var i = 0; i < data.rows.length; i++){
							$("#"+_cityId).append("<option value='" + data.rows[i].cityId + "'>" + data.rows[i].cityName + "</option>");
						}
						$("#"+_cityId).change();
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
	/**
	 *执行删除操作
	 */
	$("#remove").click(function(){
		var row = $('#article_grid').datagrid('getSelected');
		if(null==row){
			$.messager.alert('提示信息','请选择需要删除楼盘！','warning');
			return;
		}
		$.messager.confirm("操作提示", "您确定要执行删除操作吗？", function (data) {
			if(data){
				 $.ajax({
					 url:'<%=path%>/api/removeProperty.json',
					 dataType:"json",
					 data:'propertyId='+row.propertyId,
					 cache:false,
					 success:function(data){
						 if(true == data.success){
	        					$('#article_grid').datagrid('reload');
	        					$.messager.alert('提示信息','操作成功!');
	        				}else{
	        					$('#article_grid').datagrid('reload');
	        					$.messager.alert('提示信息','操作失败!');
	        				}
					 }
				 });
			
			};
		});
	});
</script>
</html>