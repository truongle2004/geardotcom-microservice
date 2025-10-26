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
import web_ecommerce.sale_service.dto.CreateWarehouseDto;
import web_ecommerce.sale_service.dto.UpdateWarehouseStockDto;
import web_ecommerce.sale_service.dto.WarehouseDetailDto;
import web_ecommerce.sale_service.dto.WarehouseDto;
import web_ecommerce.sale_service.service.WarehouseService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Api(tags = "Admin - Warehouse Management")
public class AdminWarehouseController extends BaseController {
    private static final String root = "/warehouses";
    private final WarehouseService warehouseService;

    @ApiOperation(value = "Create new warehouse")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(V1 + root)
    public Response<WarehouseDto> createWarehouse(@RequestBody CreateWarehouseDto createDto) {
        WarehouseDto warehouse = warehouseService.createWarehouse(createDto);
        return new Response<WarehouseDto>().withDataAndStatus(warehouse, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get all warehouses")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root)
    public Response<Page<WarehouseDto>> getAllWarehouses(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC, page = 0, size = 20) Pageable pageable) {
        Page<WarehouseDto> warehouses = warehouseService.getAllWarehouses(pageable);
        return new Response<Page<WarehouseDto>>().withDataAndStatus(warehouses, HttpStatus.OK);
    }

    @ApiOperation(value = "Get active warehouses")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/active")
    public Response<List<WarehouseDto>> getActiveWarehouses() {
        List<WarehouseDto> warehouses = warehouseService.getActiveWarehouses();
        return new Response<List<WarehouseDto>>().withDataAndStatus(warehouses, HttpStatus.OK);
    }

    @ApiOperation(value = "Get warehouse by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/{id}")
    public Response<WarehouseDto> getWarehouseById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id)
                .map(warehouse -> new Response<WarehouseDto>().withDataAndStatus(warehouse, HttpStatus.OK))
                .orElse(new Response<WarehouseDto>().withDataAndStatus(null, HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "Update warehouse")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping(V1 + root + "/{id}")
    public Response<WarehouseDto> updateWarehouse(@PathVariable Long id, @RequestBody CreateWarehouseDto updateDto) {
        WarehouseDto updated = warehouseService.updateWarehouse(id, updateDto);
        return new Response<WarehouseDto>().withDataAndStatus(updated, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete warehouse")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping(V1 + root + "/{id}")
    public Response<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Update product stock in warehouse")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(V1 + root + "/stock")
    public Response<WarehouseDetailDto> updateStock(@RequestBody UpdateWarehouseStockDto updateStockDto) {
        WarehouseDetailDto detail = warehouseService.updateStock(updateStockDto);
        return new Response<WarehouseDetailDto>().withDataAndStatus(detail, HttpStatus.OK);
    }

    @ApiOperation(value = "Get stock by product ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/stock/product/{productId}")
    public Response<List<WarehouseDetailDto>> getStockByProductId(@PathVariable String productId) {
        List<WarehouseDetailDto> stock = warehouseService.getStockByProductId(productId);
        return new Response<List<WarehouseDetailDto>>().withDataAndStatus(stock, HttpStatus.OK);
    }

    @ApiOperation(value = "Get stock by warehouse ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/{warehouseId}/stock")
    public Response<List<WarehouseDetailDto>> getStockByWarehouseId(@PathVariable Long warehouseId) {
        List<WarehouseDetailDto> stock = warehouseService.getStockByWarehouseId(warehouseId);
        return new Response<List<WarehouseDetailDto>>().withDataAndStatus(stock, HttpStatus.OK);
    }

    @ApiOperation(value = "Get total stock for a product across all warehouses")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/stock/product/{productId}/total")
    public Response<Long> getTotalStock(@PathVariable String productId) {
        Long total = warehouseService.getTotalStockByProductId(productId);
        return new Response<Long>().withDataAndStatus(total, HttpStatus.OK);
    }
}

