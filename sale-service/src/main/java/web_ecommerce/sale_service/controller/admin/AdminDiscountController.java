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
import web_ecommerce.sale_service.dto.CreateDiscountDto;
import web_ecommerce.sale_service.dto.DiscountDto;
import web_ecommerce.sale_service.service.DiscountService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Api(tags = "Admin - Discount Management")
public class AdminDiscountController extends BaseController {
    private static final String root = "/discounts";
    private final DiscountService discountService;

    @ApiOperation(value = "Create new discount")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(V1 + root)
    public Response<DiscountDto> createDiscount(@RequestBody CreateDiscountDto createDiscountDto) {
        DiscountDto discount = discountService.createDiscount(createDiscountDto);
        return new Response<DiscountDto>().withDataAndStatus(discount, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get all discounts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root)
    public Response<Page<DiscountDto>> getAllDiscounts(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        Page<DiscountDto> discounts = discountService.getAllDiscounts(pageable);
        return new Response<Page<DiscountDto>>().withDataAndStatus(discounts, HttpStatus.OK);
    }

    @ApiOperation(value = "Get active discounts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/active")
    public Response<List<DiscountDto>> getActiveDiscounts() {
        List<DiscountDto> discounts = discountService.getActiveDiscounts();
        return new Response<List<DiscountDto>>().withDataAndStatus(discounts, HttpStatus.OK);
    }

    @ApiOperation(value = "Get discount by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/{id}")
    public Response<DiscountDto> getDiscountById(@PathVariable String id) {
        return discountService.getDiscountById(id)
                .map(discount -> new Response<DiscountDto>().withDataAndStatus(discount, HttpStatus.OK))
                .orElse(new Response<DiscountDto>().withDataAndStatus(null, HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "Update discount")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping(V1 + root + "/{id}")
    public Response<DiscountDto> updateDiscount(@PathVariable String id, @RequestBody CreateDiscountDto updateDiscountDto) {
        DiscountDto updated = discountService.updateDiscount(id, updateDiscountDto);
        return new Response<DiscountDto>().withDataAndStatus(updated, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete discount")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping(V1 + root + "/{id}")
    public Response<Void> deleteDiscount(@PathVariable String id) {
        discountService.deleteDiscount(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Activate discount")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/activate")
    public Response<Void> activateDiscount(@PathVariable String id) {
        discountService.activateDiscount(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }

    @ApiOperation(value = "Deactivate discount")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/deactivate")
    public Response<Void> deactivateDiscount(@PathVariable String id) {
        discountService.deactivateDiscount(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }
}

