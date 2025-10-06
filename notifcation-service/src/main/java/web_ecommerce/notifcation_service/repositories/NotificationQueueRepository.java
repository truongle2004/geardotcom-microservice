package web_ecommerce.notifcation_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import web_ecommerce.core.enums.NotificationStatusEnum;
import web_ecommerce.notifcation_service.entities.NotificationQueue;

import java.util.List;

public interface NotificationQueueRepository extends JpaRepository<NotificationQueue, String> {
    List<NotificationQueue> findByStatus(NotificationStatusEnum status);
}
