package com.example.sandbox_vnpay_maven.order.controller;

import com.example.sandbox_vnpay_maven.order.model.Order;
import com.example.sandbox_vnpay_maven.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {

    private final OrderService orderService;

    public Controller(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("home")
    public String homePage(Model model,
                           @RequestParam(defaultValue = "0") Integer page,
                           @RequestParam(defaultValue = "20") Integer pageSize) {
        Page<Order> pageResult = orderService.getOrdersPagination(page, pageSize);
        int total = pageResult.getTotalPages();
        int currentPage = pageResult.getNumber();
        List<Order> orders = pageResult.getContent();
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", total);
        model.addAttribute("orders", orders);
        return "index";
    }

    @GetMapping("create")
    public String createPage(Model model) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String timeFormatted = currentTime.format(formatter);

        Order order = new Order();
        model.addAttribute("currentTime", timeFormatted);
        model.addAttribute("order", order);
        return "create_order";
    }

//    @PostMapping("create")
//    public String createOrder(@ModelAttribute Order order,
//                              HttpServletRequest request) {
//        String ipAddr = request.getRemoteAddr();
//        orderService.createOrder(order, ipAddr);
//        return "redirect:/home";
//    }

    @PostMapping("create")
    public String createOrder(HttpServletRequest request) {
        String ipAddr = request.getRemoteAddr();
        orderService.createOrder(order, ipAddr);
        return "redirect:/home";
    }

    @GetMapping("order/{id}")
    public String orderDetail(Model model,
                              @PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "orderdetail";
    }
}
