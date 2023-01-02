package org.cap.cc.batch.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.cap.cc.batch.dao.CustomChecklistDAO;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BasicChecklistEntity {

	private String userName;
	private final String password = "";
	private String editionId;
	private String moduleId;
	private Integer auId;
	private Integer suId;
	private Timestamp actEffectiveDt;
	private String outputOptions;
	private String channelData;
	private PrinterData printerData;
	@JsonIgnore
	private Integer taskU;
	@JsonIgnore
	private Integer itemSeqNo;
	@JsonIgnore
	private Integer cycleSeqNo;
	@JsonIgnore
	private String packetType;
	@JsonIgnore
	private String printSetDetailC;

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public String getEditionId() {
		return editionId;
	}

	public void setEditionId(String editionId) {
		this.editionId = editionId;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public Integer getAuId() {
		return auId;
	}

	public void setAuId(Integer auId) {
		this.auId = auId;
	}

	public Integer getSuId() {
		return suId;
	}

	public void setSuId(Integer suId) {
		this.suId = suId;
	}

	public Timestamp getActEffectiveDt() {
		return actEffectiveDt;
	}

	public void setActEffectiveDt(Timestamp actEffectiveDt) {
		this.actEffectiveDt = actEffectiveDt;
	}

	public String getOutputOptions() {
		return outputOptions;
	}

	public void setOutputOptions(String outputOptions) {
		this.outputOptions = outputOptions;
	}

	public String getChannelData() {
		return channelData;
	}

	public void setChannelData(String channelData) {
		this.channelData = channelData;
	}

	public PrinterData getPrinterData() {
		return printerData;
	}

	public void setPrinterData(PrinterData printerData) {
		this.printerData = printerData;
	}

	public Integer getTaskU() {
		return taskU;
	}

	public void setTaskU(Integer taskU) {
		this.taskU = taskU;
	}

	public Integer getItemSeqNo() {
		return itemSeqNo;
	}

	public void setItemSeqNo(Integer itemSeqNo) {
		this.itemSeqNo = itemSeqNo;
	}

	public Integer getCycleSeqNo() {
		return cycleSeqNo;
	}

	public void setCycleSeqNo(Integer cycleSeqNo) {
		this.cycleSeqNo = cycleSeqNo;
	}

	public String getPacketType() {
		return packetType;
	}

	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	public String getPrintSetDetailC() {
		return printSetDetailC;
	}

	public void setPrintSetDetailC(String printSetDetailC) {
		this.printSetDetailC = printSetDetailC;
	}

	
	public String toJsonString() {
		return "\r\n{\r\n" + "\"userName\":\"" + this.getUserName() + "\",\r\n" + "\"password\":\"" + this.password
				+ "\",\r\n" + "\"editionId\":\"" + getEditionId() + "\",\r\n" + "\"moduleId\":\"" + getModuleId()
				+ "\",\r\n" + "\"auId\":\"" + getAuId() + "\",\r\n" + "\"suId\":\"" + getSuId() + "\",\r\n"
				+ "\"actEffectiveDt\":\"" + getActEffectiveDt() + "\",\r\n" + "\"outputOptions\":\""
				+ getOutputOptions() + "\",\r\n" + "\"channelData\":\"" + getChannelData() + "\",\r\n" + " "
				+ getPrinterData() + "}";
	}

	public static List<BasicChecklistEntity> getBasicChecklistDetails(Connection con, int taskId) {
		ResultSet rs = null;
		List<BasicChecklistEntity> list = null;
		try (PreparedStatement ps = con.prepareStatement(CustomChecklistDAO.GET_BASIC_CHECKLIST_DETAILS);) {
			ps.setInt(1, taskId);
			rs = ps.executeQuery();
			if (null != rs)
				list = new ArrayList<>();
			while (null != rs && rs.next()) {
				BasicChecklistEntity obj = new BasicChecklistEntity();
				obj.setItemSeqNo(rs.getInt("ITEMSEQNO"));
				obj.setModuleId(rs.getString("CHKLIST"));
				obj.setAuId(rs.getInt("AUID"));
				obj.setSuId(rs.getInt("SUABE"));
				obj.setEditionId(rs.getString("EDITION"));
				obj.setActEffectiveDt(rs.getTimestamp("CHKLSTDATE"));
				obj.setCycleSeqNo(rs.getInt("CYCLESEQNO"));
				obj.setPacketType(rs.getString("PACKETTYPE"));
				obj.setPrintSetDetailC(rs.getString("print_set_detail_c"));
				list.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

}
