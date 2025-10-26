package web_ecommerce.sale_service.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.core.enums.ResponseMessage;
import web_ecommerce.sale_service.dto.AddToWishlistDto;
import web_ecommerce.sale_service.dto.CreateWishlistDto;
import web_ecommerce.sale_service.dto.WishlistDto;
import web_ecommerce.sale_service.dto.WishlistItemDto;
import web_ecommerce.sale_service.enitty.Wishlist;
import web_ecommerce.sale_service.enitty.WishlistItem;
import web_ecommerce.sale_service.repository.ProductRepository;
import web_ecommerce.sale_service.repository.WishlistItemRepository;
import web_ecommerce.sale_service.repository.WishlistRepository;
import web_ecommerce.sale_service.service.WishlistService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;


    @Override
    public Wishlist createWishlist(CreateWishlistDto createWishlistDto) {
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(createWishlistDto.getUserId());

        return wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WishlistDto> getWishlistById(String id) {
        return wishlistRepository.findByIdAsDto(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistDto> getWishlistsByUserId(String userId) {
        return wishlistRepository.findByUserIdAsDto(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WishlistDto> getDefaultWishlistByUserId(String userId) {
       /* Optional<WishlistDto> defaultWishlist = wishlistRepository.findDefaultByUserIdAsDto(userId);

        // If no default wishlist exists, create one
        if (defaultWishlist.isEmpty()) {
            CreateWishlistDto createDto = new CreateWishlistDto();
            createDto.setUserId(userId);
            createDto.setName("My Wishlist");
            createDto.setIsDefault(true);
            createDto.setIsPublic(false);

            WishlistDto newWishlist = createWishlist(createDto);
            return Optional.of(newWishlist);
        }

        return defaultWishlist;*/
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WishlistItemDto> getWishlistItems(String wishlistId, Pageable pageable) {
        // Verify wishlist exists
        if (!wishlistRepository.existsById(wishlistId)) {
            throw new RuntimeException("Wishlist not found with id: " + wishlistId);
        }

        return wishlistItemRepository.findByWishlistIdAsDto(wishlistId, pageable);
    }

    @Override
    public WishlistDto updateWishlist(String id, CreateWishlistDto updateWishlistDto) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + id));

        // Verify user owns this wishlist
        if (!wishlist.getUserId().equals(updateWishlistDto.getUserId())) {
//            throw new RuntimeException("User does not have permission to update this wishlist");
        }

        mapCreateDtoToEntity(updateWishlistDto, wishlist);
        Wishlist updatedWishlist = wishlistRepository.save(wishlist);
        return mapEntityToDto(updatedWishlist);
    }

    @Override
    public void deleteWishlist(String id) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + id));

        // Don't allow deletion if it's the only wishlist for the user

        // Delete all items in the wishlist first
        wishlistItemRepository.deleteByWishlistId(id);

        // Delete the wishlist
        wishlistRepository.delete(wishlist);
    }


    @Override
    public Response<?> addProductToWishlist(AddToWishlistDto addToWishlistDto, String userId) {
        // Verify wishlist exists
        try {
            String wishlistId;
            Optional<Wishlist> wishlist = wishlistRepository.findByUserId(userId);
            if (wishlist.isEmpty()) {
                CreateWishlistDto createWishlistDto = new CreateWishlistDto();
                createWishlistDto.setUserId(userId);
                wishlistId = createWishlist(createWishlistDto).getId();
            } else {
                wishlistId = wishlist.get().getId();
                if (wishlistItemRepository.existsByWishlistIdAndProductId(
                        wishlistId, addToWishlistDto.getProductId())) {
                    return new Response<>().withDataAndStatus(ResponseMessage.WISHLIST_ITEM_ALREADY_EXISTS.getMessage(), HttpStatus.CONFLICT);
                }
            }

            WishlistItem wishlistItem = new WishlistItem();
            wishlistItem.setWishlistId(wishlistId);
            wishlistItem.setProductId(addToWishlistDto.getProductId());
            wishlistItemRepository.save(wishlistItem);
            return new Response<>().withDataAndStatus(ResponseMessage.WISHLIST_ITEM_ADDED_SUCCESS.getMessage(), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeProductFromWishlist(String wishlistId, String productId) {
        // Verify wishlist exists
        if (!wishlistRepository.existsById(wishlistId)) {
            throw new RuntimeException("Wishlist not found with id: " + wishlistId);
        }

        // Check if product is in wishlist
        if (!wishlistItemRepository.existsByWishlistIdAndProductId(wishlistId, productId)) {
            throw new RuntimeException("Product is not in the wishlist");
        }

        wishlistItemRepository.deleteByWishlistIdAndProductId(wishlistId, productId);
    }

    @Override
    public void clearWishlist(String wishlistId) {
        // Verify wishlist exists
        if (!wishlistRepository.existsById(wishlistId)) {
            throw new RuntimeException("Wishlist not found with id: " + wishlistId);
        }

        wishlistItemRepository.deleteByWishlistId(wishlistId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isProductInWishlist(String wishlistId, String productId) {
        return wishlistItemRepository.existsByWishlistIdAndProductId(wishlistId, productId);
    }

    @Override
    public void setDefaultWishlist(String wishlistId, String userId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + wishlistId));

        // Verify user owns this wishlist
        if (!wishlist.getUserId().equals(userId)) {
            throw new RuntimeException("User does not have permission to modify this wishlist");
        }

        wishlistRepository.save(wishlist);
    }


    private void mapCreateDtoToEntity(CreateWishlistDto dto, Wishlist entity) {
        entity.setUserId(dto.getUserId());
    }

    private WishlistDto mapEntityToDto(Wishlist entity) {
        return new WishlistDto(
                entity.getId(),
                entity.getUserId(),
                null // Items will be loaded separately when needed
        );
    }

    private WishlistItemDto mapWishlistItemEntityToDto(WishlistItem entity) {
        return new WishlistItemDto(
                entity.getId(),
                entity.getWishlistId(),
                entity.getProductId(),
                "Unknown Product", // Would need to join with Product table to get actual data
                "unknown-handle",
                java.math.BigDecimal.ZERO,
                true
        );
    }
}
