package web_ecommerce.sale_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
// Avoid explicit SearchQuerySelectStep to prevent generic mismatches across versions
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.sort.dsl.SearchSortFactory;
import org.hibernate.search.engine.search.sort.dsl.CompositeSortComponentsStep;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.data.domain.Sort;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.core.enums.ImageEnum;
import web_ecommerce.sale_service.dto.CategoryDTO;
import web_ecommerce.sale_service.dto.ProductDTO;
import web_ecommerce.sale_service.dto.ProductImageDTO;
import web_ecommerce.sale_service.dto.VendorDTO;
import web_ecommerce.sale_service.enitty.Product;
import web_ecommerce.sale_service.repository.ProductImageRepository;
import web_ecommerce.sale_service.repository.ProductRepository;
import web_ecommerce.sale_service.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final EntityManager entityManager;

    @Value("${file_upload-url}")
    private String FILE_UPLOAD_URL;

    public ProductServiceImpl(ProductRepository productRepository, ProductImageRepository productImageRepository, EntityManager entityManager) {
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
        this.entityManager = entityManager;
    }

    /**
     * Rebuild search indexes on application startup
     */
    @PostConstruct
    @Transactional
    public void rebuildSearchIndexes() {
        try {
            log.info("Rebuilding search indexes...");
            SearchSession searchSession = Search.session(entityManager);
            searchSession.massIndexer(Product.class).startAndWait();
            log.info("Search indexes rebuilt successfully");
        } catch (Exception e) {
            log.error("Error rebuilding search indexes", e);
        }
    }

    @Override
    public Response<Page<ProductDTO>> getListProductByCategory(Pageable pageable, String category, String vendor, BigDecimal minPrice, BigDecimal maxPrice) {
        Page<ProductDTO> productDTOS = productRepository.getListProduct(pageable, ImageEnum.MAIN_IMAGE.getValue(), category, vendor, minPrice, maxPrice);
        productDTOS.forEach(productDTO -> {
            productDTO.setImages(productImageRepository.getByProductId(productDTO.getId(), FILE_UPLOAD_URL));
        });
        return new Response<Page<ProductDTO>>().withDataAndStatus(productDTOS, HttpStatus.OK);
    }

    @Override
    public Response<ProductDTO> getById(String productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            return new Response<ProductDTO>().withDataAndStatus(null, HttpStatus.NOT_FOUND);
        }

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.get().getId());
        productDTO.setHandle(product.get().getHandle());
        productDTO.setDescription(product.get().getDescription());
        productDTO.setTitle(product.get().getTitle());
        productDTO.setPrice(product.get().getPrice());
        productDTO.setPublishedScope(product.get().getPublishedScope());
        productDTO.setPurchaseCount(product.get().getPurchaseCount());
        productDTO.setAverageRating(product.get().getAverageRating());
        productDTO.setReviewCount(product.get().getReviewCount());
        productDTO.setTags(product.get().getTags());
        productDTO.setSoleQuantity(product.get().getSoleQuantity());
        productDTO.setNotAllowPromotion(product.get().getNotAllowPromotion());
        productDTO.setAvailable(product.get().getAvailable());
        productDTO.setPublishedScope(product.get().getPublishedScope());

        List<ProductImageDTO> productImageDTO = productImageRepository.getByProductId(productId, FILE_UPLOAD_URL);
        if (!productImageDTO.isEmpty()) {
            productDTO.setImages(productImageDTO);
        }

        return new Response<ProductDTO>().withDataAndStatus(productDTO, HttpStatus.OK);
    }

    @Override
    public Response<List<CategoryDTO>> getAllProductCategory() {
        return new Response<List<CategoryDTO>>().withDataAndStatus(productRepository.getAllProductCategory(), HttpStatus.OK);
    }

    @Override
    public Response<List<VendorDTO>> getAllVendor() {
        return new Response<List<VendorDTO>>().withDataAndStatus(productRepository.getAll(), HttpStatus.OK);
    }

    @Override
    public Response<Page<ProductDTO>> getBestSellers(Pageable pageable) {
        // Products with highest sold quantity
        Page<ProductDTO> products = productRepository.getListProduct(
                org.springframework.data.domain.PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "soleQuantity")
                ),
                ImageEnum.MAIN_IMAGE.getValue(),
                "",
                "",
                null,
                null
        );
        products.forEach(product -> {
            product.setImages(productImageRepository.getByProductId(product.getId(), FILE_UPLOAD_URL));
        });
        return new Response<Page<ProductDTO>>().withDataAndStatus(products, HttpStatus.OK);
    }

    @Override
    public Response<Page<ProductDTO>> getFeaturedProducts(Pageable pageable) {
        // Products marked as featured (can be enhanced with a Product.isFeatured flag if needed)
        // For now, we'll return highest-rated available products
        Page<ProductDTO> products = productRepository.getListProduct(
                org.springframework.data.domain.PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "averageRating", "reviewCount")
                ),
                ImageEnum.MAIN_IMAGE.getValue(),
                "",
                "",
                null,
                null
        );
        products.forEach(product -> {
            product.setImages(productImageRepository.getByProductId(product.getId(), FILE_UPLOAD_URL));
        });
        return new Response<Page<ProductDTO>>().withDataAndStatus(products, HttpStatus.OK);
    }

    @Override
    public Response<Page<ProductDTO>> getTopRatedProducts(Pageable pageable) {
        // Products with best average ratings and review count
        Page<ProductDTO> products = productRepository.getListProduct(
                org.springframework.data.domain.PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "averageRating", "reviewCount")
                ),
                ImageEnum.MAIN_IMAGE.getValue(),
                "",
                "",
                null,
                null
        );
        products.forEach(product -> {
            product.setImages(productImageRepository.getByProductId(product.getId(), FILE_UPLOAD_URL));
        });
        return new Response<Page<ProductDTO>>().withDataAndStatus(products, HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public Response<Page<ProductDTO>> searchProducts(Pageable pageable, String q, String category, String vendor, BigDecimal minPrice, BigDecimal maxPrice) {
        try {
            SearchSession searchSession = Search.session(entityManager);
            
            // Build search query with filters
            SearchQueryOptionsStep<?, ?, ?, ?, ?> query = buildSearchQuery(searchSession, q, category, vendor, minPrice, maxPrice);
            
            // Apply sorting if specified
            if (pageable.getSort().isSorted()) {
                query = applySorting(query, pageable.getSort());
            }
            
            // Execute search
            SearchResult<Product> result = (SearchResult<Product>) query.fetch((int) pageable.getOffset(), pageable.getPageSize());
            
            // Extract results
            List<Product> hits = result.hits();
            long totalCount = result.total().hitCount();
            
            // Convert to DTOs
            List<ProductDTO> dtos = convertProductsToDTOs(hits);
            
            // Create paginated response
            Page<ProductDTO> page = new org.springframework.data.domain.PageImpl<>(dtos, pageable, totalCount);
            return new Response<Page<ProductDTO>>().withDataAndStatus(page, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error occurred during product search", e);
            return new Response<Page<ProductDTO>>().withDataAndStatus(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Builds the search query with all filters
     */
    private SearchQueryOptionsStep<?, ?, ?, ?, ?> buildSearchQuery(SearchSession searchSession, String q, String category, String vendor, BigDecimal minPrice, BigDecimal maxPrice) {
        return searchSession.search(Product.class).where((SearchPredicateFactory f) -> {
            BooleanPredicateClausesStep<?> bool = f.bool();
            
            // Full-text search with multiple approaches for better Vietnamese text matching
            if (q != null && !q.isBlank()) {
                var searchBool = f.bool();
                
                // Primary search with field boosting
                searchBool.should(f.simpleQueryString()
                    .fields("title^3", "description^2", "tags^1.5")
                    .matching(q)
                    .defaultOperator(org.hibernate.search.engine.search.common.BooleanOperator.OR));
                
                // Additional wildcard search for partial matches (case insensitive)
                String lowerQuery = q.toLowerCase().trim();
                searchBool.should(f.wildcard().field("title").matching("*" + lowerQuery + "*"));
                searchBool.should(f.wildcard().field("description").matching("*" + lowerQuery + "*"));
                searchBool.should(f.wildcard().field("tags").matching("*" + lowerQuery + "*"));
                
                // Phrase search for exact matches
                searchBool.should(f.phrase().field("title").matching(q));
                searchBool.should(f.phrase().field("description").matching(q));
                searchBool.should(f.phrase().field("tags").matching(q));
                
                bool.must(searchBool);
            }
            
            // Category filter
            if (category != null && !category.isBlank() && !"all".equalsIgnoreCase(category)) {
                bool.filter(f.match().field("productCategoryId").matching(category));
            }
            
            // Vendor filter
            if (vendor != null && !vendor.isBlank()) {
                bool.filter(f.match().field("productVendorId").matching(vendor));
            }
            
            // Price range filters
            if (minPrice != null) {
                bool.filter(f.range().field("price").atLeast(minPrice));
            }
            if (maxPrice != null) {
                bool.filter(f.range().field("price").atMost(maxPrice));
            }
            
            return bool;
        });
    }
    
    /**
     * Applies sorting to the search query
     */
    private SearchQueryOptionsStep<?, ?, ?, ?, ?> applySorting(SearchQueryOptionsStep<?, ?, ?, ?, ?> query, Sort sort) {
        try {
            return query.sort((SearchSortFactory f) -> {
                CompositeSortComponentsStep<?> composite = f.composite();
                sort.forEach((Sort.Order order) -> {
                    String sortField = getSortableFieldName(order.getProperty());
                    if (sortField != null) {
                        if (order.isAscending()) {
                            composite.add(f.field(sortField).asc());
                        } else {
                            composite.add(f.field(sortField).desc());
                        }
                    }
                });
                return composite;
            });
        } catch (Exception e) {
            log.warn("Error applying sorting, continuing without sort", e);
            return query;
        }
    }
    
    /**
     * Maps property names to sortable field names
     */
    private String getSortableFieldName(String property) {
        switch (property) {
            case "title":
                return "title_sort";
            case "price":
            case "averageRating":
            case "reviewCount":
                return property;
            default:
                log.warn("Field '{}' is not configured for sorting, skipping", property);
                return null;
        }
    }
    
    /**
     * Converts Product entities to ProductDTOs with images
     */
    private List<ProductDTO> convertProductsToDTOs(List<Product> products) {
        return products.stream()
            .map(this::convertProductToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Converts a single Product entity to ProductDTO
     */
    private ProductDTO convertProductToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setHandle(product.getHandle());
        dto.setDescription(product.getDescription());
        dto.setTitle(product.getTitle());
        dto.setPrice(product.getPrice());
        dto.setPublishedScope(product.getPublishedScope());
        dto.setPurchaseCount(product.getPurchaseCount());
        dto.setAverageRating(product.getAverageRating());
        dto.setReviewCount(product.getReviewCount());
        dto.setTags(product.getTags());
        dto.setSoleQuantity(product.getSoleQuantity());
        dto.setNotAllowPromotion(product.getNotAllowPromotion());
        dto.setAvailable(product.getAvailable());
        dto.setImages(productImageRepository.getByProductId(product.getId(), FILE_UPLOAD_URL));
        return dto;
    }
}

