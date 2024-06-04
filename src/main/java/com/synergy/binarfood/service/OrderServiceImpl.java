package com.synergy.binarfood.service;

import com.synergy.binarfood.entity.Order;
import com.synergy.binarfood.entity.OrderDetail;
import com.synergy.binarfood.entity.User;
import com.synergy.binarfood.model.order.GetAllOrderRequest;
import com.synergy.binarfood.model.order.OrderDetailResponse;
import com.synergy.binarfood.model.order.OrderRequest;
import com.synergy.binarfood.model.order.OrderResponse;
import com.synergy.binarfood.repository.OrderDetailRepository;
import com.synergy.binarfood.repository.OrderRepository;
import com.synergy.binarfood.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ValidationService validationService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;

    @Transactional
    public Page<OrderResponse> findAllByUser(GetAllOrderRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        Page<Order> orders = this.orderRepository
                .findAllByUser_Email(request.getEmail(), pageable);

        return orders.map(new Function<Order, OrderResponse>() {
            @Override
            public OrderResponse apply(Order order) {
                return OrderResponse.builder()
                        .id(order.getId().toString())
                        .destinationAddress(order.getDestinationAddress())
                        .orderAt(order.getOrderAt())
                        .completedAt(order.getCompletedAt())
                        .totalPrice(order.getOrderDetails()
                                .stream()
                                .map(OrderDetail::getTotalPrice)
                                .reduce(0.0, Double::sum))
                        .build();
            }
        });
    }

    @Transactional
    public OrderResponse findById(UUID orderId, String userEmail) {
        Order order = this.orderRepository.findByIdAndUser_Email(orderId, userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "order doesn't exists"));
        List<OrderDetail> orderDetails = this.orderDetailRepository.findAllByOrder(order);

        List<OrderDetailResponse> orderDetailsResponse = orderDetails
                .stream()
                .map(e -> OrderDetailResponse.builder()
                        .orderDetailId(e.getId().toString())
                        .merchantId(e.getProduct().getMerchant().getId().toString())
                        .merchantName(e.getProduct().getMerchant().getName())
                        .productId(e.getProduct().getId().toString())
                        .productName(e.getProduct().getName())
                        .quantity(e.getQuantity())
                        .totalPrice(e.getProduct().getPrice() * e.getQuantity())
                        .build())
                    .toList();
        return OrderResponse.builder()
                .id(order.getId().toString())
                .destinationAddress(order.getDestinationAddress())
                .orderAt(order.getOrderAt())
                .completedAt(order.getCompletedAt())
                .orderDetails(orderDetailsResponse)
                .totalPrice(order.getOrderDetails()
                        .stream()
                        .map(OrderDetail::getTotalPrice)
                        .reduce(0.0, Double::sum))
                .build();
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        this.validationService.validate(request);

        User user = this.userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user doesn't exists"));
        Order order = Order.builder()
                .destinationAddress(request.getDestinationAddress())
                .orderAt(new Date())
                .user(user)
                .build();
        this.orderRepository.save(order);

        return OrderResponse.builder()
                .id(order.getId().toString())
                .destinationAddress(order.getDestinationAddress())
                .orderAt(order.getOrderAt())
                .build();
    }
}
