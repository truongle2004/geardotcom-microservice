package web_ecommerce.sale_service.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "Create product review")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
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

    @ApiOperation(value = "Get approved reviews for a product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/product/{productId}")
    public Response<Page<ProductReviewDto>> getProductReviews(
            @PathVariable String productId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        Page<ProductReviewDto> reviews = reviewService.getApprovedReviewsByProductId(productId, pageable);
        return new Response<Page<ProductReviewDto>>().withDataAndStatus(reviews, HttpStatus.OK);
    }

    @ApiOperation(value = "Get user's reviews")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
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

    @ApiOperation(value = "Update review")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
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

    @ApiOperation(value = "Delete review")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
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

    @ApiOperation(value = "Mark review as helpful")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(V1 + root + "/{id}/helpful")
    public Response<Void> markHelpful(@PathVariable String id) {
        reviewService.incrementHelpfulCount(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }
}

