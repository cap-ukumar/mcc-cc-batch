package org.cap.cc.batch.utils;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

public class CommonUtils {

	public static Properties configs;

	private static String s_UUID = null;

	static {
		try {
			configs = new Properties();
			configs.load(CommonUtils.class.getResourceAsStream("/Files/CustomChecklist.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		return configs.getProperty(key);
	}

	// You can use special_instr_t CHAR(255) in ptt_task table for checklist batch.
	public static String getUUID() {
		if (s_UUID == null) {
			s_UUID = UUID.randomUUID().toString();
		}

		return s_UUID;
	}

}
