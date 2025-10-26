package web_ecommerce.sale_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web_ecommerce.sale_service.enitty.ProductVendor;

import java.util.Optional;

@Repository
public interface ProductVendorRepository extends JpaRepository<ProductVendor, String> {
    Optional<ProductVendor> findByHandle(String handle);
    
    boolean existsByHandle(String handle);
    
    boolean existsByName(String name);
}

