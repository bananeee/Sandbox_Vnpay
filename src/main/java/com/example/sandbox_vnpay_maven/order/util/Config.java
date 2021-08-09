package com.example.sandbox_vnpay_maven.order.util;

import org.springframework.beans.factory.annotation.Value;

public class Config {

    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

//    @Value("${host.url}/return")
    public static String vnp_Returnurl;
    public static String vnp_TmnCode = "4IL9CXBG";
    public static String vnp_HashSecret = "WNPMDSPGRIVDGRBAUIMJBKVZRURTYFBN";
    //    TODO: Change this
    public static String vnp_apiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/merchant.html";
}
