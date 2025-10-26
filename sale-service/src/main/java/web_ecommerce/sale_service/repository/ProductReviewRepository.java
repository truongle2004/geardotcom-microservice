package web_ecommerce.sale_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web_ecommerce.sale_service.enitty.ProductReview;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, String> {
    Page<ProductReview> findByProductId(String productId, Pageable pageable);
    
    Page<ProductReview> findByProductIdAndIsApprovedTrue(String productId, Pageable pageable);
    
    Page<ProductReview> findByIsApprovedFalse(Pageable pageable);
    
    List<ProductReview> findByUserId(String userId);
    
    boolean existsByUserIdAndProductId(String userId, String productId);
    
    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.productId = :productId AND pr.isApproved = true")
    BigDecimal getAverageRatingByProductId(@Param("productId") String productId);
    
    @Query("SELECT COUNT(pr) FROM ProductReview pr WHERE pr.productId = :productId AND pr.isApproved = true")
    Long getApprovedReviewCountByProductId(@Param("productId") String productId);
}

