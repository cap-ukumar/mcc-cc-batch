package org.cap.cc.batch.dao;

public class CustomChecklistDAO {

	 
	public static final String GET_CUSTOM_CHECKLIST_FILE_PATH="SELECT Trim(c147.column_data_t) || "
			+ " DECODE (weekday(to_date(trim(c1.column_data_t),'%m/%d/%Y')) , 1, \"Monday\", 2, \"Tuesday\", "
			+ " 3, \"Wednesday\", 4, \"Thursday\", 5, \"Friday\", 6, \"Saturday\", 0, \"Sunday\") "
			+ " || \"/\" as FilePath "
			+ " FROM ptt_std_code_col c147, "
			+ " ptt_standard_codes s, "
			+ " ptt_std_code_col c1 "
			+ " WHERE c147.table_u = 147  "
			+ " AND Trim(c147.key_u) = \"39\" "
			+ " AND Trim(c147.column_type_u)  = \"PATH\" "
			+ " AND s.table_u = 1 "
			+ " AND s.key_u = \"LAPCURRDT\" "
			+ "	AND current BETWEEN s.effective_dt and s.termination_dt "
			+ "	AND s.table_u = c1.table_u "
			+ "	AND s.key_u = c1.key_u "
			+ "	AND c1.column_type_u = \"DATE\" "
			+ "	AND c1.column_data_t IS NOT NULL "
			+ "	AND trim(c1.column_data_t) <> \"\" ;";
	
	public static final String GET_TASK_ID ="SELECT Min (t.task_u)\n"
			+ "FROM	ptt_task t,\n"
			+ "lpt_print_set_item m\n"
			+ "WHERE t.task_u = m.task_u \n"
			+ "AND t.busn_activity_u = 'CO000200'  \n"
			+ "AND t.initiated_dt IS NOT NULL  \n"
			+ "AND t.started_dt IS NULL   \n"
			+ "AND t.completed_dt IS NULL  \n"
			+ "AND t.update_user_u <> 'CUSTCHK'  \n"
			+ "AND (m.print_set_detail_c like 'CHECKLST%'  OR m.print_set_detail_c like 'CHECKLIST%' )\n"
			+ ";\n"
			+ "";
	
	public static final String UPDATE_USER_U = "UPDATE ptt_task\n"
			+ "SET ptt_task.update_user_u = 'CUSTCHK',\n"
			+ "	last_update_dt = current,\n"
			+ "	update_pgm_c = :<ProgramID>\n"
			+ "WHERE ptt_task.busn_activity_u = 'CO000200' AND\n"
			+ "	ptt_task.task_u = :<task_u> AND\n"
			+ "	ptt_task.update_user_u <> : 'CUSTCHK'\n"
			+ "";
	

	public static final String GET_DUPLEX_VALUE = "SELECT ptt_std_code_col.column_data_t\r\n"
			+ "INTO ls_duplex_f"
			+ "FROM ptt_standard_codes,"
			+ "ptt_std_code_col\r\n"
			+ "WHERE ptt_standard_codes.table_u  =  201 "
			+ "  AND ptt_standard_codes.key_u        = ?"
			+ "  AND ptt_std_code_col.column_type_u  = 'DUPLEXFLAG'\r\n"
			+ "  AND ptt_standard_codes.table_u      = ptt_std_code_col.table_u\r\n"
			+ "  AND ptt_standard_codes.key_u        = ptt_std_code_col.key_u\r\n"
			+ "  AND ptt_standard_codes.effective_dt = ptt_std_code_col.effective_dt\r\n"
			+ "  AND current between ptt_standard_codes.effective_dt AND ptt_standard_codes.termination_dt ;\r\n"
			+ "";
	
	public static final String GET_STAPLE_VALUE = "SELECT TRIM(cc.column_data_t)\r\n"
			+ "  INTO :ls_staple_f\r\n"
			+ "FROM ptt_standard_codes sc,\r\n"
			+ "ptt_std_code_col cc\r\n"
			+ "WHERE sc.table_u = 201\r\n"
			+ "AND sc.key_u = :as_print_set_detail_c\r\n"
			+ "AND cc.table_u = sc.table_u\r\n"
			+ "AND cc.key_u = sc.key_u\r\n"
			+ "AND cc.column_type_u = \"STAPLEFLAG\"\r\n"
			+ "AND current BETWEEN sc.effective_dt AND sc.termination_dt ;\r\n"
			+ "";
	
	public static final String GET_MEDIA_COLOR = "SELECT c4.column_data_t\r\n"
			+ "INTO:ls_color\r\n"
			+ "FROM ptt_standard_codes s201,\r\n"
			+ "	ptt_std_code_col c201,\r\n"
			+ "	ptt_standard_codes s4,\r\n"
			+ "	ptt_std_code_col c4\r\n"
			+ "\r\n"
			+ "WHERE s201.table_u  =  201\r\n"
			+ "  AND s201.key_u        = #{printdetailId} c\r\n"
			+ "  AND c201.column_type_u  = 'PG1COLORCD'\r\n"
			+ "  AND s201.table_u      = c201.table_u\r\n"
			+ "  AND s201.key_u        = c201.key_u\r\n"
			+ "  AND s201.effective_dt = c201.effective_dt\r\n"
			+ "  AND current between s201.effective_dt AND s201.termination_dt \r\n"
			+ "  AND s4.table_u =  4\r\n"
			+ "  AND s4.table_u = c4.table_u\r\n"
			+ "  AND s4.key_u   = c4.key_u\r\n"
			+ "  AND s4.effective_dt = c4.effective_dt\r\n"
			+ "  AND current between s4.effective_dt AND s4.termination_dt\r\n"
			+ "  AND c4.column_type_u  = 'COLORDESC'\r\n"
			+ "  AND c4.key_u = c201.column_data_t\r\n"
			+ "  ; \r\n"
			+ "";
	
	public static final String GET_MEDIA_TYPE = "SELECT c203.column_data_t\r\n"
			+ "INTO :ls_mediatype\r\n"
			+ "FROM ptt_standard_codes 	s201,\r\n"
			+ "ptt_std_code_col 	c201,\r\n"
			+ "ptt_std_code_col 	c201c,\r\n"
			+ "ptt_standard_codes 	s203,\r\n"
			+ "ptt_std_code_col 	c203,\r\n"
			+ "ptt_std_code_col 	c203c,\r\n"
			+ "ptt_std_code_col 	c203g,\r\n"
			+ "ptt_standard_codes 	s193,\r\n"
			+ "ptt_std_code_col 	c193\r\n"
			+ "\r\n"
			+ "WHERE s201.table_u  =  201\r\n"
			+ "  AND s201.key_u        = #{printdetailId}  // 'CHECKLSTSP', 'CHECKLSTAI'\r\n"
			+ "  AND c201.column_type_u = 'PG1COLORCD'\r\n"
			+ "  AND s201.table_u      = c201.table_u\r\n"
			+ "  AND s201.key_u        = c201.key_u\r\n"
			+ "  AND s201.effective_dt = c201.effective_dt\r\n"
			+ "  AND s201.effective_dt = c201c.effective_dt\r\n"
			+ "  AND current between s201.effective_dt AND s201.termination_dt \r\n"
			+ "  AND c201c.table_u = c201.table_u\r\n"
			+ "  AND c201c.key_u   = c201.key_u\r\n"
			+ "  AND c201c.column_type_u = 'PRTSETDEFN'\r\n"
			+ "  AND Trim(c201c.column_data_t) = Trim(c193.key_u)\r\n"
			+ " \r\n"
			+ "  AND s203.table_u = 203\r\n"
			+ "  AND s203.table_u = c203.table_u\r\n"
			+ "  AND s203.key_u   = c203.key_u\r\n"
			+ "  AND s203.effective_dt = c203.effective_dt\r\n"
			+ "  AND s203.effective_dt = c203c.effective_dt\r\n"
			+ "  AND current between s203.effective_dt AND s203.termination_dt\r\n"
			+ "  AND c203.column_type_u = 'PAPERTYPE'\r\n"
			+ "  AND c203c.table_u = c203.table_u\r\n"
			+ "  AND c203c.key_u   = c203.key_u\r\n"
			+ "  AND c203c.column_type_u = 'COLOR'\r\n"
			+ "  AND c203c.column_data_t = c201.column_data_t\r\n"
			+ "  \r\n"
			+ "  AND c203g.table_u = s203.table_u\r\n"
			+ "  AND s203.key_u = c203g.key_u \r\n"
			+ "  AND s203.effective_dt = c203g.effective_dt\r\n"
			+ "  AND c203g.key_u = c203c.key_u\r\n"
			+ "  AND c203g.column_type_u = 'COLLGRP'\r\n"
			+ "\r\n"
			+ "  AND c203g.column_data_t = c193.column_data_t\r\n"
			+ "  AND s193.table_u = 193\r\n"
			+ "  AND s193.table_u = c193.table_u\r\n"
			+ "  AND s193.key_u   = c193.key_u\r\n"
			+ "  AND s193.effective_dt = c193.effective_dt\r\n"
			+ "  AND current between s193.effective_dt AND s193.termination_dt\r\n"
			+ "  AND c193.column_type_u = 'COLLGRP'\r\n"
			+ "  ;\r\n"
			+ "";
	
	public static final String GET_CHECKLIST_CONTENT = "SELECT cc.column_data_t, cc2.column_data_t\r\n"
			+ "INTO	:ls_content,:ls_channel\r\n"
			+ "\r\n"
			+ "  FROM ptt_standard_codes sc,\r\n"
			+ " ptt_std_code_col cc,\r\n"
			+ "ptt_std_code_col cc2\r\n"
			+ "\r\n"
			+ " WHERE sc.table_u = 193\r\n"
			+ "   AND sc.key_u = :#{printdetailId} \r\n"
			+ "   AND cc.table_u = sc.table_u\r\n"
			+ "   AND cc.key_u = sc.key_u\r\n"
			+ "   AND cc2.table_u = sc.table_u\r\n"
			+ "   AND cc2.key_u = sc.key_u\r\n"
			+ "   AND cc.column_type_u = \"CLCONTENT\"\r\n"
			+ "   AND cc2.column_type_u = \"CLCHANNEL\"\r\n"
			+ "   AND current BETWEEN sc.effective_dt AND sc.termination_dt \r\n"
			+ ";\r\n"
			+ "";

	public static final String GET_BASIC_CHECKLIST_DETAILS = " SELECT M.set_item_seq_no_u AS ITEMSEQNO,\n"
			+ "	Trim(A.addtnl_data_t) AS CHKLIST,\n"
			+ "	Trim(B.addtnl_data_t) AS AUID,\n"
			+ "	Trim(C.addtnl_data_t) AS SUABE,\n"
			+ "	Trim(D.addtnl_data_t) AS EDITION,\n"
			+ "	Trim(F.addtnl_data_t) AS CHKLSTDATE,\n"
			+ "	Trim(G.addtnl_data_t) AS CYCLESEQNO,\n"
			+ "	Trim(P.column_data_t) AS PACKETTYPE,\n"
			+ "	Trim(M.print_set_detail_c) as print_set_detail_c\n"
			+ "\n"
			+ "FROM  lpt_print_set_item M,\n"
			+ "	ptt_std_code_col P,\n"
			+ "	lpt_addtnl_d_value A,\n"
			+ "	lpt_addtnl_d_value B,\n"
			+ "	lpt_addtnl_d_value C,\n"
			+ "	lpt_addtnl_d_value D,\n"
			+ "	lpt_addtnl_d_value E,\n"
			+ "	lpt_addtnl_d_value F,\n"
			+ "	lpt_addtnl_d_value G\n"
			+ "\n"
			+ "WHERE (M.task_u = ? ) \n"
			+ "AND (M.print_set_detail_c like 'CHECKLST%' OR M.print_set_detail_c like 'CHECKLIST%') \n"
			+ "AND M.print_set_detail_c = P.key_u\n"
			+ "AND P.table_u = 201  \n"
			+ "AND P.column_type_u = 'PRTSETDEFN'\n"
			+ "AND M.addtnl_data_u = A.addtnl_data_u  \n"
			+ "AND A.addtnl_data_fld_c = 'CHKLIST'  \n"
			+ "AND (C.addtnl_data_u = A.addtnl_data_u AND C.addtnl_data_fld_c = 'SUABE')\n"
			+ "AND (B.addtnl_data_u = A.addtnl_data_u AND B.addtnl_data_fld_c = 'AUID')\n"
			+ "AND (D.addtnl_data_u = A.addtnl_data_u AND D.addtnl_data_fld_c = 'EDITION')  \n"
			+ "AND (E.addtnl_data_u = A.addtnl_data_u AND E.addtnl_data_fld_c = 'CUSTCHKLST' AND E.addtnl_data_t = 'Y')\n"
			+ "AND (F.addtnl_data_u = A.addtnl_data_u AND F.addtnl_data_fld_c = 'CHKLSTDATE') \n"
			+ "AND (G.addtnl_data_u = A.addtnl_data_u AND G.addtnl_data_fld_c = 'SEQNBR')\n"
			+ "\n"
			+ ";";
	
	public static final String GET_CAP_DOMAIN="SELECT Trim(cc.column_data_t) "
			+ " AS ls_domain "
			+ " FROM ptt_standard_codes sc, "
			+ " ptt_std_code_col cc "
			+ " WHERE sc.table_u = 569 "
			+ " AND cc.table_u = sc.table_u "
			+ " AND sc.key_u = \"04\" "
			+ " AND sc.active_s = \"A\" "
			+ " AND cc.key_u = sc.key_u "
			+ " AND cc.column_type_u = \"WEBSETTING\" ;";
			
	public static final String GET_CHECKLIST_WEBSERVICE_URL="SELECT Trim( ptt_std_code_col.column_data_t ) "
			+ " AS ls_url "
			+ " FROM	ptt_std_code_col, "
			+ "	ptt_standard_codes "
			+ " WHERE ( ptt_standard_codes.table_u = ptt_std_code_col.table_u ) and "
			+ "	( ptt_standard_codes.key_u = ptt_std_code_col.key_u ) and "
			+ "	( ptt_standard_codes.effective_dt = ptt_std_code_col.effective_dt ) and "
			+ "	( current BETWEEN ptt_standard_codes.effective_dt and ptt_standard_codes.termination_dt ) and "
			+ "	( ( ptt_std_code_col.table_u = 147 ) and "
			+ "	( ptt_standard_codes.key_u = 5 ) and "
			+ "	( ptt_std_code_col.column_type_u = 'CHKLST' ) ) ;";




}
