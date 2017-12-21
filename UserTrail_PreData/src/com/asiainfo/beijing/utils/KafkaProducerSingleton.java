package com.asiainfo.beijing.utils;

import java.io.IOException;  
import java.io.InputStream;  
import java.util.Properties;  
import java.util.Random;   
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.Callback;  
import org.apache.kafka.clients.producer.KafkaProducer;  
import org.apache.kafka.clients.producer.ProducerRecord;  
import org.apache.kafka.clients.producer.RecordMetadata;  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  

/**
 * kafka客户端
 * <p>单例模式<br>
 * 	  自控制的retry</p>
 * @author jiangtao
 *
 */
public final class KafkaProducerSingleton {  
  
    private static final Logger LOGGER = LoggerFactory  
            .getLogger(KafkaProducerSingleton.class);  
  
    private static KafkaProducer<String, String> kafkaProducer;  
    
  
    private String topic;  
  
    private int retry;  
  
    private KafkaProducerSingleton() {  
  
    }  
      
  
    private static class LazyHandler {  
  
        private static final KafkaProducerSingleton instance = new KafkaProducerSingleton();  
    }  
  
    /** 
     * 单例模式,kafkaProducer是线程安全的,可以多线程共享一个实例 
     *  
     * @return 
     */  
    public static final KafkaProducerSingleton getInstance() {  
        return LazyHandler.instance;  
    }  
  
    /** 
     * kafka生产者进行初始化 
     *  
     * @return KafkaProducer 
     */  
    public void init(String topic,int retry) {  
        this.topic = topic;  
        this.retry = retry;  
        if (null == kafkaProducer) {  
            Properties props = new Properties();  
            InputStream inStream = null;  
            try {  
                inStream = this.getClass()
                        .getResourceAsStream("/kafka.default");  
                props.load(inStream);  
                kafkaProducer = new KafkaProducer<String, String>(props);  
            } catch (IOException e) {  
                LOGGER.error("kafkaProducer初始化失败:" + e.getMessage(), e);  
            } finally {  
                if (null != inStream) {  
                    try {  
                        inStream.close();  
                    } catch (IOException e) {  
                        LOGGER.error("kafkaProducer初始化失败:" + e.getMessage(), e);  
                    }  
                }  
            }  
        }  
    }  
  
    /** 
     * 通过kafkaProducer发送消息 
     *  
     * @param topic 
     *            消息接收主题 
     * @param partitionNum 
     *            哪一个分区 
     * @param retry 
     *            重试次数 
     * @param message 
     *            具体消息值 
     */  
    public Future sendKafkaMessage(final String message) {  
    	Future result = null;
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(  
                topic, message);  
        // send方法是异步的,添加消息到缓存区等待发送,并立即返回，这使生产者通过批量发送消息来提高效率  
        // kafka生产者是线程安全的,可以单实例发送消息  
        result = kafkaProducer.send(record, new Callback() {  
            public void onCompletion(RecordMetadata recordMetadata,  
                    Exception exception) {  
                if (null != exception) {  
                    LOGGER.error("kafka发送消息失败:" + exception.getMessage(),  
                            exception);  
                    retryKakfaMessage(message);  
                }  
            }  
        }); 
        return result;
    }  
  
    /** 
     * 当kafka消息发送失败后,重试 
     *  
     * @param retryMessage 
     */  
    private void retryKakfaMessage(final String retryMessage) {  
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(  
                topic, retryMessage);  
        for (int i = 1; i <= retry; i++) {  
            try {  
                kafkaProducer.send(record);  
                return;  
            } catch (Exception e) {  
                LOGGER.error("kafka发送消息失败:" + e.getMessage(), e);  
                retryKakfaMessage(retryMessage);  
            }  
        }  
    }  
  
    /** 
     * kafka实例销毁 
     */  
    public void close() {  
        if (null != kafkaProducer) {  
            kafkaProducer.close();  
        }  
    }  
  
    public String getTopic() {  
        return topic;  
    }  
  
    public void setTopic(String topic) {  
        this.topic = topic;  
    }  
  
    public int getRetry() {  
        return retry;  
    }  
  
    public void setRetry(int retry) {  
        this.retry = retry;  
    }  
  
} 




















