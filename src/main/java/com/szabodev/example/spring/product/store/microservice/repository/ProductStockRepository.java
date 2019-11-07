package com.szabodev.example.spring.product.store.microservice.repository;

import com.szabodev.example.spring.product.store.microservice.model.ProductStock;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductStockRepository extends CrudRepository<ProductStock, Long> {

    Optional<ProductStock> findByProductId(Long id);
}
