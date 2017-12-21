package com.asiainfo.beijing.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 基站转换工具
 * @author jiangtao
 *
 */
public class DealCell{
	
	//private static final Logger _LOG = LoggerFactory.getLogger(DealCell.class);
	
	public static String transform3G(String bts_id,String bsc_code,
					Map<String,String> bts_btsIdMap,Map<String,String> nid_bscMap){
		String hex_bts_id = "";
		String bts_id_final = "";
		try{
			hex_bts_id = Integer.toHexString(Integer.parseInt(bts_id));
			hex_bts_id = hex_bts_id.substring(0, hex_bts_id.length() - 1);
			int bts_tmp = Integer.parseInt(hex_bts_id, 16);
			String nid = nid_bscMap.get(bsc_code);
		    if(nid != null && !"".equals(nid)){
		    	bts_id_final = bts_btsIdMap.get(bts_tmp+","+nid);
		    }
			
		}catch (Exception e) {
			//_LOG.error("基站处理失败，传入基站为"+bts_id);
		}
		return bts_id_final;
  }
	public static void main(String[] args){
		InitDimInfo idi = new InitDimInfo();
		idi.init();
		String bts_id = transform3G("39441","16581741",idi.bts_btsIdMap,idi.nid_bscMap);
		System.out.println(bts_id);
	  }
}
