package com.asiainfo.beijing.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间转换工具
 * @author jiangtao
 *
 */
public class CastTime {
	public static String msTime = "0";
	public static String mTime = "";
	public static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMdd HH:mm:ss");
	public static Date date;
	public static Long finalTime;

	public static String transform(String dateTime) {
		try {
			if ((dateTime == null) || ("".equals(dateTime))) {

				System.exit(-1);
			}
			if (dateTime.indexOf(".") == -1) {
				mTime = dateTime;
			} else {
				msTime = dateTime.substring(dateTime.lastIndexOf(".") + 1);
				mTime = dateTime.substring(0, dateTime.lastIndexOf("."));
			}
			date = sdf.parse(mTime);
			finalTime = Long.valueOf(date.getTime()
					+ Long.parseLong(msTime));
		} catch (Exception e) {
		}
		return finalTime + "";
	}

	public static void main(String[] args) {
		new CastTime().transform("");
	}
}
