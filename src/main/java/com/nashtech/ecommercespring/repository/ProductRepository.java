package com.nashtech.ecommercespring.repository;

import com.nashtech.ecommercespring.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Page<Product> findByDeletedFalse(Pageable pageable);
    Page<Product> findByIsFeaturedTrueAndDeletedFalse(Pageable pageable);
}
