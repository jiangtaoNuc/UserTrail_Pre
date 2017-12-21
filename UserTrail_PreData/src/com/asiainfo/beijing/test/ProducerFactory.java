package com.asiainfo.beijing.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@SuppressWarnings("deprecation")
public class ProducerFactory {
	private static final Logger _LOG=LoggerFactory.getLogger(ProducerFactory.class);
	private static KafkaProducer<Object, Object> producer=null;
	private static Properties props=new Properties();
	static{
		InputStream in=null;
		try {
			_LOG.debug("(load default config:kafka.default)");
			in=ProducerFactory.class.getResourceAsStream("/kafka.default");
			props.load(in);
		} catch (IOException e) {
			_LOG.error(e.getMessage());
			_LOG.error("初始化Producerfactory失败");
		}finally{
			try {
				if(in!=null){
					in.close();
					in=null;
				}
			} catch (IOException e) {
				_LOG.error(e.getMessage());
				_LOG.error("(close config file exception)",e);
			}
		}
		_LOG.debug("创建producer");
		producer= new KafkaProducer<>(props);
		

		Runtime.getRuntime().addShutdownHook(new ProducerHook());
	}
	
	
	
	public static KafkaProducer<Object,Object> getInstance(){
		return producer;
	}
	
	public static void closeInstance(){
		_LOG.debug("close producer");
		if(producer!=null){
			producer.close();
		}
	}
	
}
class ProducerHook extends Thread{

	@Override
	public void run() {
		System.out.println("关闭producer");
		ProducerFactory.closeInstance();
	}
	
}
