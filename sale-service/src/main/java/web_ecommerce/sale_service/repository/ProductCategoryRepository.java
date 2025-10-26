package web_ecommerce.sale_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web_ecommerce.sale_service.enitty.ProductCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {
    Optional<ProductCategory> findByHandle(String handle);
    
    List<ProductCategory> findByIsFeaturedTrue();
    
    List<ProductCategory> findByIsActiveTrue();
    
    List<ProductCategory> findByIsActiveTrueOrderBySortOrderAsc();
    
    boolean existsByHandle(String handle);
    
    boolean existsByName(String name);
}

