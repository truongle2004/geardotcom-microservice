package web_ecommerce.sale_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductCategoryDto {
    private String name;
    private String handle;
    private String description;
    private Integer sortOrder;
    private Boolean isFeatured;
    private Boolean isActive;
}

