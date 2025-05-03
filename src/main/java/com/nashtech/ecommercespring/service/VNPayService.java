package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.response.OrderDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public interface VNPayService {
    String createVNPayPaymentUrl(HttpServletRequest req, UUID orderId);

    OrderDTO updateOrderStatusByParams(Map<String, String> params);
}
