package web_ecommerce.sale_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web_ecommerce.sale_service.dto.CreateProductReviewDto;
import web_ecommerce.sale_service.dto.ProductReviewDto;
import web_ecommerce.sale_service.enitty.Product;
import web_ecommerce.sale_service.enitty.ProductReview;
import web_ecommerce.sale_service.repository.ProductRepository;
import web_ecommerce.sale_service.repository.ProductReviewRepository;
import web_ecommerce.sale_service.service.ProductReviewService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductReviewServiceImpl implements ProductReviewService {
    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Override
    public ProductReviewDto createReview(CreateProductReviewDto createDto, String userId) {
        log.debug("Creating review for product {} by user {}", createDto.getProductId(), userId);
        
        // Verify product exists
        productRepository.findById(createDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + createDto.getProductId()));
        
        // Check if user already reviewed this product
        if (reviewRepository.existsByUserIdAndProductId(userId, createDto.getProductId())) {
            throw new RuntimeException("User has already reviewed this product");
        }
        
        // Validate rating
        if (createDto.getRating() < 1 || createDto.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        
        ProductReview review = new ProductReview();
        review.setProductId(createDto.getProductId());
        review.setUserId(userId);
        review.setRating(createDto.getRating());
        review.setTitle(createDto.getTitle());
        review.setComment(createDto.getComment());
        review.setIsVerified(false);
        review.setIsApproved(true); // Auto-approve by default, can be changed based on business logic
        review.setHelpfulCount(0);
        
        ProductReview saved = reviewRepository.save(review);
        
        // Update product average rating and review count
        updateProductReviewStats(createDto.getProductId());
        
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductReviewDto> getReviewById(String id) {
        return reviewRepository.findById(id).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewDto> getReviewsByProductId(String productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewDto> getApprovedReviewsByProductId(String productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndIsApprovedTrue(productId, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewDto> getPendingReviews(Pageable pageable) {
        return reviewRepository.findByIsApprovedFalse(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductReviewDto> getReviewsByUserId(String userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductReviewDto updateReview(String id, CreateProductReviewDto updateDto, String userId) {
        log.debug("Updating review: {}", id);
        
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        
        // Verify user owns this review
        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("User does not have permission to update this review");
        }
        
        if (updateDto.getRating() != null) {
            if (updateDto.getRating() < 1 || updateDto.getRating() > 5) {
                throw new RuntimeException("Rating must be between 1 and 5");
            }
            review.setRating(updateDto.getRating());
        }
        if (updateDto.getTitle() != null) {
            review.setTitle(updateDto.getTitle());
        }
        if (updateDto.getComment() != null) {
            review.setComment(updateDto.getComment());
        }
        
        ProductReview updated = reviewRepository.save(review);
        
        // Update product stats
        updateProductReviewStats(review.getProductId());
        
        return mapToDto(updated);
    }

    @Override
    public void deleteReview(String id, String userId) {
        log.debug("Deleting review: {}", id);
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        
        // Verify user owns this review
        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("User does not have permission to delete this review");
        }
        
        String productId = review.getProductId();
        reviewRepository.delete(review);
        
        // Update product stats
        updateProductReviewStats(productId);
    }

    @Override
    public void approveReview(String id) {
        log.debug("Approving review: {}", id);
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        review.setIsApproved(true);
        reviewRepository.save(review);
        
        // Update product stats
        updateProductReviewStats(review.getProductId());
    }

    @Override
    public void rejectReview(String id) {
        log.debug("Rejecting review: {}", id);
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        review.setIsApproved(false);
        reviewRepository.save(review);
        
        // Update product stats
        updateProductReviewStats(review.getProductId());
    }

    @Override
    public void markAsVerified(String id) {
        log.debug("Marking review as verified: {}", id);
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        review.setIsVerified(true);
        reviewRepository.save(review);
    }

    @Override
    public void incrementHelpfulCount(String id) {
        log.debug("Incrementing helpful count for review: {}", id);
        ProductReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    private void updateProductReviewStats(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        BigDecimal averageRating = reviewRepository.getAverageRatingByProductId(productId);
        Long reviewCount = reviewRepository.getApprovedReviewCountByProductId(productId);
        
        product.setAverageRating(averageRating != null ? averageRating.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        product.setReviewCount(reviewCount != null ? reviewCount.intValue() : 0);
        
        productRepository.save(product);
    }

    private ProductReviewDto mapToDto(ProductReview entity) {
        return ProductReviewDto.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .userId(entity.getUserId())
                .rating(entity.getRating())
                .title(entity.getTitle())
                .comment(entity.getComment())
                .isVerified(entity.getIsVerified())
                .isApproved(entity.getIsApproved())
                .helpfulCount(entity.getHelpfulCount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

