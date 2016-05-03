jQuery.parseJSON = function( data ) {
	if (!data){
		data = "{}";
	}
	return JSON.parse( data + "" );
};
jQuery.extend({
	//执行rest的get方法,不接受url之外的参数
	restGet: function (options) {
		$.extend(options, {
			contentType:'application/json; charset=utf-8',
			dataType:'json',
			type:'get',
			data:''
		});
		this.extendError(options);
		this.ajax(options);
	},
	//执行rest的post方法，序列号参数对象
	restPost: function (options) {
		$.extend(options, {
			dataType:'json',
			contentType:'application/json;charset=utf-8',
			processData: false,
			type:'post',
			data:JSON.stringify(options.data)
		});
		this.extendError(options);
		this.ajax(options);
	},
	//执行rest的put方法，序列号参数对象
	restPut: function (options) {
		$.extend(options, {
			contentType:'application/json; charset=utf-8',
			dataType:'json',
			type:'put',
			data:JSON.stringify(options.data)
		});
		this.extendError(options);
		this.ajax(options);
	},
	//执行rest的put方法，序列号参数对象
	restDelete: function (options) {
		$.extend(options, {
			contentType:'application/json; charset=utf-8',
			dataType:'json',
			type:'delete',
			data:''
		});
		this.extendError(options);
		this.ajax(options);
	},
	//如果定义了异常处理方法，则不进行默认处理
	extendError:function (options){
		if (!options.error){
			$.extend(true, options, {
				error:function (XMLHttpRequest, textStatus, errorThrown) {
					$.restError(XMLHttpRequest, textStatus, errorThrown);
				}
			})
		}
	},
	restError : function (XMLHttpRequest, textStatus, errorThrown){
		alert(JSON.parse(XMLHttpRequest.responseText).msg);
	}
});