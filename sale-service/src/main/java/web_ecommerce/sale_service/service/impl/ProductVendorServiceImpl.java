package web_ecommerce.sale_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web_ecommerce.sale_service.dto.CreateProductVendorDto;
import web_ecommerce.sale_service.dto.ProductVendorDto;
import web_ecommerce.sale_service.enitty.ProductVendor;
import web_ecommerce.sale_service.repository.ProductVendorRepository;
import web_ecommerce.sale_service.service.ProductVendorService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductVendorServiceImpl implements ProductVendorService {
    private final ProductVendorRepository vendorRepository;

    @Override
    public ProductVendorDto createVendor(CreateProductVendorDto createDto) {
        log.debug("Creating new vendor: {}", createDto.getName());
        
        if (vendorRepository.existsByHandle(createDto.getHandle())) {
            throw new RuntimeException("Vendor with handle " + createDto.getHandle() + " already exists");
        }
        
        if (vendorRepository.existsByName(createDto.getName())) {
            throw new RuntimeException("Vendor with name " + createDto.getName() + " already exists");
        }
        
        ProductVendor vendor = new ProductVendor();
        vendor.setName(createDto.getName());
        vendor.setHandle(createDto.getHandle());
        vendor.setProduct_category_id(createDto.getProductCategoryId());
        vendor.setDescription(createDto.getDescription());
        
        ProductVendor saved = vendorRepository.save(vendor);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVendorDto> getVendorById(String id) {
        return vendorRepository.findById(id).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductVendorDto> getVendorByHandle(String handle) {
        return vendorRepository.findByHandle(handle).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductVendorDto> getAllVendors(Pageable pageable) {
        return vendorRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVendorDto> getAllVendorsList() {
        return vendorRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductVendorDto updateVendor(String id, CreateProductVendorDto updateDto) {
        log.debug("Updating vendor: {}", id);
        
        ProductVendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));
        
        if (updateDto.getName() != null && !updateDto.getName().equals(vendor.getName())) {
            if (vendorRepository.existsByName(updateDto.getName())) {
                throw new RuntimeException("Vendor with name " + updateDto.getName() + " already exists");
            }
            vendor.setName(updateDto.getName());
        }
        
        if (updateDto.getHandle() != null && !updateDto.getHandle().equals(vendor.getHandle())) {
            if (vendorRepository.existsByHandle(updateDto.getHandle())) {
                throw new RuntimeException("Vendor with handle " + updateDto.getHandle() + " already exists");
            }
            vendor.setHandle(updateDto.getHandle());
        }
        
        if (updateDto.getProductCategoryId() != null) {
            vendor.setProduct_category_id(updateDto.getProductCategoryId());
        }
        if (updateDto.getDescription() != null) {
            vendor.setDescription(updateDto.getDescription());
        }
        
        ProductVendor updated = vendorRepository.save(vendor);
        return mapToDto(updated);
    }

    @Override
    public void deleteVendor(String id) {
        log.debug("Deleting vendor: {}", id);
        ProductVendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));
        vendorRepository.delete(vendor);
    }

    private ProductVendorDto mapToDto(ProductVendor entity) {
        return ProductVendorDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .handle(entity.getHandle())
                .productCategoryId(entity.getProduct_category_id())
                .description(entity.getDescription())
                .build();
    }
}

