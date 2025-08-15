package web_ecommerce.notifcation_service.services;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface NovuNotificationService {
    Mono<Void> triggerEvent(String workflowName, String subscriberId, Map<String, Object> payload, String userId);
    Mono<Void> createSubscriber(String subscriberId);
}
