package com.nashtech.ecommercespring.controller;

import com.nashtech.ecommercespring.dto.request.PaymentReqDTO;
import com.nashtech.ecommercespring.dto.response.OrderDTO;
import com.nashtech.ecommercespring.response.ApiResponse;
import com.nashtech.ecommercespring.response.SuccessMessages;
import com.nashtech.ecommercespring.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/vnpay")
public class VNPayController {
    private final VNPayService vnPayService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createVNPayPaymentUrl(
            HttpServletRequest req,
            @RequestBody @Valid PaymentReqDTO paymentReqDTO
            ) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message(String.format(SuccessMessages.CREATE_SUCCESS, "Payment URL"))
                .data(vnPayService.createVNPayPaymentUrl(
                        req,
                        paymentReqDTO.getOrderId())
                )
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/return")
    public ResponseEntity<ApiResponse<OrderDTO>> handleVNPayReturn(@RequestParam Map<String, String> params) {
        ApiResponse<OrderDTO> response = ApiResponse.<OrderDTO>builder()
                .success(true)
                .message(SuccessMessages.PAYMENT_SUCCESSFUL)
                .data(vnPayService.updateOrderStatusByParams(params))
                .build();

        return ResponseEntity.ok(response);
    }
}
