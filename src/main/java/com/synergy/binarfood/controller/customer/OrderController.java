package com.synergy.binarfood.controller.customer;

import com.synergy.binarfood.model.order.*;
import com.synergy.binarfood.model.web.WebResponse;
import com.synergy.binarfood.service.OrderDetailService;
import com.synergy.binarfood.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    @GetMapping(
            path = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<OrderResponse>>> getAllByCurrentUser(
            Authentication authentication,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        GetAllOrderRequest request = GetAllOrderRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .email(userDetails.getUsername())
                .build();
        Page<OrderResponse> order = this.orderService.findAllByUser(request);
        WebResponse<List<OrderResponse>> response = WebResponse
                 .<List<OrderResponse>>builder()
                .data(order.getContent())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            path = "/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<OrderResponse>> getById(
            Authentication authentication,
            @PathVariable UUID orderId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        OrderResponse order = this.orderService.findById(orderId, userDetails.getUsername());
        WebResponse<OrderResponse> response = WebResponse.<OrderResponse>builder()
                .data(order)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            path = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<OrderResponse>> create(
            Authentication authentication,
            @RequestBody OrderRequest request) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        request.setEmail(userDetails.getUsername());

        OrderResponse order = this.orderService.create(request);
        WebResponse<OrderResponse> response = WebResponse.<OrderResponse>builder()
                .data(order)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            path = "/{orderId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<OrderDetailResponse>> createOrderDetail(
            Authentication authentication,
            @PathVariable UUID orderId,
            @RequestBody OrderDetailRequest request) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        request.setOrderId(orderId);
        request.setEmail(userDetails.getUsername());

        OrderDetailResponse orderDetail = this.orderDetailService.create(request);
        WebResponse<OrderDetailResponse> response = WebResponse.<OrderDetailResponse>builder()
                .data(orderDetail)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping(
            path = "/{orderId}/details/{orderDetailId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<OrderDetailResponse>> updateOrderDetail(
            Authentication authentication,
            @PathVariable UUID orderId,
            @PathVariable UUID orderDetailId,
            @RequestBody UpdateOrderDetailRequest request) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        request.setOrderId(orderId);
        request.setEmail(userDetails.getUsername());

        OrderDetailResponse orderDetail = this.orderDetailService.update(orderDetailId, request);
        WebResponse<OrderDetailResponse> response = WebResponse.<OrderDetailResponse>builder()
                .data(orderDetail)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(
            path = "/{orderId}/details/{orderDetailId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> deleteOrderDetail(
            Authentication authentication,
            @PathVariable UUID orderId,
            @PathVariable UUID orderDetailId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        DeleteOrderDetailRequest request = DeleteOrderDetailRequest.builder()
                .orderId(orderId)
                .email(userDetails.getUsername())
                .build();

        this.orderDetailService.delete(orderDetailId, request);
        WebResponse<String> response = WebResponse.<String>builder()
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            path = "/{orderId}/checkout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<OrderResponse>> checkout(
            Authentication authentication,
            @PathVariable UUID orderId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        CheckoutOrderRequest request = CheckoutOrderRequest.builder()
                .orderId(orderId)
                .email(userDetails.getUsername())
                .build();

        OrderResponse order = this.orderService.checkout(request);
        WebResponse<OrderResponse> response = WebResponse.<OrderResponse>builder()
                .data(order)
                .build();

        return ResponseEntity.ok(response);
    }
}
