package com.asiainfo.beijing.utils;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.beijing.pojo.SgnData;
import com.asiainfo.beijing.test.ProducerClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * DealDataClient是主逻辑控制类
 * <p>检查文件是否存在<br>
 *    根据文件名处理3G/4G数据<br>
 *    调用kafka客户端发送数据，取回返回值，判断是否成功</p>
 * @author jiangtao
 * @version 1.0
 *
 */
@SuppressWarnings("static-access")
public class DealDataClient {
	private static final Logger _LOG = LoggerFactory.getLogger(DealDataClient.class);
	private static final String SEQUENCE_3G = "shlt";
	private static final String SEQUENCE_4G = "S1MME";
	private static final String TOPIC = "SGN_DATA";
	
	private static final int EVENT_ID_3G = 1;
	private static final int START_TIME_3G = 6;
	private static final int END_TIME_3G = 7;
	private static final int BTS_ID_3G = 8;
	private static final int CALLING_NUM_3G = 12;
	private static final int CALLED_NUM_3G = 13;
	private static final int BSC_CODE_3G = 23;
	
	private static final int MSISDN_4G = 4;
	private static final int START_TIME_4G = 8;
	private static final int END_TIME_4G = 9;
	private static final int ENB_ID_4G = 37;
	
	private static String accs_nmbr;
	private static String event_id;
	private static String start_time;
	private static String end_time;
	private static String bts_id;
	private static String bsc_code;
	private static String enb_id;
	
	public static int count = 0;
	public static int dealCount = 0;
	public static int dealedCount = 0;
	private static File file;
	private static File failFile;
	private static String[] fields;
	public static KafkaProducerSingleton kafkaProducerSingleton;
	public static InitDimInfo initDim = new InitDimInfo();
	public static JSONObject json=new JSONObject();  
	public static SgnData sgnData = new SgnData();
	public static List<JSONObject> batchList = new ArrayList<JSONObject>();
	
	/**
	 * 初始化类的时候，读入维表数据，通过单例模式，初始化kafka客户端
	 */
	static{
		initDim.init();
		kafkaProducerSingleton = KafkaProducerSingleton  
                .getInstance();  
        kafkaProducerSingleton.init(TOPIC,2);  
	}
	
	public static String DealData(String baseUrl,String fileName){
		
		if(baseUrl == null || "".equals(baseUrl)){
			_LOG.error("文件路径为空");
			System.exit(-1);
		}
		if(fileName == null || "".equals(fileName)){
			_LOG.error("文件名不能为空");
			System.exit(-1);
		}
		
		_LOG.info("开始检查文件");
		file = legal(baseUrl,fileName);
		
		if(fileName.contains(SEQUENCE_3G)){
			//3g数据
			_LOG.info("开始处理3g数据，文件名字为"+fileName+",文件路径为"+baseUrl);
			deal3GData(file);
		}else if (fileName.contains(SEQUENCE_4G)){
			//4g数据
			_LOG.info("开始处理4g数据，文件名字为"+fileName+",文件路径为"+baseUrl);
			deal4GData(file);
		}
		return "";
	}
	
	/**
	 * 3G处理逻辑
	 * @param 输入的文件
	 */
	private static void deal3GData(File file){
		_LOG.info("开始读取文件");
		String msg = new String();
		InputStreamReader read = null;
		BufferedReader br = null;
		FileInputStream fis = null;
		FileWriter fw = null;
	    BufferedWriter bw = null;
		
		try{
			fis = new FileInputStream(file);
			read =new InputStreamReader(fis);
			br = new BufferedReader(read);
			String lineTxt = null;
			while((lineTxt = br.readLine()) != null){
				count += 1;
				fields = lineTxt.split(",");
				bts_id = fields[BTS_ID_3G];
				start_time = fields[START_TIME_3G];
				end_time = fields[END_TIME_3G];
				event_id = fields[EVENT_ID_3G];
				bsc_code = fields[BSC_CODE_3G];
				//'3','5','7','8','21','22','23','24','26'
				if(Integer.parseInt(event_id) == 3 || 
				   Integer.parseInt(event_id) == 5 ||
				   Integer.parseInt(event_id) == 7 ||
				   Integer.parseInt(event_id) == 8 ||
				   Integer.parseInt(event_id) == 21 ||
				   Integer.parseInt(event_id) == 22 ||
				   Integer.parseInt(event_id) == 23 ||
				   Integer.parseInt(event_id) == 24 ||
				   Integer.parseInt(event_id) == 26){
					accs_nmbr = fields[CALLED_NUM_3G];
				}else{
					accs_nmbr = fields[CALLING_NUM_3G];
				}
				bts_id = DealCell.transform3G(bts_id,bsc_code,
								initDim.bts_btsIdMap,initDim.nid_bscMap);
				start_time = CastTime.transform(start_time);
				end_time = CastTime.transform(end_time);
				if(accs_nmbr != null && !("".equals(accs_nmbr)) 
				   && !("".equals(start_time)) 
				   && !("".equals(end_time))
				   && !("".equals(bts_id))
				   && bts_id != null){
					//组装json
					if(!json.isEmpty()){
						json.remove("sgn_data");
					}
					sgnData.setData(sgnData,accs_nmbr, bts_id, start_time, end_time);
					json.put("sgn_data", sgnData);
					dealCount += 1;
	//				调用kafka procedu客户端
					@SuppressWarnings("rawtypes")
					Future result = kafkaProducerSingleton
									.sendKafkaMessage(json.toString());
					if(!result.isDone()){
						dealedCount += 1;
					}
//					if(!result.isDone()){
//						
//						if(failFile == null || !failFile.exists()){
//							failFile =  new File(file.getAbsolutePath()+"_fail");
//						}
//						if(fw == null){
//							fw = new FileWriter(failFile);
//						}
//						if(bw == null){
//							bw = new BufferedWriter(fw);
//						}
//					    bw.write(lineTxt+"\n");
//					}
				}
				
			}
		}catch(Exception e){
			//_LOG.error("读取文件失败");
		}finally{
			try{
				if(br != null) br.close();
				if(read != null) read.close();
				if(fis != null) fis.close();
				if(bw != null) bw.close();
				if(fw != null) fw.close();
			}catch(Exception e){
				_LOG.error("流关闭失败");
			}
		}
		
	}
	
	/**
	 * 4G处理逻辑
	 * @param file
	 */
	private static void deal4GData(File file){
		_LOG.info("开始读取文件");
		String msg = new String();
		InputStreamReader read = null;
		BufferedReader br = null;
		FileInputStream fis = null;
		FileWriter fw = null;
	    BufferedWriter bw = null;
		try{
			fis = new FileInputStream(file);
			read =new InputStreamReader(fis);
			br = new BufferedReader(read);
			String lineTxt = null;
			while((lineTxt = br.readLine()) != null){
				count +=1;
				fields = lineTxt.split("|");
				bts_id = fields[ENB_ID_4G];
				start_time = fields[START_TIME_4G];
				end_time = fields[END_TIME_4G];
				accs_nmbr = fields[MSISDN_4G];
				
				
				
				if(accs_nmbr != null && !("".equals(accs_nmbr)) 
				   &&!("".equals(start_time)) 
				   && !("".equals(end_time))
				   && !("".equals(bts_id))
				   && bts_id != null){
					//组装json
					
					if(!json.isEmpty()){
						json.remove("sgn_data");
					}
					sgnData.setData(sgnData, accs_nmbr, bts_id, start_time, end_time);
					
					json.put("sgn_data", sgnData);
					dealCount += 1;
					//调用kafka procedu客户端
					@SuppressWarnings("rawtypes")
					Future result = ProducerClient.sendMessage(TOPIC, json.toString());
					
					if(!result.isDone()){
						dealedCount += 1;
					}
					
//					if(!result.isDone()){
//						if(failFile == null){
//							failFile = new File(file.getAbsolutePath()+"_fail");
//						}
//						
//						if(fw == null){
//							fw = new FileWriter(failFile);
//						}
//						if(bw == null){
//							bw = new BufferedWriter(fw);
//						}
//					
//					    bw.write(lineTxt+"\n");
//					}
				}
				
			}
		}catch(Exception e){
			//_LOG.error("读取文件失败");
		}finally{
			try{
				if(br != null) br.close();
				if(read != null) read.close();
				if(fis != null) fis.close();
				if(bw != null) bw.close();
				if(fw != null) fw.close();
			}catch(Exception e){
				_LOG.error("流关闭失败");
			}
		}
		
	}
	
	private static File legal(String baseUrl, String fileName){
		file = new File(baseUrl+"/"+fileName);
		if(!file.isFile() && !file.exists()){
			_LOG.error("输入文件不存在"+file.getAbsolutePath());
			System.exit(-1);
		}
		if(!file.canRead()){
			_LOG.error("文件不可读"+file.getAbsolutePath());
			System.exit(-1);
		}
		return file;
	}
}
