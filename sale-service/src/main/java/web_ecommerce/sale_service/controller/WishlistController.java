package web_ecommerce.sale_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import web_ecommerce.core.controller.BaseController;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.core.enums.ResponseMessage;
import web_ecommerce.core.utils.StringUtils;
import web_ecommerce.sale_service.dto.AddToWishlistDto;
import web_ecommerce.sale_service.service.WishlistService;

@RestController
@RequiredArgsConstructor
public class WishlistController extends BaseController {
    private static final String root = "/sale/wishlist";
    private final WishlistService wishlistService;

    @Operation(summary = "API get list product")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "409", description = "Conflict"),
            @ApiResponse(responseCode = "500", description = "Internal server error")}
    )
    @PostMapping(value = V1 + root)
    public Response<?> addWishlist(HttpServletRequest httpServletRequest, @RequestBody AddToWishlistDto addToWishlistDto) {
        String userId = getUserId(httpServletRequest);
        if (StringUtils.isNotNullOrEmpty(userId)) {
            return new Response<>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        return wishlistService.addProductToWishlist(addToWishlistDto, userId);
    }

    
}

