package com.example.sandbox_vnpay_maven.order.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Helper {
//    public static String convertToQueryString(Map<String, String> params) throws UnsupportedEncodingException {
//        StringBuilder queryString = new StringBuilder();
//        Iterator<String> itr = params.keySet().iterator();
//        while (itr.hasNext()) {
//            String key = itr.next();
//            String value = params.get(key);
//            if (value != null && value.length() > 0) {
//                queryString.append(URLEncoder.encode(key, StandardCharsets.US_ASCII.toString()));
//                queryString.append('=');
//                queryString.append(URLEncoder.encode(value, StandardCharsets.US_ASCII.toString()));
//
//                if (itr.hasNext()) {
//                    queryString.append('&');
//                }
//            }
//        }
//
//        return queryString.toString();
//    }

    public static String hashAllFields(Map fields, String hashSecret) {
        // create a list and sort it
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        // create a buffer for the md5 input and add the secure secret first
        StringBuilder sb = new StringBuilder();
        sb.append(hashSecret);
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        return Sha256(sb.toString());
    }

    public static String Sha256(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(message.getBytes("UTF-8"));

            // converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }

            digest = sb.toString();

        } catch (UnsupportedEncodingException ex) {
            digest = "";
        } catch (NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

}
