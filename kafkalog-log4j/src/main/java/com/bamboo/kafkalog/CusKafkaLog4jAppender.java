package com.bamboo.kafkalog;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.*;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;


/**
 * 自定义Log4JAppender输出日志到Kafka队列
 * @author admin
 */
/*
 * 
log4j.appender.kafka=com.bamboo.kafkalog.CusKafkaLog4jAppender
log4j.appender.kafka.topic=topics
log4j.appender.kafka.brokerList=192.168.83.51:9092,192.168.83.52:9092,192.168.83.53:9092
log4j.appender.kafka.layout=org.apache.log4j.PatternLayout
log4j.appender.kafka.layout.ConversionPattern=%d [%-5p] [%t] - [%l] %m%n
 */
public class CusKafkaLog4jAppender extends AppenderSkeleton {

	private String topic;
	//multiple brokers are separated by comma ",".
	private String brokerList;
	private int requestTimeOutMS;
	private String acks;
	private String retries;
	private String clientId;
	private int maxBlockMs;
	
	private Producer<String, String> producer = null;

	@Override
	protected void append(LoggingEvent event) {
		String msg = "";
		if (null != producer) {
			if (this.layout == null) {
				msg = event.getRenderedMessage();
			} else {
				msg = this.layout.format(event);
			}
			pushLogKafka(msg);
		}
	}
	
	
	public void activateOptions() {
		try {
			Properties props = new Properties();
			if (null == brokerList && "".equals(brokerList)) {
				throw new Exception("The bootstrap servers property should be specified");
			}
			props.put("bootstrap.servers", brokerList);
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
			if (maxBlockMs != 0) {
				props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);
			} else {
				props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 500);
			}
			if (requestTimeOutMS != 0) {
				props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeOutMS);
			}
			if (null != acks && !"".equals(acks)) {
				props.put(ProducerConfig.ACKS_CONFIG, acks);
			}
			if (null != retries && !"".equals(retries)) {
				props.put(ProducerConfig.RETRIES_CONFIG, retries);
			}
			if (null != clientId && !"".equals(clientId)) {
				props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
			}
			
			producer = new KafkaProducer<String, String>(props);
			
		} catch (Exception e) {
			LogLog.error("com.bamboo.kafkalog.CusKafkaLog4jAppender-activateOptions has some config error!", e);
		}
	}
	
	private void pushLogKafka(String mesg) {
		ProducerRecord<String, String> data = null;
		try {
			
			if(topic == null && "".equals(topic)) {
				throw new Exception("topic must be specified by the Kafka log4j appender");
			}
			
			data = new ProducerRecord<String, String>(topic, mesg);

			Future<RecordMetadata> future = producer.send(data);
			future.get();
			
		} catch (Exception e) {
			LogLog.error("com.bamboo.kafkalog.CusKafkaLog4jAppender-pushLogKafka->producer send message error", e);
		} finally {
			if (null != data) {
				data = null;
			}
		}
	}

	public void close() {
		if (!this.closed) {
			this.closed = true;
			producer.close();
		}
	}

	public boolean requiresLayout() {
		return false;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getBrokerList() {
		return brokerList;
	}

	public void setBrokerList(String brokerList) {
		this.brokerList = brokerList;
	}

	public int getRequestTimeOutMS() {
		return requestTimeOutMS;
	}


	public void setRequestTimeOutMS(int requestTimeOutMS) {
		this.requestTimeOutMS = requestTimeOutMS;
	}


	public String getAcks() {
		return acks;
	}


	public void setAcks(String acks) {
		this.acks = acks;
	}


	public String getRetries() {
		return retries;
	}


	public void setRetries(String retries) {
		this.retries = retries;
	}


	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getMaxBlockMs() {
		return maxBlockMs;
	}

	public void setMaxBlockMs(int maxBlockMs) {
		this.maxBlockMs = maxBlockMs;
	}
}
