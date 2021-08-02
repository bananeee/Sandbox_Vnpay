package com.example.sandbox_vnpay.order.repository;

import com.example.sandbox_vnpay.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
