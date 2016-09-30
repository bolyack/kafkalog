package com.bamboo.kafkalog;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App {
	
	private static final Logger LOGGER = Logger.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		for (int i = 0; i < 200; i++) {
            LOGGER.info("Info |" + sdf.format(new Date()) +  "|[" + i + "]");
            Thread.sleep(500);
        }
		
	}
}
