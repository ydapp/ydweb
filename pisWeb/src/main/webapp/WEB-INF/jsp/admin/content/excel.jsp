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
<script type="text/javascript" src="<%=path%>/public/script/My97DatePicker/WdatePicker.js"></script>
</head>
<body class=" easyui-layout">
	<div data-options="region:'north',collapsible:false,border:false,split:true" style="height:123px; padding:5px 5px 0;">
		<div class="easyui-panel" data-options="fit:true,title:'推荐信息分类导出',iconCls:'title-icon'">
			<form id="excelForm" method="post" class="search-form">
				<table>
				   <tr>
				 		<td>楼盘：</td>
				 		<td><select id="buildingList" style="width:100px;"></select></td>
				 	</tr>
				 	<tr>
						<td>时间：</td>
				 		<td><input id="startDate"	name="startDate"  class="Wdate" readonly="readonly" style="width: 100px;" data-options="prompt:'2016-1-1'"  onFocus="WdatePicker({maxDate:'#F{$dp.$D(\'endDate\')}'})" >--<input id="endDate"  name="endDate" readonly="readonly"  class="Wdate" style="width:100px;" data-options="prompt:'2016-1-1'" onFocus="WdatePicker({minDate:'#F{$dp.$D(\'startDate\')}'})" ></td>
				 	</tr>
					<tr>
						<td>
							<div >
								<a id="importbtn" class="easyui-linkbutton" data-options="iconCls:'icon-search'">导出</a>
								<a id="clearbtn" class="easyui-linkbutton" data-options="iconCls:'icon-reload'">重置</a>
							</div>
						</td>
						<td></td>
					</tr>
				</table>
			</form>
		</div>
	</div>
<script type="text/javascript"
	src="<%=path%>/public/script/jquery-1.10.2.min.js"></script>
<script type="text/javascript"
	src="<%=path%>/public/script/jquery.easyui.min.js"></script>
<script type="text/javascript">
$(function(){
	loadBuildingList();
});
$('#clearbtn').click(function() {
	$('#startDate').val('');
	$('#endDate').val('');
});
$('#importbtn').click(function() {
	var building = $("#buildingList").val();
	var startDate = $("#startDate").val();
	var endDate = $("#endDate").val();
	window.location.href='<%=path%>/api/excell.json?building='+building+'&startDate='+startDate+'&endDate='+endDate;
});
var loadBuildingList = function (){
	$.ajax({
		url: "<%=path%>/api/getAllBuildings.json",
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
				alert("获取城市列表失败");
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