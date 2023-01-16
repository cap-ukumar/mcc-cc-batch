package org.cap.cc.batch.dao;

import java.time.format.DateTimeFormatter;

public class CustomChecklistConstants {
	
	public static final String INSERT_LOG_MCC_DB = "INSERT INTO chklst_log ( task_u, chk_msg_type_c, chk_msg_t, created_dt, created_user, lastupdate_dt, lastupdate_user, created_pgm_c, updated_pgm_c, record_source ) VALUES ( ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ? );";

//	public static final String GET_CUSTOM_CHECKLIST_FILE_PATH = "SELECT Trim(c147.column_data_t) || DECODE (weekday(to_date(trim(c1.column_data_t),'%m/%d/%Y')) , 1, 'Monday', 2, 'Tuesday', 3, 'Wednesday', 4, 'Thursday', 5, 'Friday', 6, 'Saturday', 0, 'Sunday') || '/' as FilePath     FROM ptt_std_code_col c147, ptt_standard_codes s, ptt_std_code_col c1 WHERE      c147.table_u = 147      AND Trim(c147.key_u) = '39' AND Trim(c147.column_type_u)  = 'PATH'     AND s.table_u = 1 AND s.key_u = 'LAPCURRDT'  AND current BETWEEN s.effective_dt and s.termination_dt AND s.table_u = c1.table_u AND s.key_u = c1.key_u AND c1.column_type_u = 'DATE'  AND c1.column_data_t IS NOT NULL AND trim(c1.column_data_t) <> '';";
	
	public static final String GET_CUSTOM_CHECKLIST_FILE_PATH = "SELECT Trim(c147.column_data_t) || '\\' || DECODE (weekday(to_date(trim(c1.column_data_t),'%m/%d/%Y')) , 1, 'Monday', 2, 'Tuesday', 3, 'Wednesday', 4, 'Thursday', 5, 'Friday', 6, 'Saturday', 0, 'Sunday') || '\\' as FilePath     FROM ptt_std_code_col c147, ptt_standard_codes s, ptt_std_code_col c1 WHERE      c147.table_u = 147      AND Trim(c147.key_u) = '39' AND Trim(c147.column_type_u)  = 'PATH'     AND s.table_u = 1 AND s.key_u = 'LAPCURRDT'  AND current BETWEEN s.effective_dt and s.termination_dt AND s.table_u = c1.table_u AND s.key_u = c1.key_u AND c1.column_type_u = 'DATE'  AND c1.column_data_t IS NOT NULL AND trim(c1.column_data_t) <> '';";

	public static final String GET_TASK_ID = "SELECT Min (t.task_u) FROM ptt_task t, lpt_print_set_item m WHERE t.task_u = m.task_u AND t.busn_activity_u = 'CO000200'  AND t.initiated_dt IS NOT NULL  AND t.started_dt IS NULL   AND t.completed_dt IS NULL  AND t.update_user_u <> 'CUSTCHK'  AND (m.print_set_detail_c like 'CHECKLST%'  OR m.print_set_detail_c like 'CHECKLIST%' ) ;";

	public static final String UPDATE_USER_U = "UPDATE ptt_task SET special_instr_t = ?, update_user_u = 'CUSTCHK', last_update_dt = current,  update_pgm_c = ? WHERE busn_activity_u = 'CO000200' AND task_u = ? AND update_user_u <>  'CUSTCHK' ;";

	public static final String UPDATE_USER_U_VALUE = "CUSTCHK";

	public static final String GET_DUPLEX_VALUE = "SELECT ptt_std_code_col.column_data_t FROM ptt_standard_codes, ptt_std_code_col WHERE ptt_standard_codes.table_u = 201 AND ptt_standard_codes.key_u = ? AND ptt_std_code_col.column_type_u  = 'DUPLEXFLAG' AND ptt_standard_codes.table_u  = ptt_std_code_col.table_u AND ptt_standard_codes.key_u  = ptt_std_code_col.key_u AND ptt_standard_codes.effective_dt = ptt_std_code_col.effective_dt AND current between ptt_standard_codes.effective_dt AND ptt_standard_codes.termination_dt ;";

	public static final String GET_STAPLE_VALUE = "SELECT TRIM(cc.column_data_t) FROM ptt_standard_codes sc, ptt_std_code_col cc WHERE sc.table_u = 201 AND sc.key_u = ? AND cc.table_u = sc.table_u AND cc.key_u = sc.key_u AND cc.column_type_u = 'STAPLEFLAG' AND current BETWEEN sc.effective_dt AND sc.termination_dt ;";

	public static final String GET_MEDIA_COLOR = "SELECT c4.column_data_t FROM ptt_standard_codes s201, ptt_std_code_col c201, ptt_standard_codes s4, ptt_std_code_col c4 WHERE s201.table_u = 201 AND s201.key_u  = ? AND c201.column_type_u  = 'PG1COLORCD' AND s201.table_u  = c201.table_u AND s201.key_u  = c201.key_u AND s201.effective_dt = c201.effective_dt AND current between s201.effective_dt AND s201.termination_dt AND s4.table_u = 4 AND s4.table_u = c4.table_u AND s4.key_u = c4.key_u AND s4.effective_dt = c4.effective_dt AND current between s4.effective_dt AND s4.termination_dt AND c4.column_type_u  = 'COLORDESC' AND c4.key_u = c201.column_data_t;";

	public static final String GET_MEDIA_TYPE = "SELECT c203.column_data_t FROM ptt_standard_codes s201, ptt_std_code_col c201, ptt_std_code_col c201c, ptt_standard_codes s203, ptt_std_code_col c203, ptt_std_code_col c203c, ptt_std_code_col c203g, ptt_standard_codes s193, ptt_std_code_col c193 WHERE s201.table_u = 201 AND s201.key_u  = ? AND c201.column_type_u = 'PG1COLORCD' AND s201.table_u  = c201.table_u AND s201.key_u   = c201.key_u AND s201.effective_dt = c201.effective_dt AND s201.effective_dt = c201c.effective_dt AND current between s201.effective_dt AND s201.termination_dt AND c201c.table_u = c201.table_u AND c201c.key_u  = c201.key_u  AND c201c.column_type_u = 'PRTSETDEFN' AND Trim(c201c.column_data_t) = Trim(c193.key_u) AND s203.table_u = 203 AND s203.table_u = c203.table_u AND s203.key_u  = c203.key_u AND s203.effective_dt = c203.effective_dt AND s203.effective_dt = c203c.effective_dt AND current between s203.effective_dt AND s203.termination_dt AND c203.column_type_u = 'PAPERTYPE' AND c203c.table_u = c203.table_u AND c203c.key_u  = c203.key_u AND c203c.column_type_u = 'COLOR' AND c203c.column_data_t = c201.column_data_t AND c203g.table_u = s203.table_u AND s203.key_u = c203g.key_u AND s203.effective_dt = c203g.effective_dt AND c203g.key_u = c203c.key_u AND c203g.column_type_u = 'COLLGRP' AND c203g.column_data_t = c193.column_data_t AND s193.table_u = 193 AND s193.table_u = c193.table_u AND s193.key_u  = c193.key_u AND s193.effective_dt = c193.effective_dt AND current between s193.effective_dt AND s193.termination_dt AND c193.column_type_u = 'COLLGRP';";

	public static final String GET_CONTENT_CHANNEL = "SELECT cc.column_data_t as ls_content, cc2.column_data_t as ls_channel FROM ptt_standard_codes sc, ptt_std_code_col cc, ptt_std_code_col cc2 WHERE sc.table_u = 193 AND sc.key_u = ? AND cc.table_u = sc.table_u AND cc.key_u = sc.key_u AND cc2.table_u = sc.table_u AND cc2.key_u = sc.key_u AND cc.column_type_u = 'CLCONTENT' AND cc2.column_type_u = 'CLCHANNEL' AND current BETWEEN sc.effective_dt AND sc.termination_dt ;";

	public static final String GET_BASIC_CHECKLIST_DETAILS = "SELECT M.set_item_seq_no_u AS ITEMSEQNO, Trim(A.addtnl_data_t) AS CHKLIST, Trim(B.addtnl_data_t) AS AUID, Trim(C.addtnl_data_t) AS SUABE, Trim(D.addtnl_data_t) AS EDITION, Trim(F.addtnl_data_t) AS CHKLSTDATE, Trim(G.addtnl_data_t) AS CYCLESEQNO, Trim(P.column_data_t) AS PACKETTYPE, Trim(M.print_set_detail_c) as print_set_detail_c FROM  lpt_print_set_item M, ptt_std_code_col P, lpt_addtnl_d_value A, lpt_addtnl_d_value B, lpt_addtnl_d_value C, lpt_addtnl_d_value D, lpt_addtnl_d_value E, lpt_addtnl_d_value F,  lpt_addtnl_d_value G WHERE (M.task_u = ? ) AND (M.print_set_detail_c like 'CHECKLST%' OR M.print_set_detail_c like 'CHECKLIST%') AND M.print_set_detail_c = P.key_u AND P.table_u = 201  AND P.column_type_u = 'PRTSETDEFN' AND M.addtnl_data_u = A.addtnl_data_u  AND A.addtnl_data_fld_c = 'CHKLIST' AND (B.addtnl_data_u = A.addtnl_data_u AND B.addtnl_data_fld_c = 'AUID')  AND (C.addtnl_data_u = A.addtnl_data_u AND C.addtnl_data_fld_c = 'SUABE')  AND (D.addtnl_data_u = A.addtnl_data_u AND D.addtnl_data_fld_c = 'EDITION')  AND (E.addtnl_data_u = A.addtnl_data_u AND E.addtnl_data_fld_c = 'CUSTCHKLST' AND E.addtnl_data_t = 'Y') AND (F.addtnl_data_u = A.addtnl_data_u AND F.addtnl_data_fld_c = 'CHKLSTDATE') AND (G.addtnl_data_u = A.addtnl_data_u AND G.addtnl_data_fld_c = 'SEQNBR') ;";

	public static final String GET_CAP_DOMAIN = "SELECT Trim(cc.column_data_t)  FROM ptt_standard_codes sc, ptt_std_code_col cc WHERE sc.table_u = 569 AND cc.table_u = sc.table_u AND sc.key_u = '04' AND sc.active_s = 'A' AND cc.key_u = sc.key_u   AND cc.column_type_u = 'WEBSETTING';";

	public static final String GET_CHECKLIST_WEBSERVICE_URL = "SELECT Trim( ptt_std_code_col.column_data_t ) FROM ptt_std_code_col,  ptt_standard_codes  WHERE ( ptt_standard_codes.table_u = ptt_std_code_col.table_u ) and  ( ptt_standard_codes.key_u = ptt_std_code_col.key_u ) and  ( ptt_standard_codes.effective_dt = ptt_std_code_col.effective_dt ) and  ( current BETWEEN ptt_standard_codes.effective_dt and ptt_standard_codes.termination_dt ) and ( ( ptt_std_code_col.table_u = 147 ) and  ( ptt_standard_codes.key_u = 5 ) and  ( ptt_std_code_col.column_type_u = 'CHKLST' ) );";

	public static final String GET_JOB_STATUS_POLLING_INTERVAL = "SELECT TRIM(cc.column_data_t) / 1000 FROM ptt_standard_codes sc, ptt_std_code_col cc WHERE sc.table_u = 569 AND sc.key_u = '09' AND cc.table_u = sc.table_u AND cc.key_u = sc.key_u AND cc.column_type_u = 'WEBSETTING' AND sc.active_s = 'A' ; ";

	public static final String GET_CHECKLIST_INSPECTOR_CHANNEL = "SELECT chk_insr_chnl_f  FROM lpt_chklst_edition  WHERE chklst_edition_u = ? ;";

	public static final String GET_JOB_COMPLETION_ITERATIONS = "SELECT TRIM(cc.column_data_t) + 0 FROM ptt_standard_codes sc, ptt_std_code_col cc WHERE sc.table_u = 569 AND sc.key_u = '11' AND cc.table_u = sc.table_u AND cc.key_u = sc.key_u AND cc.column_type_u = 'WEBSETTING' AND sc.active_s = 'A' ;";
	
	public static final String INSERT_AUDIT_CHECKLIST = "INSERT INTO lpt_chklst_audit ( abe_au_u, print_us_reg_qst_f, abe_su_u, module_key_c, chklst_edition_u, lap_packet_type_c, chklst_type_c, supl_from_dt, supl_from_audit_u, chklst_eff_dt, seq_no_u, tot_qst_cust_ph1_q, tot_qst_cust_ph2_q, tot_qst_cust_cri_q, tot_qst_supl_ph1_q, tot_qst_supl_ph2_q, tot_qst_supl_cri_q, chklst_creation_dt, last_update_dt, update_user_u, invoking_pgm_c, update_pgm_c ) VALUES ( ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );";
	
	public static final String INSERT_AUDIT_CHECKLIST_MCC_DB = "INSERT INTO CHKLST_AUDIT ( TASK_U, ITEM_SEQ_U, AU_U, SU_U, CHKLST_MODULE_C, CHKLST_EDITION_U, CHKLST_EDITION_DT, CYCLE_SEQ_NO_U, LAP_PKT_TYPE_C, CHKLST_JSON_N, ACTIVE_F, CREATED_DT, CREATED_USER, LASTUPDATE_DT, LASTUPDATE_USER, CREATED_PGM_C, UPDATED_PGM_C, RECORD_SOURCE ) VALUES ( ?,  ?, ? , ?, ?, ?, ?, ?, ? , ?::json, ?, ?, ?, ?, ?, ?, ?, ? );";
	
	public static final String GET_AUDIT_RECORDS_OF_PACKET = "SELECT   chklst_audit_u,   abe_au_u,   print_us_reg_qst_f,   abe_su_u,   module_key_c,   chklst_edition_u,   lap_packet_type_c,   chklst_type_c,   supl_from_dt,   supl_from_audit_u,   chklst_eff_dt,   seq_no_u,   tot_qst_cust_ph1_q,   tot_qst_cust_ph2_q,   tot_qst_supl_ph1_q,   tot_qst_supl_ph2_q,   tot_qst_cust_cri_q,   tot_qst_supl_cri_q,    chklst_creation_dt,   last_update_dt,   update_user_u,   invoking_pgm_c,   update_pgm_c FROM  lpt_chklst_audit  WHERE (   abe_au_u = ? ) AND  (   abe_su_u = ? ) ;";

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

	public static final DateTimeFormatter DB_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static final int PROGRAM_ID = 3001;

	public static final String ITEM_SEQ_NO = "ITEMSEQNO";

	public static final String MODULE_ID = "CHKLIST";

	public static final String AU_ID = "AUID";

	public static final String SU_ID = "SUABE";

	public static final String EDITION_ID = "EDITION";

	public static final String ACT_EFFECTIVE_DT = "CHKLSTDATE";

	public static final String CYCLE_SEQ_NO = "CYCLESEQNO";

	public static final String PACKET_TYPE = "PACKETTYPE";

	public static final String PRINT_SET_DETAIL_C = "print_set_detail_c";

	public static final String LS_CONTENT = "ls_content";

	public static final String LS_CHANNEL = "ls_channel";

	public static final String CHECKLIST_INSPECTOR_FLAG = "y";

	public static final String CHECKLIST_INSPECTOR_CONTENT = "CUSTOM";

	public static final String CHECKLIST_INSPECTOR_CHANNEL = "IPDFFINAL";

	public static final String STAPLE_VALUE = "N";

	public static final String US_REG_FLAG = "Y";

	public static final String CHKLST_TYPE_U = "CUST";

	public static final String CHANNEL_DATA_PDF = "IPDFFINAL";

	public static final String EXTENSION_PDF = "pdf";
	
	public static final String UNDERSCORE = "_";
	
	public static final String DOT = ".";
	
	//Logging constants
	
	public static final String LOG_DIVIDER = "-----------------------------------------------------------------";
	
	public static final String LOG_CC_BATCH_STARTED = " - C C B A T C H   S T A R T E D";
	
	public static final String LOG_CC_BATCH_FINISHED = " - C C B A T C H   F I N I S H E D";
	
	public static final String LOG_MSG_TYPE_INFORMATIONAL = "I";
	
	public static final String LOG_MSG_TYPE_WARNING = "W";
	
	public static final String LOG_MSG_TYPE_FAILED = "F";
	
	public static final String LOG_STARTED_PROCESSING_TASK = " - Started processing task %d by submitting checklists:";
	
	public static final String LOG_CHECKLIST_DETAILS =  "- %s, Edition: %s of AU: %s, SU: %s";

	public static final String LOG_TOTAL_PROCESSED_TASKS = " - Total processed tasks:	";

	public static final String LOG_TOTAL_FAILED_TASKS = " - Total failed tasks:	";

	public static final String LOG_STARTED_CHECKING_THUNDERHEAD_JOB_STATUS = " - Started checking Thunderhead job status for the task %d";
	
	public static final String LOG_FINISHED_CHECKING_THUNDERHEAD_JOB_STATUS = " - Returned %s Thunderhead job status for the task %s";

	public static final String SOURCE = "Ccbatch";

	public static final String LOG_ERROR_DATE_TIME = "*** Error Date/Time	= %s";

	public static final String LOG_ERROR_INSTANCE_NAME = "*** Instance Name	= %s";

	public static final String LOG_ERROR_CLASS_NAME = "*** Class Name		= %s";

	public static final String LOG_ERROR_FUNCTION_NAME = "*** Function Name	= %s";

	public static final String LOG_ERROR_CODE = "*** Error Code		= %d";

	public static final String LOG_ERROR_TEXT = "*** Error Text		= %s";

	public static final String EMPTY_STRING = "\" \"";
	
	

}
