package com.nashtech.ecommercespring.exception;

public class ExceptionMessages {
    public static final String NOT_FOUND = "%s not found";
    public static final String ALREADY_EXISTS = "%s already exists";
    public static final String IS_EMPTY = "%s is empty";

    public static final String INSUFFICIENT_STOCK = "Only %d units of %s are available in stock";
    public static final String IMAGE_UPLOAD_FAILED = "Image upload failed";
    public static final String IMAGE_DELETE_FAILED = "Image delete failed";
    public static final String PRODUCT_STATUS_IS = "%s status is %s";

    public static final String PAYMENT_FAILED = "Payment id: %s failed";
    public static final String PAYMENT_INVALID_SIGNATURE = "Payment id: %s invalid signature";

    public static final String UNEXPECTED_ERROR = "Unexpected error occurred: {}";

    public static final String PRODUCT_NOT_PURCHASED = "You must purchase %s to perform this action.";

    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access to {} from IP {}: {}";
}
