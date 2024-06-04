package com.synergy.binarfood.service;

import com.synergy.binarfood.entity.Order;
import com.synergy.binarfood.entity.OrderDetail;
import com.synergy.binarfood.entity.Product;
import com.synergy.binarfood.model.order.DeleteOrderDetailRequest;
import com.synergy.binarfood.model.order.OrderDetailRequest;
import com.synergy.binarfood.model.order.OrderDetailResponse;
import com.synergy.binarfood.model.order.UpdateOrderDetailRequest;
import com.synergy.binarfood.repository.OrderDetailRepository;
import com.synergy.binarfood.repository.OrderRepository;
import com.synergy.binarfood.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    public final OrderRepository orderRepository;
    public final OrderDetailRepository orderDetailRepository;
    public final ProductRepository productRepository;
    public final ValidationService validationService;

    @Transactional
    public OrderDetailResponse create(OrderDetailRequest request) {
        this.validationService.validate(request);

        Order order = this.orderRepository.findByIdAndUser_Email(request.getOrderId(), request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order doesn't exists"));
        if (order.getCompletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "order already finished");
        }

        Product product = this.productRepository.findById(UUID.fromString(request.getProductId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product doesn't exists"));
        if (!product.getMerchant().isOpen()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product unavailable because merchant is closed");
        }
        if (this.orderDetailRepository.existsByOrderAndProduct(order, product)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "order already exists, please update instead");
        }

        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .quantity(request.getQuantity())
                .totalPrice(product.getPrice() * request.getQuantity())
                .build();
        this.orderDetailRepository.save(orderDetail);

        return OrderDetailResponse.builder()
                .orderId(orderDetail.getOrder().getId().toString())
                .orderDetailId(orderDetail.getId().toString())
                .merchantId(orderDetail.getProduct().getMerchant().getId().toString())
                .merchantName(orderDetail.getProduct().getMerchant().getName())
                .productId(orderDetail.getProduct().getId().toString())
                .productName(orderDetail.getProduct().getName())
                .quantity(orderDetail.getQuantity())
                .totalPrice(orderDetail.getTotalPrice())
                .build();
    }

    @Transactional
    public OrderDetailResponse update(UUID orderDetailId, UpdateOrderDetailRequest request) {
        this.validationService.validate(request);
        OrderDetail orderDetail = this.orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order doesn't exists"));
        if (!Objects.equals(orderDetail.getOrder().getId().toString(), request.getOrderId().toString())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order doesn't exists");
        }
        if (!Objects.equals(orderDetail.getOrder().getUser().getEmail(), request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order doesn't exists");
        }
        if (orderDetail.getOrder().getCompletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "order already finished");
        }
        if (orderDetail.getProduct() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "product doesn't exists");
        }
        if (!orderDetail.getProduct().getMerchant().isOpen()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "product unavailable because merchant is closed");
        }

        orderDetail.setQuantity(request.getQuantity());
        this.orderDetailRepository.save(orderDetail);

        return OrderDetailResponse.builder()
                .orderId(orderDetail.getOrder().getId().toString())
                .orderDetailId(orderDetail.getId().toString())
                .merchantId(orderDetail.getProduct().getMerchant().getId().toString())
                .merchantName(orderDetail.getProduct().getMerchant().getName())
                .productId(orderDetail.getProduct().getId().toString())
                .productName(orderDetail.getProduct().getName())
                .quantity(orderDetail.getQuantity())
                .totalPrice(orderDetail.getTotalPrice())
                .build();
    }

    @Transactional
    public void delete(UUID orderDetailId, DeleteOrderDetailRequest request) {
        OrderDetail orderDetail = this.orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order doesn't exists"));
        if (!Objects.equals(orderDetail.getOrder().getId().toString(), request.getOrderId().toString())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order doesn't exists");
        }
        if (!Objects.equals(orderDetail.getOrder().getUser().getEmail(), request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order doesn't exists");
        }
        if (orderDetail.getOrder().getCompletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "order already finished");
        }

        this.orderDetailRepository.delete(orderDetail);
    }
}
