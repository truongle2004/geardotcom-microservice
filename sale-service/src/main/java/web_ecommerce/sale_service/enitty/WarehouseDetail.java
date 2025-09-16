package web_ecommerce.sale_service.enitty;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import web_ecommerce.core.db.BaseEntityNonId;
import web_ecommerce.core.validation.annotation.ColumnComment;

@Entity
@Table(name = "warehouse_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseDetail extends BaseEntityNonId {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "warehouse_id")
    @ColumnComment("id kho")
    private long warehouseId;

    @Column(name = "product_id")
    @ColumnComment("id san pham")
    private String productId;

    @Column(name = "stock")
    @ColumnComment("So luong")
    private long stock;
}
