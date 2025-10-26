package web_ecommerce.sale_service.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import web_ecommerce.core.controller.BaseController;
import web_ecommerce.core.dto.response.Response;
import web_ecommerce.core.enums.OrderStatus;
import web_ecommerce.core.enums.ResponseMessage;
import web_ecommerce.core.utils.StringUtils;
import web_ecommerce.sale_service.dto.OrderDto;
import web_ecommerce.sale_service.dto.OrderRequestDto;
import web_ecommerce.sale_service.service.OrderService;

@RestController
@RequiredArgsConstructor
public class OrderController extends BaseController {
    private final static String root = "/sale/orders";
    private final OrderService orderService;

    @ApiOperation(value = "Create order")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(V1 + root + "/create-order")
    public Response<?> createOrder(HttpServletRequest request, @RequestBody OrderRequestDto orderRequestDto) {
        String userId = getUserId(request);
        return orderService.createOrder(orderRequestDto, userId);
    }

    @ApiOperation(value = "Get order history")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/history")
    public Response<?> getOrderHistory(
            HttpServletRequest request,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        String userId = getUserId(request);
        if (StringUtils.isNotNullOrEmpty(userId)) {
            return new Response<>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        Page<OrderDto> orders = orderService.getOrderHistory(userId, pageable);
        return new Response<Page<OrderDto>>().withDataAndStatus(orders, HttpStatus.OK);
    }

    @ApiOperation(value = "Get order by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/{orderId}")
    public Response<?> getOrderById(HttpServletRequest request, @PathVariable String orderId) {
        String userId = getUserId(request);
        if (StringUtils.isNotNullOrEmpty(userId)) {
            return new Response<>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        return orderService.getOrderById(orderId, userId)
                .map(order -> new Response<OrderDto>().withDataAndStatus(order, HttpStatus.OK))
                .orElse(new Response<OrderDto>().withDataAndStatus(null, HttpStatus.NOT_FOUND));
    }

    @ApiOperation(value = "Cancel order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PatchMapping(V1 + root + "/{orderId}/cancel")
    public Response<?> cancelOrder(HttpServletRequest request, @PathVariable String orderId) {
        String userId = getUserId(request);
        if (StringUtils.isNotNullOrEmpty(userId)) {
            return new Response<>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        orderService.cancelOrder(orderId, userId);
        return new Response<Void>().withDataAndStatus(null, HttpStatus.OK);
    }

    @ApiOperation(value = "Get orders by status")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(V1 + root + "/status/{status}")
    public Response<?> getOrdersByStatus(
            HttpServletRequest request,
            @PathVariable OrderStatus status,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, page = 0, size = 20) Pageable pageable) {
        String userId = getUserId(request);
        if (StringUtils.isNotNullOrEmpty(userId)) {
            return new Response<>().withDataAndStatus(ResponseMessage.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        Page<OrderDto> orders = orderService.getOrdersByStatus(userId, status, pageable);
        return new Response<Page<OrderDto>>().withDataAndStatus(orders, HttpStatus.OK);
    }
}
