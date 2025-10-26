package web_ecommerce.sale_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web_ecommerce.sale_service.enitty.WarehouseDetail;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseDetailRepository extends JpaRepository<WarehouseDetail, String> {
    List<WarehouseDetail> findByWarehouseId(Long warehouseId);
    
    List<WarehouseDetail> findByProductId(String productId);
    
    Optional<WarehouseDetail> findByWarehouseIdAndProductId(Long warehouseId, String productId);
    
    @Query("SELECT SUM(wd.stock) FROM WarehouseDetail wd WHERE wd.productId = :productId")
    Long getTotalStockByProductId(@Param("productId") String productId);
}

