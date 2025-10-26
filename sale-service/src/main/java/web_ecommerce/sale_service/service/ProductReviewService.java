package web_ecommerce.sale_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import web_ecommerce.sale_service.dto.CreateProductReviewDto;
import web_ecommerce.sale_service.dto.ProductReviewDto;

import java.util.List;
import java.util.Optional;

public interface ProductReviewService {
    ProductReviewDto createReview(CreateProductReviewDto createDto, String userId);
    
    Optional<ProductReviewDto> getReviewById(String id);
    
    Page<ProductReviewDto> getReviewsByProductId(String productId, Pageable pageable);
    
    Page<ProductReviewDto> getApprovedReviewsByProductId(String productId, Pageable pageable);
    
    Page<ProductReviewDto> getPendingReviews(Pageable pageable);
    
    List<ProductReviewDto> getReviewsByUserId(String userId);
    
    ProductReviewDto updateReview(String id, CreateProductReviewDto updateDto, String userId);
    
    void deleteReview(String id, String userId);
    
    void approveReview(String id);
    
    void rejectReview(String id);
    
    void markAsVerified(String id);
    
    void incrementHelpfulCount(String id);
}

