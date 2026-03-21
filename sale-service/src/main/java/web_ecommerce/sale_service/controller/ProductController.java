package web_ecommerce.sale_service.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import web_ecommerce.core.controller.BaseController;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.sale_service.dto.CategoryDTO;
import web_ecommerce.sale_service.dto.ProductDTO;
import web_ecommerce.sale_service.dto.VendorDTO;
import web_ecommerce.sale_service.service.ProductService;
//import web_ecommerce.sale_service.service.VietnameseSearchService;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class ProductController extends BaseController {
    private static final String root = "/sale/products";
    private final ProductService productService;
//    private final VietnameseSearchService vietnameseSearchService;
    
    @Value("${file_upload-dir}")
    private String imageUploadDir;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation(value = "API get list product")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = V1 + root)
    public Response<Page<ProductDTO>> getListProduct(
            Pageable pageable,
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "") String vendor,
            @RequestParam(required = false) BigDecimal min,
            @RequestParam(required = false) BigDecimal max
    ) {
        return productService.getListProductByCategory(pageable, category, vendor, min, max);
    }

    @ApiOperation(value = "API search products (Hibernate Search)")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = V1 + root + "/search")
    public Response<Page<ProductDTO>> searchProducts(
            Pageable pageable,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "") String vendor,
            @RequestParam(required = false, name = "min") BigDecimal min,
            @RequestParam(required = false, name = "max") BigDecimal max
    ) {
        return productService.searchProducts(pageable, query, category, vendor, min, max);
    }

    @ApiOperation(value = "API get product detail")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = V1 + root + "/{id}")
    public Response<ProductDTO> getProductDetail(
            @PathVariable(value = "id") String id
    ) {
        return productService.getById(id);
    }

    @ApiOperation(value = "API get list category")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = V1 + root + "/categories")
    public Response<List<CategoryDTO>> getListCategory() {
        return productService.getAllProductCategory();
    }

    @ApiOperation(value = "API get image")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = V1 + root + "/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get(imageUploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(imagePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(imagePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "API get vendor")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = V1 + root + "/vendors")
    public Response<List<VendorDTO>> getVendor() {
        return productService.getAllVendor();
    }

    @ApiOperation(value = "API get best sellers")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = V1 + root + "/best-sellers")
    public Response<Page<ProductDTO>> getBestSellers(Pageable pageable) {
        return productService.getBestSellers(pageable);
    }

    @ApiOperation(value = "API get featured products")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = V1 + root + "/featured")
    public Response<Page<ProductDTO>> getFeaturedProducts(Pageable pageable) {
        return productService.getFeaturedProducts(pageable);
    }

    @ApiOperation(value = "API get top rated products")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
    @GetMapping(value = V1 + root + "/top-rated")
    public Response<Page<ProductDTO>> getTopRatedProducts(Pageable pageable) {
        return productService.getTopRatedProducts(pageable);
    }

//    @ApiOperation(value = "API search products with Vietnamese text support")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
//            @ApiResponse(code = 400, message = "Bad request"),
//            @ApiResponse(code = 500, message = "Internal server error")}
//    )
//    @GetMapping(value = V1 + root + "/search-vietnamese")
//    public Response<Page<ProductDTO>> searchProductsVietnamese(
//            Pageable pageable,
//            @RequestParam(required = false, name = "q") String query,
//            @RequestParam(defaultValue = "all") String category,
//            @RequestParam(defaultValue = "") String vendor,
//            @RequestParam(required = false, name = "min") BigDecimal min,
//            @RequestParam(required = false, name = "max") BigDecimal max
//    ) {
//        return vietnameseSearchService.searchProductsVietnamese(pageable, query, category, vendor, min, max);
//    }

//    @ApiOperation(value = "API search products with Vietnamese text normalization")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
//            @ApiResponse(code = 400, message = "Bad request"),
//            @ApiResponse(code = 500, message = "Internal server error")}
//    )
//    @GetMapping(value = V1 + root + "/search-normalized")
//    public Response<Page<ProductDTO>> searchProductsNormalized(
//            Pageable pageable,
//            @RequestParam(required = false, name = "q") String query,
//            @RequestParam(defaultValue = "all") String category,
//            @RequestParam(defaultValue = "") String vendor,
//            @RequestParam(required = false, name = "min") BigDecimal min,
//            @RequestParam(required = false, name = "max") BigDecimal max
//    ) {
//        return vietnameseSearchService.searchProductsNormalized(pageable, query, category, vendor, min, max);
//    }
//
//    @ApiOperation(value = "API get search suggestions for autocomplete")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
//            @ApiResponse(code = 400, message = "Bad request"),
//            @ApiResponse(code = 500, message = "Internal server error")}
//    )
//    @GetMapping(value = V1 + root + "/search-suggestions")
//    public Response<List<String>> getSearchSuggestions(
//            @RequestParam(required = false, name = "q") String query,
//            @RequestParam(defaultValue = "10") int limit
//    ) {
//        return vietnameseSearchService.getSearchSuggestions(query, limit);
//    }
}
