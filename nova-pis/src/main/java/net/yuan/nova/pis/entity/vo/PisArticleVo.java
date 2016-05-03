package net.yuan.nova.pis.entity.vo;

import net.yuan.nova.pis.entity.PisArticle;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PisArticleVo extends PisArticle {

	private String filePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
