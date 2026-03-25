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
import web_ecommerce.sale_service.dto.CreateWarehouseDto;
import web_ecommerce.sale_service.dto.UpdateWarehouseStockDto;
import web_ecommerce.sale_service.dto.WarehouseDetailDto;
import web_ecommerce.sale_service.dto.WarehouseDto;
import web_ecommerce.sale_service.service.WarehouseService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin - Warehouse Management")
public class AdminWarehouseController extends BaseController {
    private static final String root = "/warehouses";
    private final WarehouseService warehouseService;

    @Operation(summary = "Create new warehouse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(V1 + root)
    public Response<WarehouseDto> createWarehouse(@RequestBody CreateWarehouseDto createDto) {
        WarehouseDto warehouse = warehouseService.createWarehouse(createDto);
        return new Response<WarehouseDto>().withDataAndStatus(warehouse, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all warehouses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root)
    public Response<Page<WarehouseDto>> getAllWarehouses(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, page = 0, size = 20) Pageable pageable) {
        Page<WarehouseDto> warehouses = warehouseService.getAllWarehouses(pageable);
        return new Response<Page<WarehouseDto>>().withDataAndStatus(warehouses, HttpStatus.OK);
    }

    @Operation(summary = "Get active warehouses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/active")
    public Response<List<WarehouseDto>> getActiveWarehouses() {
        List<WarehouseDto> warehouses = warehouseService.getActiveWarehouses();
        return new Response<List<WarehouseDto>>().withDataAndStatus(warehouses, HttpStatus.OK);
    }

    @Operation(summary = "Get warehouse by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/{id}")
    public Response<WarehouseDto> getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id)
                .map(warehouse -> new Response<WarehouseDto>().withDataAndStatus(warehouse, HttpStatus.OK))
                .orElse(new Response<WarehouseDto>().withDataAndStatus(null, HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Update warehouse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(V1 + root + "/{id}")
    public Response<WarehouseDto> updateWarehouse(@PathVariable Long id, @RequestBody CreateWarehouseDto updateDto) {
        WarehouseDto updated = warehouseService.updateWarehouse(id, updateDto);
        return new Response<WarehouseDto>().withDataAndStatus(updated, HttpStatus.OK);
    }

    @Operation(summary = "Delete warehouse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(V1 + root + "/{id}")
    public Response<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Update product stock in warehouse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(V1 + root + "/stock")
    public Response<WarehouseDetailDto> updateStock(@RequestBody UpdateWarehouseStockDto updateStockDto) {
        WarehouseDetailDto detail = warehouseService.updateStock(updateStockDto);
        return new Response<WarehouseDetailDto>().withDataAndStatus(detail, HttpStatus.OK);
    }

    @Operation(summary = "Get stock by product ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/stock/product/{productId}")
    public Response<List<WarehouseDetailDto>> getStockByProductId(@PathVariable String productId) {
        List<WarehouseDetailDto> stock = warehouseService.getStockByProductId(productId);
        return new Response<List<WarehouseDetailDto>>().withDataAndStatus(stock, HttpStatus.OK);
    }

    @Operation(summary = "Get stock by warehouse ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/{warehouseId}/stock")
    public Response<List<WarehouseDetailDto>> getStockByWarehouseId(@PathVariable Long warehouseId) {
        List<WarehouseDetailDto> stock = warehouseService.getStockByWarehouseId(warehouseId);
        return new Response<List<WarehouseDetailDto>>().withDataAndStatus(stock, HttpStatus.OK);
    }

    @Operation(summary = "Get total stock for a product across all warehouses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(V1 + root + "/stock/product/{productId}/total")
    public Response<Long> getTotalStock(@PathVariable String productId) {
        Long total = warehouseService.getTotalStockByProductId(productId);
        return new Response<Long>().withDataAndStatus(total, HttpStatus.OK);
    }
}


