package web_ecommerce.sale_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import web_ecommerce.core.controller.BaseController;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.core.enums.ResponseMessage;
import web_ecommerce.core.utils.StringUtils;
import web_ecommerce.sale_service.dto.CreateProductReviewDto;
import web_ecommerce.sale_service.dto.ProductReviewDto;
import web_ecommerce.sale_service.service.ProductReviewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController extends BaseController {
    private static final String root = "/sale/reviews";
    private final ProductReviewService reviewService;

    @Operation(summary = "Create product review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(V1 + root)
    public Response<?> createReview(HttpServletRequest httpServletRequest, @RequestBody CreateProductReviewDto createDto) {
        String userId = getUserId(httpServletRequest);
        if (StringUtils.isNotNullOrEmpty(userId)) {
            return new Response<>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        ProductReviewDto review = reviewService.createReview(createDto, userId);
        return new Response<ProductReviewDto>().withDataAndStatus(review, HttpStatus.CREATED);
    }

    @Operation(summary = "Get approved reviews for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/product/{productId}")
    public Response<Page<ProductReviewDto>> getProductReviews(
            @PathVariable String productId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        Page<ProductReviewDto> reviews = reviewService.getApprovedReviewsByProductId(productId, pageable);
        return new Response<Page<ProductReviewDto>>().withDataAndStatus(reviews, HttpStatus.OK);
    }

    @Operation(summary = "Get user's reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/my-reviews")
    public Response<?> getMyReviews(HttpServletRequest httpServletRequest) {
        String userId = getUserId(httpServletRequest);
        if (StringUtils.isNotNullOrEmpty(userId)) {
            return new Response<>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        List<ProductReviewDto> reviews = reviewService.getReviewsByUserId(userId);
        return new Response<List<ProductReviewDto>>().withDataAndStatus(reviews, HttpStatus.OK);
    }

    @Operation(summary = "Update review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(V1 + root + "/{id}")
    public Response<?> updateReview(HttpServletRequest httpServletRequest, @PathVariable String id, @RequestBody CreateProductReviewDto updateDto) {
        String userId = getUserId(httpServletRequest);
        if (StringUtils.isNotNullOrEmpty(userId)) {
            return new Response<>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        ProductReviewDto updated = reviewService.updateReview(id, updateDto, userId);
        return new Response<ProductReviewDto>().withDataAndStatus(updated, HttpStatus.OK);
    }

    @Operation(summary = "Delete review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(V1 + root + "/{id}")
    public Response<?> deleteReview(HttpServletRequest httpServletRequest, @PathVariable String id) {
        String userId = getUserId(httpServletRequest);
        if (StringUtils.isNotNullOrEmpty(userId)) {
            return new Response<>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        reviewService.deleteReview(id, userId);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Mark review as helpful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(V1 + root + "/{id}/helpful")
    public Response<Void> markHelpful(@PathVariable String id) {
        reviewService.incrementHelpfulCount(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }
}


