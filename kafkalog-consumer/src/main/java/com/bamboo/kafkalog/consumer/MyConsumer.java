package com.bamboo.kafkalog.consumer;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

public class MyConsumer {
	
	private static final Logger LOGGER = Logger.getLogger(MyConsumer.class);
	
	public static void main(String[] args) {
		 Properties props = new Properties();
	     props.put("bootstrap.servers", "192.168.83.51:9092,192.168.83.52:9092,192.168.83.53:9092");
	     props.put("group.id", "default1");
	     props.put("enable.auto.commit", "true");
	     props.put("auto.commit.interval.ms", "1000");
	     props.put("session.timeout.ms", "30000");
	     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	     KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
	     consumer.subscribe(Arrays.asList("kafka_log"));
	     while (true) {
	         ConsumerRecords<String, String> recordMap = consumer.poll(100);
	         for (ConsumerRecord<String, String> record : recordMap) {
	        	 LOGGER.info("========================>topic=" + record.topic() + ", partition=" + record.partition() + ", offset=" + record.offset()+ ", key=" + record.key() + ", value=" + record.value());
	         }
	     }
//	     kafka.producer.KafkaLog4jAppender
	     
	}
	
}