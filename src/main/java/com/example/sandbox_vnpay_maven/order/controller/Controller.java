package com.example.sandbox_vnpay_maven.order.controller;

import com.example.sandbox_vnpay_maven.order.util.Config;
import com.example.sandbox_vnpay_maven.order.model.Order;
import com.example.sandbox_vnpay_maven.order.service.OrderService;
import com.example.sandbox_vnpay_maven.order.util.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@org.springframework.stereotype.Controller
public class Controller {

    @Value("${host.url}")
    private String hostUrl;

    private final Logger log = LoggerFactory.getLogger(getClass());

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
    public String createOrder(HttpServletRequest request) throws IOException {
//        Create new Order with pending status
        Long orderAmount = Long.parseLong(request.getParameter("amount")) * 100;
        String orderDes = request.getParameter("description");
        Order newOrder = new Order(orderAmount, orderDes);
        String ipAddr = request.getRemoteAddr();
        log.info("ip: " + ipAddr);
        Long createdOrderId = orderService.createOrder(newOrder, ipAddr);

//        Query Parameter
        String vnp_Version = "2.0.1";

        String vnp_Command = "pay";

        String vnp_TmnCode = Config.vnp_TmnCode;

        String vnp_Amount = orderAmount.toString();

        String vnp_BankCode = request.getParameter("bankcode");

        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String vnp_CreateDate = time.format(formatter);

        String vnp_CurrCode = "VND";

        String vnp_IpAddr = ipAddr;

        String vnp_Locale = request.getParameter("language");

        String vnp_OrderInfo = orderDes;

        String vnp_OrderType = request.getParameter("ordertype");

//        String vnp_ReturnUrl = Config.vnp_Returnurl;
        String vnp_ReturnUrl = hostUrl + "return";

        String vnp_TxnRef = createdOrderId.toString();
//        String vnp_TxnRef = Helper.getRandomNumber(8);

        Map<String, String> params = new HashMap<>();

        params.put("vnp_Version", vnp_Version);
        params.put("vnp_Command", vnp_Command);
        params.put("vnp_TmnCode", vnp_TmnCode);
        params.put("vnp_Amount", vnp_Amount);
        params.put("vnp_BankCode", vnp_BankCode);
        params.put("vnp_CreateDate", vnp_CreateDate);
        params.put("vnp_CurrCode", vnp_CurrCode);
        params.put("vnp_IpAddr", vnp_IpAddr);
        params.put("vnp_Locale", vnp_Locale);
        params.put("vnp_OrderInfo", vnp_OrderInfo);
        params.put("vnp_OrderType", vnp_OrderType);
        params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        params.put("vnp_TxnRef", vnp_TxnRef);

        List fieldNames = new ArrayList(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(fieldValue);
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Helper.Sha256(Config.vnp_HashSecret + hashData);
        log.info("Hash " + vnp_SecureHash);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = Config.vnp_PayUrl + '?' + queryUrl;
        log.info("Paymenr url: " + paymentUrl);

        return "redirect:" + paymentUrl;
    }

//    @GetMapping("return")
//    public String returnPage(Model model,
//                             @RequestParam String vnp_TmnCode,
//                             @RequestParam String vnp_Amount,
//                             @RequestParam String vnp_BankCode,
//                             @RequestParam(required = false) String vnp_BankTranNo,
//                             @RequestParam(required = false) String vnp_CardType,
//                             @RequestParam(required = false) String vnp_PayDate,
//                             @RequestParam String vnp_OrderInfo,
//                             @RequestParam String vnp_TransactionNo,
//                             @RequestParam String vnp_ResponseCode,
//                             @RequestParam String vnp_TxnRef,
//                             @RequestParam(required = false) String vnp_SecureHashType,
//                             @RequestParam String vnp_SecureHash) {
//
//        model.addAttribute("vnp_TmnCode", vnp_TmnCode);
//        model.addAttribute("vnp_Amount", vnp_Amount);
//        model.addAttribute("vnp_BankCode", vnp_BankCode);
//        model.addAttribute("vnp_OrderInfo", vnp_OrderInfo);
//        model.addAttribute("vnp_TransactionNo", vnp_TransactionNo);
//        model.addAttribute("vnp_ResponseCode", vnp_ResponseCode);
//        model.addAttribute("vnp_TxnRef", vnp_TxnRef);
//        return "returnpage";
//    }

    @GetMapping("return")
    public String returnPage(Model model,
                              HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
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

        model.addAllAttributes(fields);
        return "returnpage";
    }



    @GetMapping("order/{id}")
    public String orderDetail(Model model,
                              @PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "orderdetail";
    }
}
