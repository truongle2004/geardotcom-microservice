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
import web_ecommerce.sale_service.dto.CouponDto;
import web_ecommerce.sale_service.dto.CreateCouponDto;
import web_ecommerce.sale_service.service.CouponService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Api(tags = "Admin - Coupon Management")
public class AdminCouponController extends BaseController {
    private static final String root = "/coupons";
    private final CouponService couponService;

    @ApiOperation(value = "Create new coupon")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(V1 + root)
    public Response<CouponDto> createCoupon(@RequestBody CreateCouponDto createCouponDto) {
        CouponDto coupon = couponService.createCoupon(createCouponDto);
        return new Response<CouponDto>().withDataAndStatus(coupon, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get all coupons")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root)
    public Response<Page<CouponDto>> getAllCoupons(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        Page<CouponDto> coupons = couponService.getAllCoupons(pageable);
        return new Response<Page<CouponDto>>().withDataAndStatus(coupons, HttpStatus.OK);
    }

    @ApiOperation(value = "Get coupon by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/{id}")
    public Response<CouponDto> getCouponById(@PathVariable String id) {
        return couponService.getCouponById(id)
                .map(coupon -> new Response<CouponDto>().withDataAndStatus(coupon, HttpStatus.OK))
                .orElse(new Response<CouponDto>().withDataAndStatus(null, HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "Update coupon")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping(V1 + root + "/{id}")
    public Response<CouponDto> updateCoupon(@PathVariable String id, @RequestBody CreateCouponDto updateCouponDto) {
        CouponDto updated = couponService.updateCoupon(id, updateCouponDto);
        return new Response<CouponDto>().withDataAndStatus(updated, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete coupon")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping(V1 + root + "/{id}")
    public Response<Void> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Activate coupon")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/activate")
    public Response<Void> activateCoupon(@PathVariable String id) {
        couponService.activateCoupon(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }

    @ApiOperation(value = "Deactivate coupon")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/deactivate")
    public Response<Void> deactivateCoupon(@PathVariable String id) {
        couponService.deactivateCoupon(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }
}

