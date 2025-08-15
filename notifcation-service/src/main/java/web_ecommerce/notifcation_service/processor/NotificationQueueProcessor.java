package web_ecommerce.notifcation_service.processor;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import web_ecommerce.core.enums.NotificationStatusEnum;
import web_ecommerce.notifcation_service.config.NovuProperties;
import web_ecommerce.notifcation_service.entities.NotificationQueue;
import web_ecommerce.notifcation_service.repositories.NotificationQueueRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationQueueProcessor {

    private final NotificationQueueRepository queueRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final NovuProperties novuProperties;
    private static final String novuTriggerUrl = "https://api.novu.co/v1/events/trigger";

    @Scheduled(fixedRate = 10000) // Run every 10 seconds
    public void processQueue() {
        List<NotificationQueue> pending = queueRepository.findByStatus(NotificationStatusEnum.PENDING);
        for (NotificationQueue item : pending) {
            try {
                Map<String, Object> payload = objectMapper.readValue(item.getPayloadJson(), Map.class);
                webClient.post()
                        .uri(novuTriggerUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "ApiKey " + novuProperties.getApi_key())
                        .bodyValue(Map.of(
                                "name", item.getTriggerName(),
                                "to", Map.of("subscriberId", item.getSubscriberId()),
                                "payload", payload
                        ))
                        .retrieve()
                        .toBodilessEntity()
                        .block();

                item.setStatus(NotificationStatusEnum.SENT);
                item.setSentAt(LocalDateTime.now());
                item.setErrorMessage(null);
            } catch (Exception e) {
                item.setStatus(NotificationStatusEnum.FAILED);
                item.setErrorMessage(e.getMessage());
                log.error("Failed to send notification ID {}: {}", item.getId(), e.getMessage());
            }

            queueRepository.save(item);
        }
    }
}
