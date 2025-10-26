# Vietnamese Text Search Implementation with Hibernate Search and Lucene

## Overview

This implementation provides enhanced Vietnamese text search capabilities for the e-commerce platform using Hibernate Search with Lucene backend. The solution addresses the unique challenges of Vietnamese text processing, including diacritics handling, text normalization, and language-specific search patterns.

## Key Features

### 1. Vietnamese Text Analysis
- **Custom Vietnamese Analyzer**: Handles Vietnamese diacritics and common stop words
- **Text Normalization**: Removes diacritics for better matching (e.g., "điện thoại" matches "dien thoai")
- **Multiple Search Strategies**: Combines fuzzy search, wildcard search, and phrase search
- **Stop Words Filtering**: Filters out common Vietnamese words like "và", "của", "cho", etc.

### 2. Search Capabilities
- **Full-text Search**: Search across product titles, descriptions, and tags
- **Fuzzy Matching**: Handles typos and variations in Vietnamese text
- **Field Boosting**: Prioritizes title matches over description matches
- **Autocomplete Support**: Provides search suggestions for better UX

### 3. Performance Optimizations
- **Caching**: Redis-based caching for search results and suggestions
- **Indexing**: Automatic index rebuilding on application startup
- **Pagination**: Efficient pagination for large result sets
- **Concurrent Processing**: Multi-threaded indexing for better performance

## Architecture Components

### 1. VietnameseSearchConfig
```java
@Configuration
public class VietnameseSearchConfig implements LuceneAnalysisConfigurer
```
- Configures Vietnamese-specific analyzers
- Defines stop words for Vietnamese text
- Sets up different analyzers for different use cases

### 2. VietnameseSearchService
```java
public interface VietnameseSearchService
```
- Defines search operations with Vietnamese text support
- Provides normalized search capabilities
- Includes autocomplete functionality

### 3. VietnameseSearchServiceImpl
```java
@Service
public class VietnameseSearchServiceImpl implements VietnameseSearchService
```
- Implements Vietnamese text search logic
- Handles text normalization and diacritics removal
- Provides caching for performance optimization

## API Endpoints

### 1. Vietnamese Text Search
```
GET /api/v1/sale/products/search-vietnamese
```
- **Parameters**: 
  - `q`: Search query (Vietnamese text)
  - `category`: Product category filter
  - `vendor`: Vendor filter
  - `min`, `max`: Price range filters
  - `page`, `size`: Pagination parameters
- **Features**: Full Vietnamese text support with diacritics handling

### 2. Normalized Search
```
GET /api/v1/sale/products/search-normalized
```
- **Parameters**: Same as above
- **Features**: Search with Vietnamese text normalization (diacritics removal)

### 3. Search Suggestions
```
GET /api/v1/sale/products/search-suggestions
```
- **Parameters**:
  - `q`: Partial search query
  - `limit`: Maximum number of suggestions
- **Features**: Autocomplete suggestions for Vietnamese text

## Configuration

### 1. Application Properties
```yaml
spring:
  jpa:
    properties:
      hibernate:
        search:
          backend:
            type: lucene
            directory:
              root: ${java.io.tmpdir}/geardotcom/lucene-indexes
            lucene:
              analysis:
                configurer: web_ecommerce.sale_service.config.VietnameseSearchConfig
              indexing:
                queue_count: 4
                queue_size: 1000
                max_batch_size: 100
```

### 2. Entity Configuration
```java
@Entity
@Indexed
public class Product extends BaseEntityNonId {
    @FullTextField(analyzer = "vietnamese")
    private String title;
    
    @FullTextField(analyzer = "vietnamese")
    private String description;
    
    @FullTextField(analyzer = "vietnamese")
    private String tags;
}
```

## Vietnamese Text Processing

### 1. Diacritics Handling
The system handles Vietnamese diacritics through:
- **ASCIIFoldingFilter**: Converts accented characters to base characters
- **Custom Normalization**: Additional Vietnamese-specific character mapping
- **Dual Search**: Searches both original and normalized text

### 2. Stop Words
Common Vietnamese stop words are filtered out:
```java
private static final CharArraySet VIETNAMESE_STOP_WORDS = new CharArraySet(
    Arrays.asList("và", "của", "cho", "với", "từ", "đến", "trong", ...), true
);
```

### 3. Text Normalization
```java
private String normalizeVietnameseText(String text) {
    // Normalize Unicode characters
    String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
    
    // Remove diacritics
    normalized = normalized.replaceAll("\\p{M}", "");
    
    // Convert to lowercase and handle Vietnamese-specific characters
    normalized = normalized.toLowerCase().trim();
    normalized = normalized.replace("đ", "d");
    
    return normalized;
}
```

## Search Strategies

### 1. Multi-Strategy Search
The implementation uses multiple search strategies simultaneously:
- **Simple Query String**: Primary search with field boosting
- **Fuzzy Search**: Handles typos and variations
- **Wildcard Search**: Partial matches
- **Phrase Search**: Exact phrase matching
- **Normalized Search**: Search without diacritics

### 2. Field Boosting
Different fields have different importance weights:
- **Title**: Weight 4 (highest priority)
- **Tags**: Weight 3
- **Description**: Weight 2

### 3. Query Building
```java
private SearchQueryOptionsStep<?, ?, ?, ?, ?> buildVietnameseSearchQuery(
    SearchSession searchSession, String query, String category, String vendor, 
    BigDecimal minPrice, BigDecimal maxPrice) {
    
    return searchSession.search(Product.class).where((SearchPredicateFactory f) -> {
        BooleanPredicateClausesStep<?> bool = f.bool();
        
        if (query != null && !query.isBlank()) {
            var searchBool = f.bool();
            
            // Primary search with Vietnamese analyzer
            searchBool.should(f.simpleQueryString()
                .fields("title^4", "description^2", "tags^3")
                .matching(query)
                .defaultOperator(BooleanOperator.OR));
            
            // Additional search strategies...
            bool.must(searchBool);
        }
        
        // Apply filters...
        return bool;
    });
}
```

## Performance Considerations

### 1. Caching Strategy
- **Search Results**: Cached for 30 minutes
- **Suggestions**: Cached for 15 minutes
- **Fallback**: In-memory cache if Redis unavailable

### 2. Indexing Configuration
- **Queue Count**: 4 concurrent indexing threads
- **Queue Size**: 1000 items per queue
- **Batch Size**: 100 items per batch

### 3. Memory Management
- **Index Location**: Temporary directory for development
- **Production**: Should use persistent storage
- **Cleanup**: Automatic index cleanup on application shutdown

## Usage Examples

### 1. Basic Vietnamese Search
```bash
curl "http://localhost:8081/api/v1/sale/products/search-vietnamese?q=điện%20thoại&page=0&size=10"
```

### 2. Search with Filters
```bash
curl "http://localhost:8081/api/v1/sale/products/search-vietnamese?q=laptop&category=electronics&min=1000000&max=5000000"
```

### 3. Get Search Suggestions
```bash
curl "http://localhost:8081/api/v1/sale/products/search-suggestions?q=điện&limit=5"
```

## Testing Vietnamese Search

### 1. Test Cases
- Search with diacritics: "điện thoại"
- Search without diacritics: "dien thoai"
- Mixed case: "Điện Thoại"
- Partial matches: "điện"
- Typos: "điện thoai" (missing 'i')

### 2. Expected Behavior
- All variations should return similar results
- Diacritics should be handled gracefully
- Partial matches should work
- Stop words should be filtered appropriately

## Monitoring and Maintenance

### 1. Index Health
- Monitor index size and performance
- Check indexing queue status
- Verify search response times

### 2. Search Analytics
- Track popular search terms
- Monitor search success rates
- Analyze user search patterns

### 3. Performance Metrics
- Search response time
- Index rebuild time
- Cache hit rates
- Memory usage

## Future Enhancements

### 1. Advanced Features
- **Synonym Support**: Handle Vietnamese synonyms
- **Stemming**: Vietnamese word stemming
- **Phonetic Search**: Sound-based matching
- **Auto-correction**: Suggest corrections for typos

### 2. Machine Learning Integration
- **Search Ranking**: ML-based result ranking
- **Personalization**: User-specific search results
- **Recommendations**: Search-based product recommendations

### 3. Multi-language Support
- **English Search**: Support for English product names
- **Mixed Language**: Search in both Vietnamese and English
- **Language Detection**: Automatic language detection

## Troubleshooting

### 1. Common Issues
- **Index Not Found**: Rebuild indexes on startup
- **Slow Search**: Check index configuration and caching
- **Memory Issues**: Adjust indexing queue sizes
- **Encoding Problems**: Ensure UTF-8 encoding throughout

### 2. Debug Configuration
```yaml
logging:
  level:
    org.hibernate.search: DEBUG
    web_ecommerce.sale_service.service.impl.VietnameseSearchServiceImpl: DEBUG
```

This implementation provides a robust foundation for Vietnamese text search in your e-commerce platform, with room for future enhancements and optimizations.
