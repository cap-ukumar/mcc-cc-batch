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

	private Integer task_u;
	private Integer item_seq_no;
	private Integer au_id;
	private Integer su_id;
	private String module;
	private String edition;
	private Timestamp chklst_dt;
	private Integer cycle_seq_no;
	private String packet_type;
	private String print_set_detail_c;
	private Integer tot_crit;
	private Integer tot_ph1;
	private Integer tot_ph2;
	private String chklst_file_name;
	private String chklst_prn_file;

	public Integer getTask_u() {
		return task_u;
	}

	public void setTask_u(Integer task_u) {
		this.task_u = task_u;
	}

	public Integer getItem_seq_no() {
		return item_seq_no;
	}

	public void setItem_seq_no(Integer item_seq_no) {
		this.item_seq_no = item_seq_no;
	}

	public Integer getAu_id() {
		return au_id;
	}

	public void setAu_id(Integer au_id) {
		this.au_id = au_id;
	}

	public Integer getSu_id() {
		return su_id;
	}

	public void setSu_id(Integer su_id) {
		this.su_id = su_id;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public Timestamp getChklst_dt() {
		return chklst_dt;
	}

	public void setChklst_dt(Timestamp chklst_dt) {
		this.chklst_dt = chklst_dt;
	}

	public Integer getCycle_seq_no() {
		return cycle_seq_no;
	}

	public void setCycle_seq_no(Integer cycle_seq_no) {
		this.cycle_seq_no = cycle_seq_no;
	}

	public String getPacket_type() {
		return packet_type;
	}

	public void setPacket_type(String packet_type) {
		this.packet_type = packet_type;
	}

	public String getPrint_set_detail_c() {
		return print_set_detail_c;
	}

	public void setPrint_set_detail_c(String print_set_detail_c) {
		this.print_set_detail_c = print_set_detail_c;
	}

	public Integer getTot_crit() {
		return tot_crit;
	}

	public void setTot_crit(Integer tot_crit) {
		this.tot_crit = tot_crit;
	}

	public Integer getTot_ph1() {
		return tot_ph1;
	}

	public void setTot_ph1(Integer tot_ph1) {
		this.tot_ph1 = tot_ph1;
	}

	public Integer getTot_ph2() {
		return tot_ph2;
	}

	public void setTot_ph2(Integer tot_ph2) {
		this.tot_ph2 = tot_ph2;
	}

	public String getChklst_file_name() {
		return chklst_file_name;
	}

	public void setChklst_file_name(String chklst_file_name) {
		this.chklst_file_name = chklst_file_name;
	}

	public String getChklst_prn_file() {
		return chklst_prn_file;
	}

	public void setChklst_prn_file(String chklst_prn_file) {
		this.chklst_prn_file = chklst_prn_file;
	}

	@Override
	public String toString() {
		return "BasicChecklistEntity [task_u=" + task_u + ", item_seq_no=" + item_seq_no + ", au_id=" + au_id
				+ ", su_id=" + su_id + ", module=" + module + ", edition=" + edition + ", chklst_dt=" + chklst_dt
				+ ", cycle_seq_no=" + cycle_seq_no + ", packet_type=" + packet_type + ", print_set_detail_c="
				+ print_set_detail_c + ", tot_crit=" + tot_crit + ", tot_ph1=" + tot_ph1 + ", tot_ph2=" + tot_ph2
				+ ", chklst_file_name=" + chklst_file_name + ", chklst_prn_file=" + chklst_prn_file + "]";
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
				obj.setItem_seq_no(rs.getInt("ITEMSEQNO"));
				obj.setModule(rs.getString("CHKLIST"));
				obj.setAu_id(rs.getInt("AUID"));
				obj.setSu_id(rs.getInt("SUABE"));
				obj.setEdition(rs.getString("EDITION"));
				obj.setChklst_dt(rs.getTimestamp("CHKLSTDATE"));
				obj.setCycle_seq_no(rs.getInt("CYCLESEQNO"));
				obj.setPacket_type(rs.getString("PACKETTYPE"));
				obj.setPrint_set_detail_c(rs.getString("print_set_detail_c"));
				list.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

}
