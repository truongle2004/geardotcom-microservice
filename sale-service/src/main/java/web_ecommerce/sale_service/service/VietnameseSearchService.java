package web_ecommerce.sale_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.sale_service.dto.ProductDTO;

import java.math.BigDecimal;

/**
 * Enhanced search service for Vietnamese text search capabilities
 */
public interface VietnameseSearchService {
    
    /**
     * Search products with Vietnamese text support
     * @param pageable pagination information
     * @param query search query (Vietnamese text)
     * @param category product category filter
     * @param vendor vendor filter
     * @param minPrice minimum price filter
     * @param maxPrice maximum price filter
     * @return paginated search results
     */
    Response<Page<ProductDTO>> searchProductsVietnamese(Pageable pageable, String query, String category, String vendor, BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Search products with Vietnamese text normalization
     * Handles diacritics removal and common Vietnamese search patterns
     * @param pageable pagination information
     * @param query search query (Vietnamese text)
     * @param category product category filter
     * @param vendor vendor filter
     * @param minPrice minimum price filter
     * @param maxPrice maximum price filter
     * @return paginated search results
     */
    Response<Page<ProductDTO>> searchProductsNormalized(Pageable pageable, String query, String category, String vendor, BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Get search suggestions for autocomplete
     * @param query partial search query
     * @param limit maximum number of suggestions
     * @return list of search suggestions
     */
    Response<java.util.List<String>> getSearchSuggestions(String query, int limit);
    
    /**
     * Rebuild search indexes with Vietnamese text processing
     */
    void rebuildVietnameseIndexes();
}
