package com.asiainfo.beijing.pojo;

import net.sf.json.JSONObject;

public class SgnData {
	private String accs_nmbr;
	private String bts_id;
	private String start_time;
	private String end_time;
	public SgnData sgnData = null;
	

	
//	public static SgnData singleTon(){
//		if(sgnData == null){
//			sgnData = new SgnData();
//			return sgnData;
//		}
//		return sgnData;
//	}
	
	public String getAccs_nmbr() {
		return accs_nmbr;
	}
	public void setAccs_nmbr(String accs_nmbr) {
		this.accs_nmbr = accs_nmbr;
	}
	public String getBts_id() {
		return bts_id;
	}
	public void setBts_id(String bts_id) {
		this.bts_id = bts_id;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public SgnData setData(SgnData sgnData,String accs_nmbr, String bts_id, String start_time,
			String end_time) {
		sgnData.setAccs_nmbr(accs_nmbr);
		sgnData.setBts_id(bts_id);
		sgnData.setStart_time(start_time);
		sgnData.setEnd_time(end_time);
		return sgnData;
	}

	
	
}
