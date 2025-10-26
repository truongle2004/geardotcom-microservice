package web_ecommerce.sale_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.engine.search.sort.dsl.CompositeSortComponentsStep;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.core.enums.ImageEnum;
import web_ecommerce.sale_service.dto.ProductDTO;
import web_ecommerce.sale_service.dto.ProductImageDTO;
import web_ecommerce.sale_service.enitty.Product;
import web_ecommerce.sale_service.repository.ProductImageRepository;
import web_ecommerce.sale_service.service.VietnameseSearchService;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of Vietnamese text search service
 * Provides enhanced Vietnamese text search capabilities with diacritics handling
 */
@Service
@Slf4j
public class VietnameseSearchServiceImpl implements VietnameseSearchService {
    
    private final EntityManager entityManager;
    private final ProductImageRepository productImageRepository;
    
    @Value("${file_upload-url}")
    private String FILE_UPLOAD_URL;
    
    public VietnameseSearchServiceImpl(EntityManager entityManager, ProductImageRepository productImageRepository) {
        this.entityManager = entityManager;
        this.productImageRepository = productImageRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "searchResults", key = "#query + '_' + #category + '_' + #vendor + '_' + #minPrice + '_' + #maxPrice + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Response<Page<ProductDTO>> searchProductsVietnamese(Pageable pageable, String query, String category, String vendor, BigDecimal minPrice, BigDecimal maxPrice) {
        try {
            SearchSession searchSession = Search.session(entityManager);
            
            // Build enhanced Vietnamese search query
            SearchQueryOptionsStep<?, ?, ?, ?, ?> searchQuery = buildVietnameseSearchQuery(searchSession, query, category, vendor, minPrice, maxPrice);
            
            // Apply sorting
            if (pageable.getSort().isSorted()) {
                searchQuery = applySorting(searchQuery, pageable.getSort());
            }
            
            // Execute search
            SearchResult<Product> result = (SearchResult<Product>) searchQuery.fetch((int) pageable.getOffset(), pageable.getPageSize());
            
            // Extract results
            List<Product> hits = result.hits();
            long totalCount = result.total().hitCount();
            
            // Convert to DTOs
            List<ProductDTO> dtos = convertProductsToDTOs(hits);
            
            // Create paginated response
            Page<ProductDTO> page = new org.springframework.data.domain.PageImpl<>(dtos, pageable, totalCount);
            return new Response<Page<ProductDTO>>().withDataAndStatus(page, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error occurred during Vietnamese product search", e);
            return new Response<Page<ProductDTO>>().withDataAndStatus(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Response<Page<ProductDTO>> searchProductsNormalized(Pageable pageable, String query, String category, String vendor, BigDecimal minPrice, BigDecimal maxPrice) {
        try {
            SearchSession searchSession = Search.session(entityManager);
            
            // Normalize Vietnamese text for better matching
            String normalizedQuery = normalizeVietnameseText(query);
            
            // Build search query with normalized text
            SearchQueryOptionsStep<?, ?, ?, ?, ?> searchQuery = buildVietnameseSearchQuery(searchSession, normalizedQuery, category, vendor, minPrice, maxPrice);
            
            // Apply sorting
            if (pageable.getSort().isSorted()) {
                searchQuery = applySorting(searchQuery, pageable.getSort());
            }
            
            // Execute search
            SearchResult<Product> result = (SearchResult<Product>) searchQuery.fetch((int) pageable.getOffset(), pageable.getPageSize());
            
            // Extract results
            List<Product> hits = result.hits();
            long totalCount = result.total().hitCount();
            
            // Convert to DTOs
            List<ProductDTO> dtos = convertProductsToDTOs(hits);
            
            // Create paginated response
            Page<ProductDTO> page = new org.springframework.data.domain.PageImpl<>(dtos, pageable, totalCount);
            return new Response<Page<ProductDTO>>().withDataAndStatus(page, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error occurred during normalized Vietnamese product search", e);
            return new Response<Page<ProductDTO>>().withDataAndStatus(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "searchSuggestions", key = "#query + '_' + #limit")
    public Response<List<String>> getSearchSuggestions(String query, int limit) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return new Response<List<String>>().withDataAndStatus(List.of(), HttpStatus.OK);
            }
            
            SearchSession searchSession = Search.session(entityManager);
            String normalizedQuery = normalizeVietnameseText(query);
            
            // Search for product titles that match the query
            SearchResult<Product> result = searchSession.search(Product.class)
                .where(f -> f.simpleQueryString()
                    .fields("title^3", "description^1")
                    .matching(normalizedQuery)
                    .defaultOperator(org.hibernate.search.engine.search.common.BooleanOperator.OR))
                .fetch(limit);
            
            // Extract unique suggestions from titles
            List<String> suggestions = result.hits().stream()
                .map(Product::getTitle)
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
            
            return new Response<List<String>>().withDataAndStatus(suggestions, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error occurred during search suggestions", e);
            return new Response<List<String>>().withDataAndStatus(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    @Transactional
    public void rebuildVietnameseIndexes() {
        try {
            log.info("Rebuilding Vietnamese search indexes...");
            SearchSession searchSession = Search.session(entityManager);
            searchSession.massIndexer(Product.class).startAndWait();
            log.info("Vietnamese search indexes rebuilt successfully");
        } catch (Exception e) {
            log.error("Error rebuilding Vietnamese search indexes", e);
        }
    }
    
    /**
     * Builds enhanced Vietnamese search query with multiple search strategies
     */
    private SearchQueryOptionsStep<?, ?, ?, ?, ?> buildVietnameseSearchQuery(SearchSession searchSession, String query, String category, String vendor, BigDecimal minPrice, BigDecimal maxPrice) {
        return searchSession.search(Product.class).where((SearchPredicateFactory f) -> {
            BooleanPredicateClausesStep<?> bool = f.bool();
            
            // Enhanced Vietnamese text search
            if (query != null && !query.isBlank()) {
                var searchBool = f.bool();
                
                // Primary search with Vietnamese analyzer and field boosting
                searchBool.should(f.simpleQueryString()
                    .fields("title^4", "description^2", "tags^3")
                    .matching(query)
                    .defaultOperator(org.hibernate.search.engine.search.common.BooleanOperator.OR));
                
                // Fuzzy search for Vietnamese text variations
                searchBool.should(f.fuzzy().field("title").matching(query).fuzzyTranspositions(true));
                searchBool.should(f.fuzzy().field("description").matching(query).fuzzyTranspositions(true));
                
                // Wildcard search for partial matches
                String lowerQuery = query.toLowerCase().trim();
                searchBool.should(f.wildcard().field("title").matching("*" + lowerQuery + "*"));
                searchBool.should(f.wildcard().field("description").matching("*" + lowerQuery + "*"));
                searchBool.should(f.wildcard().field("tags").matching("*" + lowerQuery + "*"));
                
                // Phrase search for exact Vietnamese phrases
                searchBool.should(f.phrase().field("title").matching(query));
                searchBool.should(f.phrase().field("description").matching(query));
                searchBool.should(f.phrase().field("tags").matching(query));
                
                // Search with normalized Vietnamese text (without diacritics)
                String normalizedQuery = normalizeVietnameseText(query);
                if (!normalizedQuery.equals(query)) {
                    searchBool.should(f.simpleQueryString()
                        .fields("title^2", "description^1", "tags^1.5")
                        .matching(normalizedQuery)
                        .defaultOperator(org.hibernate.search.engine.search.common.BooleanOperator.OR));
                }
                
                bool.must(searchBool);
            }
            
            // Category filter
            if (category != null && !category.isBlank() && !"all".equalsIgnoreCase(category)) {
                bool.filter(f.match().field("productCategoryId").matching(category));
            }
            
            // Vendor filter
            if (vendor != null && !vendor.isBlank() && !"all".equalsIgnoreCase(vendor)) {
                bool.filter(f.match().field("productVendorId").matching(vendor));
            }
            
            // Price range filter
            if (minPrice != null) {
                bool.filter(f.range().field("price").atLeast(minPrice));
            }
            if (maxPrice != null) {
                bool.filter(f.range().field("price").atMost(maxPrice));
            }
            
            // Only show available products
            bool.filter(f.match().field("available").matching(true));
            
            return bool;
        });
    }
    
    /**
     * Normalizes Vietnamese text by removing diacritics and standardizing characters
     */
    private String normalizeVietnameseText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        // Normalize Unicode characters
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        
        // Remove diacritics
        normalized = normalized.replaceAll("\\p{M}", "");
        
        // Convert to lowercase
        normalized = normalized.toLowerCase().trim();
        
        // Replace common Vietnamese character variations
        normalized = normalized.replace("đ", "d");
        normalized = normalized.replace("Đ", "d");
        
        return normalized;
    }
    
    /**
     * Applies sorting to search results
     */
    private SearchQueryOptionsStep<?, ?, ?, ?, ?> applySorting(SearchQueryOptionsStep<?, ?, ?, ?, ?> query, Sort sort) {
        return query.sort((SearchSortFactory f) -> {
            CompositeSortComponentsStep<?> sortStep = f.composite();
            
            for (Sort.Order order : sort) {
                switch (order.getProperty()) {
                    case "price":
                        if (order.getDirection() == Sort.Direction.ASC) {
                            sortStep.add(f.field("price").asc());
                        } else {
                            sortStep.add(f.field("price").desc());
                        }
                        break;
                    case "title":
                        if (order.getDirection() == Sort.Direction.ASC) {
                            sortStep.add(f.field("title_sort").asc());
                        } else {
                            sortStep.add(f.field("title_sort").desc());
                        }
                        break;
                    case "averageRating":
                        if (order.getDirection() == Sort.Direction.ASC) {
                            sortStep.add(f.field("averageRating").asc());
                        } else {
                            sortStep.add(f.field("averageRating").desc());
                        }
                        break;
                    case "reviewCount":
                        if (order.getDirection() == Sort.Direction.ASC) {
                            sortStep.add(f.field("reviewCount").asc());
                        } else {
                            sortStep.add(f.field("reviewCount").desc());
                        }
                        break;
                    case "purchaseCount":
                        if (order.getDirection() == Sort.Direction.ASC) {
                            sortStep.add(f.field("purchaseCount").asc());
                        } else {
                            sortStep.add(f.field("purchaseCount").desc());
                        }
                        break;
                    default:
                        // Default to relevance score
                        sortStep.add(f.score().desc());
                        break;
                }
            }
            
            return sortStep;
        });
    }
    
    /**
     * Converts Product entities to DTOs with image information
     */
    private List<ProductDTO> convertProductsToDTOs(List<Product> products) {
        return products.stream().map(product -> {
            ProductDTO dto = new ProductDTO();
            dto.setId(product.getId());
            dto.setHandle(product.getHandle());
            dto.setTitle(product.getTitle());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setWarehouseId(product.getWarehouseId());
            dto.setProductVendorId(product.getProductVendorId());
            dto.setProductCategoryId(product.getProductCategoryId());
            dto.setPublishedScope(product.getPublishedScope());
            dto.setPurchaseCount(product.getPurchaseCount());
            dto.setAverageRating(product.getAverageRating());
            dto.setReviewCount(product.getReviewCount());
            dto.setTags(product.getTags());
            dto.setSoleQuantity(product.getSoleQuantity());
            dto.setNotAllowPromotion(product.getNotAllowPromotion());
            dto.setAvailable(product.getAvailable());
            
            // Add images
            List<ProductImageDTO> images = productImageRepository.getByProductId(product.getId(), FILE_UPLOAD_URL);
            dto.setImages(images);
            
            return dto;
        }).collect(Collectors.toList());
    }
}
