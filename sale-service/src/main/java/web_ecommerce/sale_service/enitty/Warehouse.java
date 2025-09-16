package web_ecommerce.sale_service.enitty;


import jakarta.persistence.*;
import lombok.*;
import web_ecommerce.core.db.BaseEntityNonId;
import web_ecommerce.core.validation.annotation.ColumnComment;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse extends BaseEntityNonId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @ColumnComment("Tên kho")
    private String name;

    @ColumnComment("Mã kho")
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(columnDefinition = "TEXT")
    @ColumnComment("Địa chỉ")
    private String address;

    @Column(length = 100)
    @ColumnComment("Thành phố")
    private String city;

    @Column(length = 100)
    @ColumnComment("Tỉnh/Thành phố")
    private String state;

    @Column(length = 100)
    @ColumnComment("Quốc gia")
    private String country;

    @Column(name = "postal_code", length = 20)
    @ColumnComment("Mã bưu điện")
    private String postalCode;

    @ColumnComment("Kho chứa tối đa")
    private Integer capacity;

    @Column(name = "is_active", nullable = false)
    @ColumnComment("Trạng thái")
    private Boolean isActive = true;
}
