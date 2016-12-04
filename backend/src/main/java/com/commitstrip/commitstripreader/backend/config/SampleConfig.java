package com.commitstrip.commitstripreader.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

@Component
public class SampleConfig {

    @Value("${notificationurl}")
    private String notificationUrl;

    @Value("${notificationkey}")
    private String notificationkey;

    @Value("${notificationtopic}")
    private String notificationTopic;

    public String getNotificationkey() {
        return notificationkey;
    }

    public void setNotificationkey(String notificationkey) {
        this.notificationkey = notificationkey;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }

    public String getNotificationTopic() {
        return notificationTopic;
    }

    public void setNotificationTopic(String notificationTopic) {
        this.notificationTopic = notificationTopic;
    }
}