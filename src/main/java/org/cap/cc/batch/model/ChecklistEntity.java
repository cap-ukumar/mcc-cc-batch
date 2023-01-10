package org.cap.cc.batch.model;

import java.sql.Timestamp;

public class ChecklistEntity {

	private Integer chklst_log_u;
	private Integer task_u;
	private String chk_msg_type_c;
	private String chk_msg_t;
	private Timestamp created_dt;
	private String created_user;
	private Timestamp lastupdate_dt;
	private String lastupdate_user;
	private Integer created_pgm_c;
	private Integer updated_pgm_c;
	private String record_source;

	public Integer getChklst_log_u() {
		return chklst_log_u;
	}

	public void setChklst_log_u(Integer chklst_log_u) {
		this.chklst_log_u = chklst_log_u;
	}

	public Integer getTask_u() {
		return task_u;
	}

	public void setTask_u(Integer task_u) {
		this.task_u = task_u;
	}

	public String getChk_msg_type_c() {
		return chk_msg_type_c;
	}

	public void setChk_msg_type_c(String chk_msg_type_c) {
		this.chk_msg_type_c = chk_msg_type_c;
	}

	public String getChk_msg_t() {
		return chk_msg_t;
	}

	public void setChk_msg_t(String chk_msg_t) {
		this.chk_msg_t = chk_msg_t;
	}

	public Timestamp getCreated_dt() {
		return created_dt;
	}

	public void setCreated_dt(Timestamp created_dt) {
		this.created_dt = created_dt;
	}

	public String getCreated_user() {
		return created_user;
	}

	public void setCreated_user(String created_user) {
		this.created_user = created_user;
	}

	public Timestamp getLastupdate_dt() {
		return lastupdate_dt;
	}

	public void setLastupdate_dt(Timestamp lastupdate_dt) {
		this.lastupdate_dt = lastupdate_dt;
	}

	public String getLastupdate_user() {
		return lastupdate_user;
	}

	public void setLastupdate_user(String lastupdate_user) {
		this.lastupdate_user = lastupdate_user;
	}

	public Integer getCreated_pgm_c() {
		return created_pgm_c;
	}

	public void setCreated_pgm_c(Integer created_pgm_c) {
		this.created_pgm_c = created_pgm_c;
	}

	public Integer getUpdated_pgm_c() {
		return updated_pgm_c;
	}

	public void setUpdated_pgm_c(Integer updated_pgm_c) {
		this.updated_pgm_c = updated_pgm_c;
	}

	public String getRecord_source() {
		return record_source;
	}

	public void setRecord_source(String record_source) {
		this.record_source = record_source;
	}
	
	
	

	@Override
	public String toString() {
		return "ChecklistEntity [chklst_log_u=" + chklst_log_u + ", task_u=" + task_u
				+ ", chk_msg_type_c=" + chk_msg_type_c + ", chk_msg_t=" + chk_msg_t + ", created_dt="
				+ created_dt + ", created_user=" + created_user + ", lastupdate_dt=" + lastupdate_dt
				+ ", lastupdate_user=" + lastupdate_user + ", created_pgm_c=" + created_pgm_c + ", updated_pgm_c="
				+ updated_pgm_c + ", record_source=" + record_source +"]";
	}

}
