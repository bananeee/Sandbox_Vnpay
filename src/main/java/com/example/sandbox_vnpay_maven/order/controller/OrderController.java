package com.example.sandbox_vnpay_maven.order.controller;

import com.example.sandbox_vnpay_maven.order.util.Config;
import com.example.sandbox_vnpay_maven.order.model.Order;
import com.example.sandbox_vnpay_maven.order.service.OrderService;
import com.example.sandbox_vnpay_maven.order.util.Helper;
import com.example.sandbox_vnpay_maven.order.util.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/order")
public class OrderController {

    private final Logger log = LoggerFactory.getLogger(getClass());

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
        boolean statusExist = false;
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

    @GetMapping("/ipn")
    public Map<String, String> getPaymentInfo(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        String hash = Helper.hashAllFields(fields, Config.vnp_HashSecret);
        if (!hash.equals(vnp_SecureHash))
            throw new RuntimeException("Wrong checksum");

        Long orderId = Long.parseLong(fields.get("vnp_TxnRef"));
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Order " + orderId + " not exist");
        }

        Status orderStatus = order.getOrderStatus();
        if (orderStatus == Status.PENDING) {
            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                orderService.updateOrderStatus(orderId, Status.SUCCESS);
                log.info("Transation success");
            } else {
                orderService.updateOrderStatus(orderId, Status.UNSUCCESS);
                log.info("Transation fail");
            }
            log.info("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
            HashMap<String, String> result = new HashMap<>();
            result.put("RspCode", "00");
            log.info("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
            return result;
        } else {
            HashMap<String, String> result = new HashMap<>();
            result.put("RspCode", "02");
            log.info("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
            return result;
        }

    }
}
