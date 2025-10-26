package web_ecommerce.sale_service.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "Admin - Review Management")
public class AdminReviewController extends BaseController {
    private static final String root = "/reviews";
    private final ProductReviewService reviewService;

    @ApiOperation(value = "Get pending reviews (for moderation)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/pending")
    public Response<Page<ProductReviewDto>> getPendingReviews(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        Page<ProductReviewDto> reviews = reviewService.getPendingReviews(pageable);
        return new Response<Page<ProductReviewDto>>().withDataAndStatus(reviews, HttpStatus.OK);
    }

    @ApiOperation(value = "Approve review")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/approve")
    public Response<Void> approveReview(@PathVariable String id) {
        reviewService.approveReview(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }

    @ApiOperation(value = "Reject review")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/reject")
    public Response<Void> rejectReview(@PathVariable String id) {
        reviewService.rejectReview(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }

    @ApiOperation(value = "Mark review as verified purchase")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/verify")
    public Response<Void> verifyReview(@PathVariable String id) {
        reviewService.markAsVerified(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }

    @ApiOperation(value = "Get all reviews for a product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/product/{productId}")
    public Response<Page<ProductReviewDto>> getReviewsByProductId(
            @PathVariable String productId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        Page<ProductReviewDto> reviews = reviewService.getReviewsByProductId(productId, pageable);
        return new Response<Page<ProductReviewDto>>().withDataAndStatus(reviews, HttpStatus.OK);
    }
}

