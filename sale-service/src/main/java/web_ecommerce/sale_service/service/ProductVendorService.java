package web_ecommerce.sale_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import web_ecommerce.sale_service.dto.CreateProductVendorDto;
import web_ecommerce.sale_service.dto.ProductVendorDto;

import java.util.List;
import java.util.Optional;

public interface ProductVendorService {
    ProductVendorDto createVendor(CreateProductVendorDto createDto);
    
    Optional<ProductVendorDto> getVendorById(String id);
    
    Optional<ProductVendorDto> getVendorByHandle(String handle);
    
    Page<ProductVendorDto> getAllVendors(Pageable pageable);
    
    List<ProductVendorDto> getAllVendorsList();
    
    ProductVendorDto updateVendor(String id, CreateProductVendorDto updateDto);
    
    void deleteVendor(String id);
}

