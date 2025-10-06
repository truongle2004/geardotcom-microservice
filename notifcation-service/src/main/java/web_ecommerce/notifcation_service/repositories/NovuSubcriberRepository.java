package web_ecommerce.notifcation_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web_ecommerce.notifcation_service.entities.NovuSubscriber;

@Repository
public interface NovuSubcriberRepository extends JpaRepository<NovuSubcriberRepository, String> {

    @Query(value = "select s.userId from NovuSubscriber s where s.userId = :userId")
    String findByUserId(String userId);
}
