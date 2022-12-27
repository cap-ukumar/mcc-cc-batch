package org.cap.cc.batch.model;

public class CheckListChannelEntity {
	
	private String content;
	private String channel;
	
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	@Override
	public String toString() {
		return "CheckListChannelEntity [content=" + content + ", channel=" + channel + "]";
	}
	
	
	


}
