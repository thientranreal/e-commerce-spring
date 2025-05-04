package com.nashtech.ecommercespring.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    public String uploadImage(MultipartFile file);
    public void deleteImage(String imageUrl);
}
