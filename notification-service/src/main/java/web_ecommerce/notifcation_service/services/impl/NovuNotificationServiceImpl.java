package web_ecommerce.notifcation_service.services.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import web_ecommerce.notifcation_service.config.NovuProperties;
import web_ecommerce.notifcation_service.repositories.NovuSubcriberRepository;
import web_ecommerce.notifcation_service.services.NovuNotificationService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NovuNotificationServiceImpl implements NovuNotificationService {

    private static final Logger log = LoggerFactory.getLogger(NovuNotificationServiceImpl.class);
    private final NovuProperties novuProperties;
    private final WebClient novuWebClient;
    private final NovuSubcriberRepository novuSubcriberRepository;

    @Override
    public Mono<Void> triggerEvent(String workflowName, String subscriberId, Map<String, Object> payload, String userId) {
        final String url = "v1/events/trigger";

        String sub_id = novuSubcriberRepository.findByUserId(userId);
        if (sub_id == null) {
            log.error("Error: Subscriber not found for user {}", userId);
            return Mono.error(new RuntimeException("Subscriber not found for user " + userId));
        }

        Map<String, Object> requestBody = Map.of(
                "name", workflowName,
                "to", Map.of("subscriberId", subscriberId),
                "payload", payload
        );
        log.info("Triggering Novu event: {}", requestBody);

        return novuWebClient.post()
                .uri(url)
                .header("Authorization", "ApiKey " + novuProperties.getApi_key())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> {
                                    System.err.println("Novu Error: " + error);
                                    return Mono.error(new RuntimeException("Failed to trigger Novu event"));
                                })
                )
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> createSubscriber(String subscriberId) {
        String url = "v2/subcribers";
        Map<String, Object> body = Map.of(
                "subscriberId", subscriberId,
                "firstName", "John",
                "lastName", "Doe",
                "email", "jUf4t@example.com",
                "phone", "123-456-7890",
                "avatar", "https://example.com/avatar.jpg",
                "timezone", "America/New_York",
                "locale", "en-US",
                "data", Map.of()
        );
        return null;
    }
}
