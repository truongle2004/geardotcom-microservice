package web_ecommerce.sale_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.core.enums.OrderStatus;
import web_ecommerce.sale_service.dto.OrderDto;
import web_ecommerce.sale_service.dto.OrderRequestDto;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Response<?> createOrder(OrderRequestDto orderRequestDto, String userId);
    
    Optional<OrderDto> getOrderById(String orderId, String userId);
    
    Page<OrderDto> getOrderHistory(String userId, Pageable pageable);
    
    Page<OrderDto> getOrdersByStatus(String userId, OrderStatus status, Pageable pageable);
    
    List<OrderDto> getUserOrders(String userId);
    
    void updateOrderStatus(String orderId, OrderStatus newStatus);
    
    void cancelOrder(String orderId, String userId);
}
