package com.example.sandbox_vnpay.order.service;

import com.example.sandbox_vnpay.order.model.Order;
import com.example.sandbox_vnpay.order.util.Status;
import com.example.sandbox_vnpay.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Page<Order> getOrdersPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        return orderRepository.findAll(pageable);
    }

    public Order getOrderById(Long orderId) {
        log.info("Get order " + orderId);
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty())
            throw new EntityNotFoundException("Order " + orderId + " not found");

        return order.get();
    }

    @Transactional
    public void updateOrderStatus(Long orderId, Status status) {
        log.info("Update order " + orderId + " with status " + status);
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty())
            throw new EntityNotFoundException("Order " + orderId + " not found");

        order.get().setOrderStatus(status);

    }

    public void createOrder(Order newOrder, String ipAddr) {
//        log.info("User IP: " + ipAddr);
//        Optional<Order> order = orderRepository.findById(newOrder.getId());
//        if (order.isPresent())
//            throw new EntityExistsException();
        LocalDateTime orderDate = LocalDateTime.now();
        newOrder.setIpAddress(ipAddr);
        newOrder.setOrderDate(orderDate);
        orderRepository.save(newOrder);
    }

    public void deleteOrder(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty())
            throw new EntityNotFoundException("Order " + orderId + " not found");

        log.info("Delete order " + order.toString());
        orderRepository.delete(order.get());
    }

}
