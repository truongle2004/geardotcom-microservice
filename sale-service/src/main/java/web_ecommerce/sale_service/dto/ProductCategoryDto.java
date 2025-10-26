package web_ecommerce.sale_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategoryDto {
    private String id;
    private String name;
    private String handle;
    private String description;
    private Integer sortOrder;
    private Boolean isFeatured;
    private Boolean isActive;
    private Integer productCount;
}

