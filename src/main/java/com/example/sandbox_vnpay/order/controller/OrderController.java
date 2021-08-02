package com.example.sandbox_vnpay.order.controller;

import com.example.sandbox_vnpay.order.model.Order;
import com.example.sandbox_vnpay.order.Utils.Status;
import com.example.sandbox_vnpay.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping(path = "{orderId}")
    public Order getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @PatchMapping(path = "{orderId}/{status}")
    public void updateOrderStatus(@PathVariable Long orderId,
                                  @PathVariable String status) {
        Boolean statusExist = false;
        for (Status s : Status.values()) {
            System.out.println(s.name());
            if (s.name().equalsIgnoreCase(status)) {
                statusExist = true;
                orderService.updateOrderStatus(orderId, s);
            }
        }
        if (!statusExist)
            throw new RuntimeException("Status " + status + " does not exist");
    }

    @PostMapping
    public void createOrder(@RequestBody Order newOrder,
                            HttpServletRequest request) {
//        String ipAddr = request.getHeader("X-FORWARDED-FOR");
        String ipAddr = request.getRemoteAddr();
        orderService.createOrder(newOrder, ipAddr);
    }

    @DeleteMapping(path = "{orderId}")
    public void deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
    }
}
