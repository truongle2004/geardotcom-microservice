package web_ecommerce.sale_service.enitty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import web_ecommerce.core.db.BaseEntityNonId;
import web_ecommerce.core.validation.annotation.ColumnComment;

import java.math.BigDecimal;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = "handle")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntityNonId {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ColumnComment("handle sản phẩm")
    @Column(nullable = false, unique = true)
    private String handle;

    @ColumnComment("Gía sản phẩm")
    @Column(nullable = false)
    private BigDecimal price;

    @ColumnComment("Mô tả")
    @Column(columnDefinition = "TEXT")
    private String description;

    @ColumnComment("tiêu đề")
    @Column(nullable = false, length = 500)
    private String title;

    @ColumnComment("id kho")
    @Column(name = "warehouse_id", length = 36)
    private String warehouseId;

    @ColumnComment("id nhà cung cấp")
    @Column(name = "product_vendor_id")
    private String productVendorId;

    @ColumnComment("id danh mục")
    @Column(name = "product_category_id")
    private String productCategoryId;

    @ColumnComment("Phạm vi hiển thị (ví dụ: 'web', 'mobile')")
    @Column(name = "published_scope", length = 50)
    private String publishedScope = "web";

    @ColumnComment("Số lượng sản phẩm đã được mua")
    @Column(name = "purchase_count")
    private Integer purchaseCount = 0;

    @ColumnComment("Đánh giá trung bình của sản phẩm (0.00 - 5.00)")
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.valueOf(0.00);

    @ColumnComment("Tổng số lượt đánh giá sản phẩm")
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @ColumnComment("Các thẻ liên quan đến sản phẩm (tags), cách nhau bằng dấu phẩy")
    @Column(columnDefinition = "TEXT")
    private String tags;

    @ColumnComment("Số lượng đã bán")
    @Column(name = "sole_quantity")
    private Integer soleQuantity = 0;

    @ColumnComment("Cờ đánh dấu sản phẩm không được áp dụng khuyến mãi")
    @Column(name = "not_allow_promotion")
    private Boolean notAllowPromotion = false;

    @ColumnComment("Trạng thái sản phẩm còn bán hay không")
    @Column(name = "available")
    private Boolean available = true;
}
