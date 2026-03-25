package web_ecommerce.sale_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import web_ecommerce.core.controller.BaseController;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.core.enums.ResponseMessage;
import web_ecommerce.core.utils.StringUtils;
import web_ecommerce.sale_service.dto.CartItemDTO;
import web_ecommerce.sale_service.service.CartService;

import java.util.List;

@RestController
public class CartController extends BaseController {
    private final String root = "/sale/carts";

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @Operation(summary = "API add product to cart")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")}
    )
    @PostMapping(value = V1 + root)
    public Response<?> addProductToCart(HttpServletRequest httpServletRequest,
                                        @RequestBody CartItemDTO cartItemDTO) {
        String userId = getUserId(httpServletRequest);
        if (StringUtils.isNotNullOrEmpty(userId))
            return new Response<String>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        return cartService.addItemToCart(userId, cartItemDTO);
    }

    @Operation(summary = "API remove product from cart")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")}
    )
    @DeleteMapping(value = V1 + root)
    public Response<String> removeProductFromCart(HttpServletRequest httpServletRequest, @RequestParam List<String> ids) {
        String userId = getUserId(httpServletRequest);
        if (StringUtils.isNotNullOrEmpty(userId))
            return new Response<String>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.FORBIDDEN);
        cartService.removeItemFromCart(ids);
        return new Response<String>().withDataAndStatus("Xóa sản phẩm thành công!", HttpStatus.OK);
    }


    @Operation(summary = "API get all cart items")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")}
    )
    @GetMapping(value = V1 + root)
    public Response<?> getAllCartItems(HttpServletRequest httpServletRequest, @PageableDefault(sort = "id", direction = Sort.Direction.DESC, page = 0, size = 10) Pageable pageable) {
        String userId = getUserId(httpServletRequest);
        if (StringUtils.isNotNullOrEmpty(userId)) {
            return new Response<>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        return cartService.getAllCartItemInfo(userId, pageable);
    }
}

