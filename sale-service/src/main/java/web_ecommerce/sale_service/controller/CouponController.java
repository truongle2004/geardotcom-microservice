package web_ecommerce.sale_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import web_ecommerce.core.controller.BaseController;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.sale_service.dto.CouponValidationResultDto;
import web_ecommerce.sale_service.dto.ValidateCouponDto;
import web_ecommerce.sale_service.service.CouponService;

@RestController
@RequiredArgsConstructor
public class CouponController extends BaseController {
    private static final String root = "coupon";
    private final CouponService couponService;

    @Operation(summary = "API get list product")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")}
    )
    @PostMapping(V1 + root + "/validate")
    public Response<?> validationResultDtoResponse(HttpServletRequest request, @RequestBody ValidateCouponDto validateCouponDto) {
        String userId = getUserId(request);
        CouponValidationResultDto couponValidationResultDto = couponService.validateCoupon(validateCouponDto, userId);
        return new Response<>().withDataAndStatus(couponValidationResultDto, HttpStatus.OK);
    }
}

