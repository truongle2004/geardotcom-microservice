package web_ecommerce.sale_service.enitty;

import lombok.*;

import jakarta.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import web_ecommerce.core.db.BaseEntityNonId;
import web_ecommerce.core.validation.annotation.ColumnComment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_categories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name"}),
                @UniqueConstraint(columnNames = {"handle"})
        }
)
public class ProductCategory extends BaseEntityNonId {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    @ColumnComment("Tên danh mục")
    private String name;

    @Column(name = "handle", nullable = false, unique = true)
    @ColumnComment("Định danh URL")
    private String handle;

    @Column(name = "description")
    @ColumnComment("Mô tả")
    private String description;

    @Column(name = "sort_order")
    @ColumnComment("Thứ tự sắp xếp")
    private Integer sortOrder = 0;

    @Column(name = "is_featured")
    @ColumnComment("Nổi bật")
    private Boolean isFeatured = false;

    @Column(name = "is_active")
    @ColumnComment("Có hiển thị")
    private Boolean isActive = true;

    @Column(name = "product_count")
    @ColumnComment("Số lượng sản phẩm")
    private Integer productCount = 0;
}
