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
import web_ecommerce.sale_service.dto.CreateDiscountDto;
import web_ecommerce.sale_service.dto.DiscountDto;
import web_ecommerce.sale_service.service.DiscountService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Discount Management")
public class AdminDiscountController extends BaseController {
    private static final String root = "/discounts";
    private final DiscountService discountService;

    @Operation(summary = "Create new discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(V1 + root)
    public Response<DiscountDto> createDiscount(@RequestBody CreateDiscountDto createDiscountDto) {
        DiscountDto discount = discountService.createDiscount(createDiscountDto);
        return new Response<DiscountDto>().withDataAndStatus(discount, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all discounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root)
    public Response<Page<DiscountDto>> getAllDiscounts(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        Page<DiscountDto> discounts = discountService.getAllDiscounts(pageable);
        return new Response<Page<DiscountDto>>().withDataAndStatus(discounts, HttpStatus.OK);
    }

    @Operation(summary = "Get active discounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/active")
    public Response<List<DiscountDto>> getActiveDiscounts() {
        List<DiscountDto> discounts = discountService.getActiveDiscounts();
        return new Response<List<DiscountDto>>().withDataAndStatus(discounts, HttpStatus.OK);
    }

    @Operation(summary = "Get discount by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/{id}")
    public Response<DiscountDto> getDiscountById(@PathVariable String id) {
        return discountService.getDiscountById(id)
                .map(discount -> new Response<DiscountDto>().withDataAndStatus(discount, HttpStatus.OK))
                .orElse(new Response<DiscountDto>().withDataAndStatus(null, HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Update discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(V1 + root + "/{id}")
    public Response<DiscountDto> updateDiscount(@PathVariable String id, @RequestBody CreateDiscountDto updateDiscountDto) {
        DiscountDto updated = discountService.updateDiscount(id, updateDiscountDto);
        return new Response<DiscountDto>().withDataAndStatus(updated, HttpStatus.OK);
    }

    @Operation(summary = "Delete discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(V1 + root + "/{id}")
    public Response<Void> deleteDiscount(@PathVariable String id) {
        discountService.deleteDiscount(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Activate discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/activate")
    public Response<Void> activateDiscount(@PathVariable String id) {
        discountService.activateDiscount(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }

    @Operation(summary = "Deactivate discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{id}/deactivate")
    public Response<Void> deactivateDiscount(@PathVariable String id) {
        discountService.deactivateDiscount(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }
}


