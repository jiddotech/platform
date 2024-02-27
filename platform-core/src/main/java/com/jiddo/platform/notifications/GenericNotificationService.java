package com.jiddo.platform.notifications;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.jiddo.platform.clients.CustomRabbitMQTemplate;
import com.jiddo.platform.exception.PlatformExceptionCodes;
import com.jiddo.platform.exception.ValidationException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GenericNotificationService {

	private CustomRabbitMQTemplate rabbitTemplate;

	private GenericRabbitQueueConfiguration rabbitQueueConfiguration;

	public void sendWhatsapp(String mobile, Map<String, String> data, String templates) {
		sendWhatsapp(91, mobile, data, Arrays.asList(templates));
	}

	public void sendWhatsapp(int countryCode, String mobile, Map<String, String> data, String templates) {
		sendWhatsapp(countryCode, mobile, data, Arrays.asList(templates));
	}

	public void sendWhatsapp(int countryCode, String mobile, Map<String, String> data, List<String> templates) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setId(UUID.randomUUID().toString());
		notificationDTO.setType(NotificationType.GENERIC_NOTIFICATION);
		notificationDTO.setChannel(Channel.WHATSAPP);
		WhatsappNotificationTo smsNotificationTo = new WhatsappNotificationTo();
		smsNotificationTo.setPhone(mobile);
		smsNotificationTo.setCountryCode(countryCode);
		notificationDTO.setTo(smsNotificationTo);
		notificationDTO.setTemplates(templates);
		notificationDTO.setData(data);
		sendNotification(notificationDTO);
	}

	public void sendNotification(NotificationDTO notification) {
		validateSendNotification(notification);
		notification.setId(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend(rabbitQueueConfiguration.getSendNotification(), notification);
	}

	private void validateSendNotification(NotificationDTO notification) {
		if (ObjectUtils.isEmpty(notification)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid notification request");
		}
		if (ObjectUtils.isEmpty(notification.getChannel())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid notification channel");
		}
		if (ObjectUtils.isEmpty(notification.getTo())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid notification to");
		}
		if (ObjectUtils.isEmpty(notification.getType())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid notification type");
		}
	}
}
