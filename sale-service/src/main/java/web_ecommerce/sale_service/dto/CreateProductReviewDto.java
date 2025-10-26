package web_ecommerce.sale_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductReviewDto {
    private String productId;
    private Integer rating;
    private String title;
    private String comment;
}

