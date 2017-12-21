package com.asiainfo.beijing.mainClass;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.asiainfo.beijing.utils.DealDataClient;

/**
 * PreData是项目主入口
 * <p>常规性判断</p>
 * @param 数据的绝对路径	格式：/a/b/c.txt
 * @author jiangtao
 * @version 1.0
 */
public class PreData {
	private static final Logger _LOG = LoggerFactory.getLogger(PreData.class);
	public static void main(String[] args) {
		if(args.length != 1){
			_LOG.error("输入参数有误,请输入数据的绝对路径");
			System.exit(-1);
		}
		String fileName = args[0].substring(args[0].lastIndexOf("/")+1);
		String baseUrl = args[0].substring(0,args[0].lastIndexOf("/"));
		
		if(!(fileName.contains("shlt") || fileName.contains("S1MME"))){
			_LOG.error("文件名有误");
			System.exit(-1);
		}

		long startTime = new Date().getTime();
		_LOG.info("开始处理数据，开始时间戳为"+startTime+",输入路径为："+baseUrl+",输入文件名为："+fileName);
		
		DealDataClient.DealData(baseUrl, fileName);
		
		long endTime = new Date().getTime();
		_LOG.info("本次处理完成，结束时间戳为"+endTime+",共耗时"+(startTime-endTime)
					+"毫秒,共输入"+DealDataClient.count
					+"数据，符合规则"+DealDataClient.dealCount
					+"数据，发送成功"+DealDataClient.dealedCount);

	}

}
