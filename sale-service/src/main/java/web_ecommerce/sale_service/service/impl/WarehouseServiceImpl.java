package web_ecommerce.sale_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web_ecommerce.sale_service.dto.CreateWarehouseDto;
import web_ecommerce.sale_service.dto.UpdateWarehouseStockDto;
import web_ecommerce.sale_service.dto.WarehouseDetailDto;
import web_ecommerce.sale_service.dto.WarehouseDto;
import web_ecommerce.sale_service.enitty.Warehouse;
import web_ecommerce.sale_service.enitty.WarehouseDetail;
import web_ecommerce.sale_service.repository.WarehouseDetailRepository;
import web_ecommerce.sale_service.repository.WarehouseRepository;
import web_ecommerce.sale_service.service.WarehouseService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseDetailRepository warehouseDetailRepository;

    @Override
    public WarehouseDto createWarehouse(CreateWarehouseDto createDto) {
        log.debug("Creating new warehouse: {}", createDto.getName());
        
        if (warehouseRepository.existsByCode(createDto.getCode())) {
            throw new RuntimeException("Warehouse with code " + createDto.getCode() + " already exists");
        }
        
        Warehouse warehouse = Warehouse.builder()
                .name(createDto.getName())
                .code(createDto.getCode())
                .address(createDto.getAddress())
                .city(createDto.getCity())
                .state(createDto.getState())
                .country(createDto.getCountry())
                .postalCode(createDto.getPostalCode())
                .capacity(createDto.getCapacity())
                .isActive(createDto.getIsActive() != null ? createDto.getIsActive() : true)
                .build();
        
        Warehouse saved = warehouseRepository.save(warehouse);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseDto> getWarehouseById(Long id) {
        return warehouseRepository.findById(id).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WarehouseDto> getWarehouseByCode(String code) {
        return warehouseRepository.findByCode(code).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseDto> getAllWarehouses(Pageable pageable) {
        return warehouseRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDto> getActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseDto updateWarehouse(Long id, CreateWarehouseDto updateDto) {
        log.debug("Updating warehouse: {}", id);
        
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        
        if (updateDto.getCode() != null && !updateDto.getCode().equals(warehouse.getCode())) {
            if (warehouseRepository.existsByCode(updateDto.getCode())) {
                throw new RuntimeException("Warehouse with code " + updateDto.getCode() + " already exists");
            }
            warehouse.setCode(updateDto.getCode());
        }
        
        if (updateDto.getName() != null) {
            warehouse.setName(updateDto.getName());
        }
        if (updateDto.getAddress() != null) {
            warehouse.setAddress(updateDto.getAddress());
        }
        if (updateDto.getCity() != null) {
            warehouse.setCity(updateDto.getCity());
        }
        if (updateDto.getState() != null) {
            warehouse.setState(updateDto.getState());
        }
        if (updateDto.getCountry() != null) {
            warehouse.setCountry(updateDto.getCountry());
        }
        if (updateDto.getPostalCode() != null) {
            warehouse.setPostalCode(updateDto.getPostalCode());
        }
        if (updateDto.getCapacity() != null) {
            warehouse.setCapacity(updateDto.getCapacity());
        }
        if (updateDto.getIsActive() != null) {
            warehouse.setIsActive(updateDto.getIsActive());
        }
        
        Warehouse updated = warehouseRepository.save(warehouse);
        return mapToDto(updated);
    }

    @Override
    public void deleteWarehouse(Long id) {
        log.debug("Deleting warehouse: {}", id);
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        
        // Delete associated warehouse details first
        List<WarehouseDetail> details = warehouseDetailRepository.findByWarehouseId(id);
        warehouseDetailRepository.deleteAll(details);
        
        warehouseRepository.delete(warehouse);
    }

    @Override
    public WarehouseDetailDto updateStock(UpdateWarehouseStockDto updateStockDto) {
        log.debug("Updating stock for product {} in warehouse {}", 
                updateStockDto.getProductId(), updateStockDto.getWarehouseId());
        
        // Verify warehouse exists
        warehouseRepository.findById(updateStockDto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + updateStockDto.getWarehouseId()));
        
        Optional<WarehouseDetail> existing = warehouseDetailRepository
                .findByWarehouseIdAndProductId(updateStockDto.getWarehouseId(), updateStockDto.getProductId());
        
        WarehouseDetail detail;
        if (existing.isPresent()) {
            detail = existing.get();
            detail.setStock(updateStockDto.getStock());
        } else {
            detail = WarehouseDetail.builder()
                    .warehouseId(updateStockDto.getWarehouseId())
                    .productId(updateStockDto.getProductId())
                    .stock(updateStockDto.getStock())
                    .build();
        }
        
        WarehouseDetail saved = warehouseDetailRepository.save(detail);
        return mapDetailToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDetailDto> getStockByProductId(String productId) {
        return warehouseDetailRepository.findByProductId(productId).stream()
                .map(this::mapDetailToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDetailDto> getStockByWarehouseId(Long warehouseId) {
        return warehouseDetailRepository.findByWarehouseId(warehouseId).stream()
                .map(this::mapDetailToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalStockByProductId(String productId) {
        Long total = warehouseDetailRepository.getTotalStockByProductId(productId);
        return total != null ? total : 0L;
    }

    private WarehouseDto mapToDto(Warehouse entity) {
        return WarehouseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .address(entity.getAddress())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .postalCode(entity.getPostalCode())
                .capacity(entity.getCapacity())
                .isActive(entity.getIsActive())
                .build();
    }

    private WarehouseDetailDto mapDetailToDto(WarehouseDetail entity) {
        return WarehouseDetailDto.builder()
                .id(entity.getId())
                .warehouseId(entity.getWarehouseId())
                .productId(entity.getProductId())
                .stock(entity.getStock())
                .build();
    }
}

