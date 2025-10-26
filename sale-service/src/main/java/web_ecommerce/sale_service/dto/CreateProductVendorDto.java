package web_ecommerce.sale_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductVendorDto {
    private String name;
    private String handle;
    private String productCategoryId;
    private String description;
}

