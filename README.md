# UserTrail_Pre
亚信用户轨迹项目，读取信令数据，处理，整合，输出到kafka
信令数据通过ftp，以文件的方式传到接口机上，3g大小约300M/每个，4g大小约800M/每个，每五分钟大约4个文件
读取文件，转换基站，处理时间
调用单例懒汉模式的kafkaproducer客户端，发送消息，同时控制重发
打印日志