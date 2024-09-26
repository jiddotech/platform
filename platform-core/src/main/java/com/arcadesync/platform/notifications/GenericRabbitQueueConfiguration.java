package com.arcadesync.platform.notifications;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.arcadesync.platform.PlatformConstants;
import com.arcadesync.platform.config.QueueConfiguration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = PlatformConstants.QUEUE_CONFIGURATION_KEY_PATH)
public class GenericRabbitQueueConfiguration {
	private QueueConfiguration sendNotification;
	private QueueConfiguration updateTracker;
	private QueueConfiguration entityAuditing;
	private QueueConfiguration pdfGenerationAndUpload;
	private QueueConfiguration httpScheduleTask;
}
