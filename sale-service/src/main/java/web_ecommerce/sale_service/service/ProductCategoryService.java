package web_ecommerce.sale_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import web_ecommerce.sale_service.dto.CreateProductCategoryDto;
import web_ecommerce.sale_service.dto.ProductCategoryDto;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryService {
    ProductCategoryDto createCategory(CreateProductCategoryDto createDto);
    
    Optional<ProductCategoryDto> getCategoryById(String id);
    
    Optional<ProductCategoryDto> getCategoryByHandle(String handle);
    
    Page<ProductCategoryDto> getAllCategories(Pageable pageable);
    
    List<ProductCategoryDto> getFeaturedCategories();
    
    List<ProductCategoryDto> getActiveCategories();
    
    ProductCategoryDto updateCategory(String id, CreateProductCategoryDto updateDto);
    
    void deleteCategory(String id);
    
    void updateProductCount(String categoryId, int delta);
}

