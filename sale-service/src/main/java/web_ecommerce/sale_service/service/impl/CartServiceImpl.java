package web_ecommerce.sale_service.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.core.enums.ResponseMessage;
import web_ecommerce.core.utils.StringUtils;
import web_ecommerce.sale_service.dto.CartItemDTO;
import web_ecommerce.sale_service.dto.CartItemResponse;
import web_ecommerce.sale_service.enitty.Cart;
import web_ecommerce.sale_service.enitty.CartItem;
import web_ecommerce.sale_service.repository.CartItemRepository;
import web_ecommerce.sale_service.repository.CartRepository;
import web_ecommerce.sale_service.service.CartService;

import java.util.List;
import java.util.Objects;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Value("${file_upload-url}")
    private String FILE_UPLOAD_URL;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public void clearMultipleCart(String userId, List<String> productIdsToRemove) {
    }

    @Override
    public Response<?> getAllCartItemInfo(String userId, Pageable pageable) {
        return new Response<Page<List<CartItemResponse>>>().withDataAndStatus(cartItemRepository.getAllCartItemByUserId(userId, FILE_UPLOAD_URL, pageable), HttpStatus.OK);
    }

    @Override
    public void clearAllCartItem(String userId) {
        cartItemRepository.deleteAllByUserId(userId);
    }

    public void createNewCart(Cart cart) {
        cartRepository.save(cart);
    }

    @Override
    public Response<String> addItemToCart(String userId, CartItemDTO newItem) {
        if (newItem.getProductId() == null || newItem.getProductId().isEmpty()) {
            return new Response<String>().withDataAndStatus(ResponseMessage.PRODUCT_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }

        String cartId = cartRepository.getByUserId(userId);

        // If cart doesn't exist, create it
        if (StringUtils.isNotNullOrEmpty(cartId)) {
            Cart cart = new Cart();
            cart.setUserId(userId);
            Cart savedCart = cartRepository.save(cart);
            cartId = savedCart.getId();
        } else {
            // Check if item already exists in cart
            CartItem isExist = cartItemRepository.findByCartIdAndProductId(cartId, newItem.getProductId());
            if(isExist != null) {
                return new Response<String>().withDataAndStatus(ResponseMessage.CART_ITEM_ALREADY_EXISTS.getMessage(), HttpStatus.CONFLICT);
            }
        }

        // Add item to cart
        CartItem cartItem = new CartItem();
        cartItem.setCartId(cartId);
        cartItem.setProductId(newItem.getProductId());
        cartItem.setQuantity(newItem.getQuantity());
        cartItemRepository.save(cartItem);
        
        return new Response<String>().withDataAndStatus("Đã thêm sản phẩm vào giỏ hàng", HttpStatus.OK);
    }

    @Override
    public void removeItemFromCart(List<String> ids) {
        cartItemRepository.deleteAllById(ids);
    }
}