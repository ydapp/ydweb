<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>主界面</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/style/easyui.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/public/style/style_content.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/public/script/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/public/script/jquery.easyui.min.js"></script>
</head>
<body>
		<div class="title_borderB"><a>只有下面分界线的标题</a></div>
		<br>
		<div class="title_border"><a>上下均有分界线的标题</a></div>
		<br>
		<div class="title_borderT"><a>只有上面分界线的标题</a></div>
		<br>
		<div class="title"><a>上下均无分界线的标题</a></div>
		<br>
		<p>表格样式</p>
	<table id="griddata"></table>
	<script>
				$('#griddata').datagrid({
					width:'100%',
					height:'190',
					url: '<%=request.getContextPath()%>/api/users.json',
				    method: 'GET',
					striped:true,
					fitColumns:true,
					pagination:true,
				    pageSize:5,
				    pageList: [5, 10, 15],
				    singleSelect:true,
				    rownumbers:true,
				    columns: [[
/* 				               { field: 'ck', checkbox: true }, */
				               { field: 'userId', title: '用户编码', width: 800, align: 'left' },
				               { field: 'username', title: '用户姓名', width: 800, align: 'left' },
				               { field: 'name', title: '姓名', width: 800, align: 'center' },
				               { field: 'password', title: '密码', width: 800,hidden:true, align: 'center' },
				              ]],
				    onClickRow: function (rowIndex, rowData) {
				           console.log(arguments); 
				           console.log(rowData.password);
				    }
				});
 
	</script>
    <br>

</body>
</html>