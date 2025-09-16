package web_ecommerce.sale_service.enitty;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import web_ecommerce.core.db.BaseEntityNonId;
import web_ecommerce.core.validation.annotation.ColumnComment;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "product_vendors", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class ProductVendor extends BaseEntityNonId {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ColumnComment("Tên")
    @Column(name = "name", nullable = false)
    private String name;

    @ColumnComment("Mã")
    @Column(name = "handle")
    private String handle;

    @Column(name = "product_category_id")
    private String product_category_id;

    @Column(name = "description")
    private String description;
}
