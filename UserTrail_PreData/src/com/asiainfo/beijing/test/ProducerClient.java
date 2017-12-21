package com.asiainfo.beijing.test;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@SuppressWarnings({ "deprecation", "unused" })
public class ProducerClient {
	private static final SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final Logger LOG= LoggerFactory.getLogger(ProducerClient.class);
	private static KafkaProducer<Object,Object> producer = null;
	private static ProducerRecord<Object, Object> message = null;
	public static int count = 0;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Future sendMessage(String topic,String xmlMsg) {
		if (producer == null) {
			producer = ProducerFactory.getInstance();
		}
		Future result = null;
		try{
			message = new ProducerRecord<Object, Object>(topic,xmlMsg);
			//KeyedMessage<String, String> message = new KeyedMessage<String, String>(topic,xmlMsg);
			result = producer.send(message);
		}catch(Exception e){
			e.printStackTrace();
			LOG.error(e.getMessage());
			LOG.error("kafka send error:",e);
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println("start:"+format.format(new Date()));
		
		for (int i = 0; i < 10; i++) {
			Thread thread = new Thread(){
				public void run(){
					
					for (int i = 0; i < 1000; i++) {
						try {
							String s = ""+ new Date().getTime();
							String msg = "TEST";
							ProducerClient.sendMessage(
									"SGN_DATA", msg);
						} catch (Exception e) {
							System.out.println("发送失败");
							e.printStackTrace();
						}
					}
				}
			};
			thread.start();
		}
		System.out.println("over:"+format.format(new Date()));
	}

}
