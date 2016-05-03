var JsonFormat = function() {
	var TAB = "  ";
	function isArray(obj) {
		return obj && typeof obj === 'object' && typeof obj.length === 'number'
				&& !(obj.propertyIsEnumerable('length'));
	}
	function process(domId, json) {
		var html = "";
		if (typeof json == 'object') {
			html = processObject(json, 0, false, false, false);
		} else {
			try {
				if (json == "")
					json = "\"\"";
				var obj = eval("[" + json + "]");
				html = processObject(obj[0], 0, false, false, false);
			} catch (e) {
				alert("JSON语法错误,不能格式化,错误信息:\n" + e.message);
				document.getElementById(domId).innerHTML = "";
			}
		}
		document.getElementById(domId).innerHTML = "<pre class='codeContainer'>"
				+ html + "</pre>";
	}
	function processObject(obj, indent, addComma, isArr, isPropertyContent) {
		var html = "";
		var comma = (addComma) ? "<span class='comma'>,</span> " : "";
		var type = typeof obj;
		if (isArray(obj)) {
			if (obj.length == 0) {
				html += getRow(indent, "<span class='arrayBrace'>[ ]</span>"
						+ comma, isPropertyContent);
			} else {
				html += getRow(indent, "<span class='arrayBrace'>[</span>",
						isPropertyContent);
				for (var i = 0; i < obj.length; i++) {
					html += processObject(obj[i], indent + 1,
							i < (obj.length - 1), true, false);
				}
				html += getRow(indent, "<span class='arrayBrace'>]</span>"
						+ comma);
			}
		} else if (type == 'object' && obj == null) {
			html += formatLiteral("null", "", comma, indent, isArr, "null");
		} else if (type == 'object') {
			var numProps = 0;
			for ( var prop in obj)
				numProps++;
			if (numProps == 0) {
				html += getRow(indent, "<span class='objectBrace'>{ }</span>"
						+ comma, isPropertyContent);
			} else {
				html += getRow(indent, "<span class='objectBrace'>{</span>",
						isPropertyContent);
				var j = 0;
				for ( var prop in obj) {
					html += getRow(indent + 1, '<span class="propertyName">"'
							+ prop
							+ '"</span>: '
							+ processObject(obj[prop], indent + 1,
									++j < numProps, false, true));
				}
				html += getRow(indent, "<span class='objectBrace'>}</span>"
						+ comma);
			}
		} else if (type == 'number') {
			html += formatLiteral(obj, "", comma, indent, isArr, "number");
		} else if (type == 'boolean') {
			html += formatLiteral(obj, "", comma, indent, isArr, "boolean");
		} else if (type == 'function') {
			obj = formatFunction(indent, obj);
			html += formatLiteral(obj, "", comma, indent, isArr, "function");
		} else if (type == 'undefined') {
			html += formatLiteral("undefined", "", comma, indent, isArr, "null");
		} else {
			html += formatLiteral(obj, "\"", comma, indent, isArr, "string");
		}
		return html;
	}
	function formatLiteral(literal, quote, comma, indent, isArr, style) {
		if (typeof literal == 'string')
			literal = literal.split("<").join("<").split(">").join(">");
		var str = "<span class='" + style + "'>" + quote + literal + quote
				+ comma + "</span>";
		if (isArr)
			str = getRow(indent, str);
		return str;
	}
	function formatFunction(indent, obj) {
		var tabs = "";
		for (var i = 0; i < indent; i++)
			tabs += TAB;
		var funcStrArray = obj.toString().split("\n");
		var str = "";
		for (var i = 0; i < funcStrArray.length; i++) {
			str += ((i == 0) ? "" : tabs) + funcStrArray[i] + "\n";
		}
		return str;
	}
	function getRow(indent, data, isPropertyContent) {
		var tabs = "";
		for (var i = 0; i < indent && !isPropertyContent; i++)
			tabs += TAB;
		if (data != null && data.length > 0
				&& data.charAt(data.length - 1) != "\n")
			data = data + "\n";
		return tabs + data;
	}

	return {
		'process' : process
	};
}();
