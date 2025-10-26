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
import web_ecommerce.sale_service.dto.CreateProductVendorDto;
import web_ecommerce.sale_service.dto.ProductVendorDto;
import web_ecommerce.sale_service.service.ProductVendorService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Api(tags = "Admin - Vendor Management")
public class AdminVendorController extends BaseController {
    private static final String root = "/vendors";
    private final ProductVendorService vendorService;

    @ApiOperation(value = "Create new vendor")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(V1 + root)
    public Response<ProductVendorDto> createVendor(@RequestBody CreateProductVendorDto createDto) {
        ProductVendorDto vendor = vendorService.createVendor(createDto);
        return new Response<ProductVendorDto>().withDataAndStatus(vendor, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get all vendors")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root)
    public Response<Page<ProductVendorDto>> getAllVendors(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, page = 0, size = 20) Pageable pageable) {
        Page<ProductVendorDto> vendors = vendorService.getAllVendors(pageable);
        return new Response<Page<ProductVendorDto>>().withDataAndStatus(vendors, HttpStatus.OK);
    }

    @ApiOperation(value = "Get vendor by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/{id}")
    public Response<ProductVendorDto> getVendorById(@PathVariable String id) {
        return vendorService.getVendorById(id)
                .map(vendor -> new Response<ProductVendorDto>().withDataAndStatus(vendor, HttpStatus.OK))
                .orElse(new Response<ProductVendorDto>().withDataAndStatus(null, HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "Update vendor")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping(V1 + root + "/{id}")
    public Response<ProductVendorDto> updateVendor(@PathVariable String id, @RequestBody CreateProductVendorDto updateDto) {
        ProductVendorDto updated = vendorService.updateVendor(id, updateDto);
        return new Response<ProductVendorDto>().withDataAndStatus(updated, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete vendor")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping(V1 + root + "/{id}")
    public Response<Void> deleteVendor(@PathVariable String id) {
        vendorService.deleteVendor(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.NO_CONTENT);
    }
}

