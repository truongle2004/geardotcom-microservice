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
import web_ecommerce.sale_service.dto.CouponDto;
import web_ecommerce.sale_service.dto.CreateCouponDto;
import web_ecommerce.sale_service.service.CouponService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Coupon Management")
public class AdminCouponController extends BaseController {
    private static final String root = "/coupons";
    private final CouponService couponService;

    @Operation(summary = "Create new coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(V1 + root)
    public Response<CouponDto> createCoupon(@RequestBody CreateCouponDto createCouponDto) {
        CouponDto coupon = couponService.createCoupon(createCouponDto);
        return new Response<CouponDto>().withDataAndStatus(coupon, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all coupons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root)
    public Response<Page<CouponDto>> getAllCoupons(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        Page<CouponDto> coupons = couponService.getAllCoupons(pageable);
        return new Response<Page<CouponDto>>().withDataAndStatus(coupons, HttpStatus.OK);
    }

    @Operation(summary = "Get coupon by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/{id}")
    public Response<CouponDto> getCouponById(@PathVariable String id) {
        return couponService.getCouponById(id)
                .map(coupon -> new Response<CouponDto>().withDataAndStatus(coupon, HttpStatus.OK))
                .orElse(new Response<CouponDto>().withDataAndStatus(null, HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Update coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(V1 + root + "/{id}")
    public Response<CouponDto> updateCoupon(@PathVariable String id, @RequestBody CreateCouponDto updateCouponDto) {
        CouponDto updated = couponService.updateCoupon(id, updateCouponDto);
        return new Response<CouponDto>().withDataAndStatus(updated, HttpStatus.OK);
    }

    @Operation(summary = "Delete coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(V1 + root + "/{id}")
    public Response<Void> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Activate coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/activate")
    public Response<Void> activateCoupon(@PathVariable String id) {
        couponService.activateCoupon(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }

    @Operation(summary = "Deactivate coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/deactivate")
    public Response<Void> deactivateCoupon(@PathVariable String id) {
        couponService.deactivateCoupon(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }
}


