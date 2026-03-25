package web_ecommerce.notifcation_service.entities;


import jakarta.persistence.*;
import lombok.*;
import web_ecommerce.core.enums.NotificationStatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_queue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Subscriber ID in Novu
    @Column(name = "subscriber_id", nullable = false)
    private String subscriberId;

    // Trigger identifier (e.g., "order-created", "payment-success")
    @Column(name = "trigger_name", nullable = false)
    private String triggerName;

    // Dynamic data passed to Novu
    @Lob
    @Column(name = "payload", columnDefinition = "TEXT")
    private String payloadJson;

    // Queue status: PENDING, SENT, FAILED
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatusEnum status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "error_message")
    private String errorMessage;
}
