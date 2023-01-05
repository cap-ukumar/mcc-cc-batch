package org.cap.cc.batch.model;

import java.io.Serializable;

public class ChecklistCsvInfo implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -3416590504664356987L;
	
	private byte[] chklstCsvData;

    /**
     * @return the chklstCsvShortData
     */
    public byte[] getChklstCsvData() {
        return chklstCsvData;
    }

    /**
     * @param chklstCsvShortData the chklstCsvShortData to set
     */
    public void setChklstCsvData(byte[] chklstCsvData) {
        this.chklstCsvData = chklstCsvData;
    }

}
