package org.cap.cc.batch.model;

public class PrinterData {

	private String filePath;
	private String mediaColor;
	private String mediaType;
	private boolean staple;
	private boolean duplex;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath, Integer taskId, Integer itemSeqNo, Integer auId, Integer suId,
			String moduleId, String editionId) {
		String path = filePath + taskId + "_" + itemSeqNo + "_" + auId + "_" + suId + "_" + moduleId + "_" + editionId
				+ ".pdf";
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
		return "\"printerData\": {\r\n"
				+ "	\"filePath\":\""+getFilePath()+"\",\r\n"
				+ "	\"mediaColor\":\""+getMediaColor()+"\",\r\n"
				+ "	\"mediaType\":\""+getMediaType()+"\",\r\n"
				+ "	\"staple\":"+isStaple()+",\r\n"
				+ "	\"duplex\":"+isDuplex()+"\r\n"
				+ "	";
	}

}
