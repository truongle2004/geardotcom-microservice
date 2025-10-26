package web_ecommerce.sale_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web_ecommerce.sale_service.dto.CreateProductCategoryDto;
import web_ecommerce.sale_service.dto.ProductCategoryDto;
import web_ecommerce.sale_service.enitty.ProductCategory;
import web_ecommerce.sale_service.repository.ProductCategoryRepository;
import web_ecommerce.sale_service.service.ProductCategoryService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductCategoryServiceImpl implements ProductCategoryService {
    private final ProductCategoryRepository categoryRepository;

    @Override
    public ProductCategoryDto createCategory(CreateProductCategoryDto createDto) {
        log.debug("Creating new category: {}", createDto.getName());
        
        if (categoryRepository.existsByHandle(createDto.getHandle())) {
            throw new RuntimeException("Category with handle " + createDto.getHandle() + " already exists");
        }
        
        if (categoryRepository.existsByName(createDto.getName())) {
            throw new RuntimeException("Category with name " + createDto.getName() + " already exists");
        }
        
        ProductCategory category = ProductCategory.builder()
                .name(createDto.getName())
                .handle(createDto.getHandle())
                .description(createDto.getDescription())
                .sortOrder(createDto.getSortOrder() != null ? createDto.getSortOrder() : 0)
                .isFeatured(createDto.getIsFeatured() != null ? createDto.getIsFeatured() : false)
                .isActive(createDto.getIsActive() != null ? createDto.getIsActive() : true)
                .productCount(0)
                .build();
        
        ProductCategory saved = categoryRepository.save(category);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductCategoryDto> getCategoryById(String id) {
        return categoryRepository.findById(id).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductCategoryDto> getCategoryByHandle(String handle) {
        return categoryRepository.findByHandle(handle).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductCategoryDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDto> getFeaturedCategories() {
        return categoryRepository.findByIsFeaturedTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDto> getActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderBySortOrderAsc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCategoryDto updateCategory(String id, CreateProductCategoryDto updateDto) {
        log.debug("Updating category: {}", id);
        
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        if (updateDto.getName() != null && !updateDto.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(updateDto.getName())) {
                throw new RuntimeException("Category with name " + updateDto.getName() + " already exists");
            }
            category.setName(updateDto.getName());
        }
        
        if (updateDto.getHandle() != null && !updateDto.getHandle().equals(category.getHandle())) {
            if (categoryRepository.existsByHandle(updateDto.getHandle())) {
                throw new RuntimeException("Category with handle " + updateDto.getHandle() + " already exists");
            }
            category.setHandle(updateDto.getHandle());
        }
        
        if (updateDto.getDescription() != null) {
            category.setDescription(updateDto.getDescription());
        }
        if (updateDto.getSortOrder() != null) {
            category.setSortOrder(updateDto.getSortOrder());
        }
        if (updateDto.getIsFeatured() != null) {
            category.setIsFeatured(updateDto.getIsFeatured());
        }
        if (updateDto.getIsActive() != null) {
            category.setIsActive(updateDto.getIsActive());
        }
        
        ProductCategory updated = categoryRepository.save(category);
        return mapToDto(updated);
    }

    @Override
    public void deleteCategory(String id) {
        log.debug("Deleting category: {}", id);
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        if (category.getProductCount() > 0) {
            throw new RuntimeException("Cannot delete category with existing products");
        }
        
        categoryRepository.delete(category);
    }

    @Override
    public void updateProductCount(String categoryId, int delta) {
        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        category.setProductCount(category.getProductCount() + delta);
        categoryRepository.save(category);
    }

    private ProductCategoryDto mapToDto(ProductCategory entity) {
        return ProductCategoryDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .handle(entity.getHandle())
                .description(entity.getDescription())
                .sortOrder(entity.getSortOrder())
                .isFeatured(entity.getIsFeatured())
                .isActive(entity.getIsActive())
                .productCount(entity.getProductCount())
                .build();
    }
}

