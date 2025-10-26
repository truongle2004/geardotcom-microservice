package web_ecommerce.sale_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import web_ecommerce.sale_service.dto.CreateWarehouseDto;
import web_ecommerce.sale_service.dto.UpdateWarehouseStockDto;
import web_ecommerce.sale_service.dto.WarehouseDetailDto;
import web_ecommerce.sale_service.dto.WarehouseDto;

import java.util.List;
import java.util.Optional;

public interface WarehouseService {
    WarehouseDto createWarehouse(CreateWarehouseDto createDto);
    
    Optional<WarehouseDto> getWarehouseById(Long id);
    
    Optional<WarehouseDto> getWarehouseByCode(String code);
    
    Page<WarehouseDto> getAllWarehouses(Pageable pageable);
    
    List<WarehouseDto> getActiveWarehouses();
    
    WarehouseDto updateWarehouse(Long id, CreateWarehouseDto updateDto);
    
    void deleteWarehouse(Long id);
    
    // Stock management
    WarehouseDetailDto updateStock(UpdateWarehouseStockDto updateStockDto);
    
    List<WarehouseDetailDto> getStockByProductId(String productId);
    
    List<WarehouseDetailDto> getStockByWarehouseId(Long warehouseId);
    
    Long getTotalStockByProductId(String productId);
}

