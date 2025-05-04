package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.response.OrderDTO;
import com.nashtech.ecommercespring.enums.OrderStatus;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.payment.vnpay.VNPayConfig;
import com.nashtech.ecommercespring.service.OrderService;
import com.nashtech.ecommercespring.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@AllArgsConstructor
public class VNPayServiceImpl  implements VNPayService {

    private final VNPayConfig vnPayConfig;

    private final OrderService orderService;

    @Override
    public String createVNPayPaymentUrl(HttpServletRequest req, UUID orderId) {

        OrderDTO order = orderService.getOrderById(orderId);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        String vnp_TxnRef = orderId.toString();
        String vnp_OrderInfo = "Purchase for OrderId: " + vnp_TxnRef;
        String vnp_IpAddr = vnPayConfig.getIpAddress(req);
        String vnp_TmnCode = vnPayConfig.vnp_TmnCode;

        BigDecimal amount = order.getTotal().multiply(BigDecimal.valueOf(100));
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "en");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        //Add Params of 2.1.0 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        //Build data to hash and querystring
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnPayConfig.vnp_Url + "?" + queryUrl;
    }

    @Override
    public OrderDTO updateOrderStatusByParams(Map<String, String> params) {
        Map<String, String> fields = new HashMap<>();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String fieldName = URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII);
            String fieldValue = URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = params.get("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

//        Check checksum
        String signValue = vnPayConfig.hashAllFields(fields);

//        If the signature is valid
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(params.get("vnp_ResponseCode"))) {
                return orderService.updateStatusById(
                                UUID.fromString(params.get("vnp_TxnRef")),
                                OrderStatus.CONFIRMED
                        );
            } else {
                orderService.updateStatusById(
                                UUID.fromString(params.get("vnp_TxnRef")),
                                OrderStatus.CANCELLED
                        );

                throw new BadRequestException(ExceptionMessages.PAYMENT_FAILED);
            }
        } else {
//            If the signature is invalid
            throw new BadRequestException(ExceptionMessages.PAYMENT_INVALID_SIGNATURE);
        }
    }
}
