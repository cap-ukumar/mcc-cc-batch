package org.cap.cc.batch.model;

import org.cap.cc.batch.dao.CustomChecklistConstants;

public class PrinterData {

	private String filePath;
	private String mediaColor;
	private String mediaType;
	private boolean staple;
	private boolean duplex;

	public PrinterData() {
	}

	public PrinterData(String filePath, String mediaColor, String mediaType, boolean staple, boolean duplex) {
		this.filePath = filePath;
		this.mediaColor = mediaColor;
		this.mediaType = mediaType;
		this.staple = staple;
		this.duplex = duplex;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath, Integer taskId, Integer itemSeqNo, String auId, String suId,
			String moduleId, String editionId) {
		String path = filePath + taskId + CustomChecklistConstants.UNDERSCORE + itemSeqNo + CustomChecklistConstants.UNDERSCORE + auId + CustomChecklistConstants.UNDERSCORE + suId + CustomChecklistConstants.UNDERSCORE + moduleId + CustomChecklistConstants.UNDERSCORE + editionId
				+ CustomChecklistConstants.DOT + CustomChecklistConstants.EXTENSION_PDF;
		this.filePath = path;
	}

	public String getMediaColor() {
		return mediaColor;
	}

	public void setMediaColor(String mediaColor) {
		this.mediaColor = mediaColor;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public boolean isStaple() {
		return staple;
	}

	public void setStaple(boolean staple) {
		this.staple = staple;
	}

	public boolean isDuplex() {
		return duplex;
	}

	public void setDuplex(boolean duplex) {
		this.duplex = duplex;
	}

	@Override
	public String toString() {
		return "PrinterData [filePath=" + filePath + ", mediaColor=" + mediaColor + ", mediaType=" + mediaType
				+ ", staple=" + staple + ", duplex=" + duplex + "]";
	}

	public String toJsonString() {
		return "\"printerData\": {\r\n" + "	\"filePath\":\"" + getFilePath() + "\",\r\n" + "	\"mediaColor\":\""
				+ getMediaColor() + "\",\r\n" + "	\"mediaType\":\"" + getMediaType() + "\",\r\n" + "	\"staple\":"
				+ isStaple() + ",\r\n" + "	\"duplex\":" + isDuplex() + "\r\n" + "}\r\n";
	}

}
