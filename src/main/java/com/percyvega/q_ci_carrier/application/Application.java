package com.percyvega.q_ci_carrier.application;

import com.percyvega.q_ci_carrier.jms.JMSReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.percyvega.q_ci_carrier")
@PropertySource(value = {"application.properties", "sensitive.properties"}, ignoreResourceNotFound = true)
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.debug("Starting main(" + Arrays.toString(args) + ")");

        SpringApplication.run(Application.class, args);

        JMSReceiver jmsReceiver = new JMSReceiver();
        jmsReceiver.init();
    }

}
