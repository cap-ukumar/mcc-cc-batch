package org.cap.cc.batch.model;

import java.io.Serializable;


public class ChecklistResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6671528282583002694L;

	private ChecklistJobInfo checklistJobInfo;
	private ChecklistCsvInfo checklistCsvInfo;
	private ChklstPreviewInfo chklstPreviewInfo;

	public ChecklistJobInfo getChecklistJobInfo() {
		return checklistJobInfo;
	}

	public void setChecklistJobInfo(ChecklistJobInfo checklistJobInfo) {
		this.checklistJobInfo = checklistJobInfo;
	}

	public ChecklistCsvInfo getChecklistCsvInfo() {
		return checklistCsvInfo;
	}

	public void setChecklistCsvInfo(ChecklistCsvInfo checklistCsvInfo) {
		this.checklistCsvInfo = checklistCsvInfo;
	}

	public ChklstPreviewInfo getChklstPreviewInfo() {
		return chklstPreviewInfo;
	}

	public void setChklstPreviewInfo(ChklstPreviewInfo chklstPreviewInfo) {
		this.chklstPreviewInfo = chklstPreviewInfo;
	}

}
