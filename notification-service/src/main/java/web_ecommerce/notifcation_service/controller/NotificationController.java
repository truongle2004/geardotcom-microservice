package web_ecommerce.notifcation_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import web_ecommerce.core.controller.BaseController;
import web_ecommerce.notifcation_service.services.NovuNotificationService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class NotificationController extends BaseController {

    private final static String root = "/notification";
    private final NovuNotificationService notificationService;

    @PostMapping(V1 + root + "/send-notification")
    public Mono<String> sendNotification(HttpServletRequest request) {
        String userId = getUserId(request);

        Map<String, Object> payload = Map.of(
                "orderId", "12345",
                "status", "confirmed"
        );

        return notificationService.triggerEvent("order-confirmed", userId, payload, userId)
                .thenReturn("Notification sent");
    }
}
