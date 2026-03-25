package web_ecommerce.sale_service.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import web_ecommerce.core.controller.BaseController;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.sale_service.dto.ProductReviewDto;
import web_ecommerce.sale_service.service.ProductReviewService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Review Management")
public class AdminReviewController extends BaseController {
    private static final String root = "/reviews";
    private final ProductReviewService reviewService;

    @Operation(summary = "Get pending reviews (for moderation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/pending")
    public Response<Page<ProductReviewDto>> getPendingReviews(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        Page<ProductReviewDto> reviews = reviewService.getPendingReviews(pageable);
        return new Response<Page<ProductReviewDto>>().withDataAndStatus(reviews, HttpStatus.OK);
    }

    @Operation(summary = "Approve review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/approve")
    public Response<Void> approveReview(@PathVariable String id) {
        reviewService.approveReview(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }

    @Operation(summary = "Reject review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/reject")
    public Response<Void> rejectReview(@PathVariable String id) {
        reviewService.rejectReview(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }

    @Operation(summary = "Mark review as verified purchase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/verify")
    public Response<Void> verifyReview(@PathVariable String id) {
        reviewService.markAsVerified(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }

    @Operation(summary = "Get all reviews for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/product/{productId}")
    public Response<Page<ProductReviewDto>> getReviewsByProductId(
            @PathVariable String productId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        Page<ProductReviewDto> reviews = reviewService.getReviewsByProductId(productId, pageable);
        return new Response<Page<ProductReviewDto>>().withDataAndStatus(reviews, HttpStatus.OK);
    }
}


