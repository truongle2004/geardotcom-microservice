package web_ecommerce.sale_service.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import web_ecommerce.core.controller.BaseController;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.sale_service.dto.CreateProductCategoryDto;
import web_ecommerce.sale_service.dto.ProductCategoryDto;
import web_ecommerce.sale_service.service.ProductCategoryService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Api(tags = "Admin - Category Management")
public class AdminCategoryController extends BaseController {
    private static final String root = "/categories";
    private final ProductCategoryService categoryService;

    @ApiOperation(value = "Create new category")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(V1 + root)
    public Response<ProductCategoryDto> createCategory(@RequestBody CreateProductCategoryDto createDto) {
        ProductCategoryDto category = categoryService.createCategory(createDto);
        return new Response<ProductCategoryDto>().withDataAndStatus(category, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root)
    public Response<Page<ProductCategoryDto>> getAllCategories(
            @PageableDefault(sort = "sortOrder", direction = Sort.Direction.ASC, page = 0, size = 20) Pageable pageable) {
        Page<ProductCategoryDto> categories = categoryService.getAllCategories(pageable);
        return new Response<Page<ProductCategoryDto>>().withDataAndStatus(categories, HttpStatus.OK);
    }

    @ApiOperation(value = "Get featured categories")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/featured")
    public Response<List<ProductCategoryDto>> getFeaturedCategories() {
        List<ProductCategoryDto> categories = categoryService.getFeaturedCategories();
        return new Response<List<ProductCategoryDto>>().withDataAndStatus(categories, HttpStatus.OK);
    }

    @ApiOperation(value = "Get category by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/{id}")
    public Response<ProductCategoryDto> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id)
                .map(category -> new Response<ProductCategoryDto>().withDataAndStatus(category, HttpStatus.OK))
                .orElse(new Response<ProductCategoryDto>().withDataAndStatus(null, HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "Update category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping(V1 + root + "/{id}")
    public Response<ProductCategoryDto> updateCategory(@PathVariable String id, @RequestBody CreateProductCategoryDto updateDto) {
        ProductCategoryDto updated = categoryService.updateCategory(id, updateDto);
        return new Response<ProductCategoryDto>().withDataAndStatus(updated, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete category")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @DeleteMapping(V1 + root + "/{id}")
    public Response<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.NO_CONTENT);
    }
}

