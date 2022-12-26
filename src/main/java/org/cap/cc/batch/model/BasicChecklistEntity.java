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

public class BasicChecklistEntity {

	private String itemSeqNo;
	private String chkList;
	private String auId;
	private String suAbe;
	private String edition;
	private Timestamp chkLstDate;
	private String cycleSeqNo;
	private String packetType;
	private String print_set_detail_c;
	private String taskId;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getItemSeqNo() {
		return itemSeqNo;
	}

	public void setItemSeqNo(String itemSeqNo) {
		this.itemSeqNo = itemSeqNo;
	}

	public String getChkList() {
		return chkList;
	}

	public void setChkList(String chkList) {
		this.chkList = chkList;
	}

	public String getAuId() {
		return auId;
	}

	public void setAuId(String auId) {
		this.auId = auId;
	}

	public String getSuAbe() {
		return suAbe;
	}

	public void setSuAbe(String suAbe) {
		this.suAbe = suAbe;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public Timestamp getChkLstDate() {
		return chkLstDate;
	}

	public void setChkLstDate(Timestamp chkLstDate) {
		this.chkLstDate = chkLstDate;
	}

	public String getCycleSeqNo() {
		return cycleSeqNo;
	}

	public void setCycleSeqNo(String cycleSeqNo) {
		this.cycleSeqNo = cycleSeqNo;
	}

	public String getPacketType() {
		return packetType;
	}

	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	public String getPrint_set_detail_c() {
		return print_set_detail_c;
	}

	public void setPrint_set_detail_c(String print_set_detail_c) {
		this.print_set_detail_c = print_set_detail_c;
	}

	@Override
	public String toString() {
		return "BasicChecklistEntity [itemSeqNo=" + itemSeqNo + ", chkList=" + chkList + ", auId=" + auId + ", suAbe="
				+ suAbe + ", edition=" + edition + ", chkLstDate=" + chkLstDate + ", cycleSeqNo=" + cycleSeqNo
				+ ", packetType=" + packetType + ", print_set_detail_c=" + print_set_detail_c + ", taskId=" + taskId
				+ "]";
	}

	public List<BasicChecklistEntity> getBasicChecklistDetails(int taskId) {
		ResultSet rs = null;
		List<BasicChecklistEntity> list = new ArrayList<>();
		try (Connection con = DriverManager.getConnection(auId);
				PreparedStatement ps = con.prepareStatement(CustomChecklistDAO.GET_BASIC_CHECKLIST_DETAILS);) {
			ps.setInt(1, taskId);
			rs = ps.executeQuery();			
			while (rs.next()) {
				BasicChecklistEntity pojo = new BasicChecklistEntity();
				pojo.setItemSeqNo(rs.getString("ITEMSEQNO"));
				pojo.setChkList(rs.getString("CHKLIST"));
				pojo.setAuId(rs.getString("AUID"));
				pojo.setSuAbe(rs.getString("SUABE"));
				pojo.setEdition(rs.getString("EDITION"));
				pojo.setChkLstDate(rs.getTimestamp("CHKLSTDATE"));
				pojo.setCycleSeqNo(rs.getString("CYCLESEQNO"));
				pojo.setPacketType(rs.getString("PACKETTYPE"));
				pojo.setPrint_set_detail_c(rs.getString("print_set_detail_c"));
				list.add(pojo);
			}
			System.out.println(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

}
