package com.example.sandbox_vnpay_maven.order.repository;

import com.example.sandbox_vnpay_maven.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
