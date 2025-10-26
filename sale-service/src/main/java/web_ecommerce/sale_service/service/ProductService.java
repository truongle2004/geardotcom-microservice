package web_ecommerce.sale_service.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.sale_service.dto.CategoryDTO;
import web_ecommerce.sale_service.dto.ProductDTO;
import web_ecommerce.sale_service.dto.VendorDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    Response<Page<ProductDTO>> getListProductByCategory(Pageable pageable, String category, String vendor, BigDecimal minPrice, BigDecimal maxPrice);
    Response<ProductDTO> getById(String id);
    Response<List<CategoryDTO>> getAllProductCategory();
    Response<List<VendorDTO>> getAllVendor();
    Response<Page<ProductDTO>> getBestSellers(Pageable pageable);
    Response<Page<ProductDTO>> getFeaturedProducts(Pageable pageable);
    Response<Page<ProductDTO>> getTopRatedProducts(Pageable pageable);

    Response<Page<ProductDTO>> searchProducts(Pageable pageable, String q, String category, String vendor, BigDecimal minPrice, BigDecimal maxPrice);
 }
