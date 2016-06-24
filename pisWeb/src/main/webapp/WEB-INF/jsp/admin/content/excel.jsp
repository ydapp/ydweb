<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<!DOCTYPE html>
<%
	String path = request.getContextPath();
%>
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
<meta name="apple-mobile-web-app-title" content="推荐信息" />
<title>推荐信息</title>
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/metro_blue.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/public/style/icon.css" />
<script type="text/javascript" src="<%=path%>/public/script/My97DatePicker/WdatePicker.js"></script>
</head>
<body class=" easyui-layout">
	<div data-options="region:'north',collapsible:false,border:false,split:true" style="height:140px; padding:5px 5px 0;">
		<div class="easyui-panel" data-options="fit:true,title:'推荐信息分类导出',iconCls:'title-icon'">
			<form id="excelForm" method="post" class="search-form">
				<table style="width:40%;" >
				   <tr>
				 		<td align="right">楼盘：</td>
				 		<td><select id="buildingList" style="width:100px;"></select></td>
				 	</tr>
				 	<tr>
						<td align="right">时间：</td>
				 		<td><input id="startDate"	name="startDate"  class="Wdate" readonly="readonly" style="width: 100px;" data-options="prompt:'2016-1-1'"  onFocus="WdatePicker({maxDate:'#F{$dp.$D(\'endDate\')}'})" >--<input id="endDate"  name="endDate" readonly="readonly"  class="Wdate" style="width:100px;" data-options="prompt:'2016-1-1'" onFocus="WdatePicker({minDate:'#F{$dp.$D(\'startDate\')}'})" ></td>
				 	</tr>
					<tr>
						<td colspan="2">
							<div align="right">
								<a id="searchbtn" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
								<a id="importbtn" class="easyui-linkbutton" data-options="iconCls:'icon-print'">导出</a>
								<a id="clearbtn" class="easyui-linkbutton" data-options="iconCls:'icon-reload'">重置</a>
							</div>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<div data-options="region:'center',border:false" style="padding:5px 5px 5px 5px;" >
		<table id="excel_grid" class="easyui-datagrid" title="推荐信息列表" 
		data-options="singleSelect:true,url:'<%=path%>/recommend/getList.json',method:'get',toolbar:'#tb',rownumbers:'true',pagination:'true',fit:'true',fitColumns: true">
			<thead>
				<tr>
					<th data-options="field:'customerName',width:130">客户名称</th>
					<th data-options="field:'customerTel',width:100">客户电话</th>
					<th data-options="field:'cityName',width:60">城市</th>
					<th data-options="field:'buildingName',width:100">楼盘</th>
					<th data-options="field:'refreeName',width:130">推荐人</th>
					<th data-options="field:'recommendDate',width:130">推荐时间</th>
					<th data-options="field:'customerPresentDate',width:130">客户到场时间</th>
					<th data-options="field:'customerPresentName',width:130">到场确认人</th>
					<th data-options="field:'remark',width:130">详情</th>
				</tr>
			</thead>
		</table>
	</div>
<script type="text/javascript"
	src="<%=path%>/public/script/jquery-1.10.2.min.js"></script>
<script type="text/javascript"
	src="<%=path%>/public/script/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="<%=path%>/public/script/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">

/**
 *  初始化
 */
$(function(){
	//加载楼盘信息
	loadBuildingList();
});
/**
 * 重置事件
 */
$('#clearbtn').click(function() {
	loadBuildingList();//刷新楼盘信息
	//清空
	$('#startDate').val('');
	$('#endDate').val('');
});
/**
 * 导出事件 
 */
$('#importbtn').click(function() {
	var building = $("#buildingList").val();
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	window.location.href='<%=path%>/api/excell.json?building='+building+'&startDate='+startDate+'&endDate='+endDate;
});
/**
 * 查询事件
 */
$("#searchbtn").click(function(){
	 $('#excel_grid').datagrid('load',{  
		 building:$('#buildingList').val(),  
		 startDate:$('#startDate').val(),
		 endDate:$("#endDate").val()
	 });  
});
/**
 * 加载楼盘信息 
 */
var loadBuildingList = function (){
	$.ajax({
		url: '<%=path%>/api/getAllBuildings.json',
		dataType: "json",
		cache : false,
		success: function(data){
			console.log("buildingsdata", data);
			if(data && data.success == true){
				if(data.result){
					$("#buildingList").empty();
					$("#buildingList").append("<option id='sele' value=''>请选择</option>");
					for (var i = 0; i < data.result.length; i++){
						$("#buildingList").append("<option value='" + data.result[i].buildingId + "'>" + data.result[i].buildingName + "</option>");
					}
				}
			}else {
				$.messager.alert('温馨提示',"获取楼盘列表失败!");
			}
		},
		error: function(){
			alert("发生异常");
		}
	});
}
 
</script>
</body>
</html>