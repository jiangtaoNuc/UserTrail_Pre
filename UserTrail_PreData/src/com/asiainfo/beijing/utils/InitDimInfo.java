package com.asiainfo.beijing.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 初始化维表到map中
 * @param bts_btsIdMap bts	bts_id的关系表 键：bts,nid 值：bts_id
 * @param nid_btsMap   nid	bsc的关系表 键：bsc 值：nid
 * @author jiangtao
 *
 */
public class InitDimInfo {
	private static final Logger _LOG = LoggerFactory.getLogger(DealCell.class);
	public static Map<String,String> bts_btsIdMap = new HashMap<String,String>();
	public static Map<String,String> nid_bscMap = new HashMap<String,String>();
	public static void init(){
		_LOG.info("开始读取bts_btsId_relation.txt");
		InputStream inBts_btsId = InitDimInfo.class.getResourceAsStream("/bts_btsId_relation.txt");
		InputStreamReader read = null;
		BufferedReader br = null;
		try{
			read =new InputStreamReader(inBts_btsId);
			br = new BufferedReader(read);
			String lineTxt = null;
			while((lineTxt = br.readLine()) != null){
				String[] fields = lineTxt.split("\t");
				bts_btsIdMap.put(fields[0]+","+fields[1], fields[2]);
			}
		}catch(Exception e){
			_LOG.error("读取文件失败");
		}finally{
			try{
				if(br != null) br.close();
				if(read != null) read.close();
				if(inBts_btsId != null) inBts_btsId.close();
			}catch(Exception e){
				_LOG.error("流关闭失败");
			}
		}
	
		_LOG.info("开始读取nid_bsc.txt");
		InputStream inNid_bsc = InitDimInfo.class.getResourceAsStream("/nid_bsc.txt");
		InputStreamReader nidRead = null;
		BufferedReader nidBr = null;
		try{
			nidRead =new InputStreamReader(inNid_bsc);
			nidBr = new BufferedReader(nidRead);
			String lineTxt = null;
			while((lineTxt = nidBr.readLine()) != null){
				String[] fields = lineTxt.split("\t");
				nid_bscMap.put(fields[1], fields[0]);
				
			}
		}catch(Exception e){
			_LOG.error("读取文件失败");
		}finally{
			try{
				if(nidBr != null) nidBr.close();
				if(nidRead != null) nidRead.close();
				if(inNid_bsc != null) inNid_bsc.close();
			}catch(Exception e){
				_LOG.error("流关闭失败");
			}
			if(bts_btsIdMap.isEmpty() || nid_bscMap.isEmpty()){
				_LOG.error("读取维表数据失败，jar包文件异常");
				System.exit(-1);
			}
		}
		
	}
	public static void main(String[] args) {
		InitDimInfo idi = new InitDimInfo();
		idi.init();
		//System.out.println(idi.nid_bscMap);
		
	}
}
