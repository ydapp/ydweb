package net.yuan.nova.core.entity;

public class AttachmentBlob {

	private String atchMd5;
	private Long acthSize;
	private byte[] atchCont;

	public String getAtchMd5() {
		return atchMd5;
	}

	public void setAtchMd5(String atchMd5) {
		this.atchMd5 = atchMd5;
	}

	public Long getActhSize() {
		return acthSize;
	}

	public void setActhSize(Long acthSize) {
		this.acthSize = acthSize;
	}

	public byte[] getAtchCont() {
		return atchCont;
	}

	public void setAtchCont(byte[] atchCont) {
		this.atchCont = atchCont;
	}

}
