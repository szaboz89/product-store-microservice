package com.szabodev.example.spring.product.store.microservice.service;

import com.szabodev.example.spring.product.store.microservice.dto.ProductStockDTO;

import java.util.List;
import java.util.Optional;

public interface ProductStockService {

    Optional<ProductStockDTO> findById(Long id);

    Optional<ProductStockDTO> findByProductId(Long id);

    List<ProductStockDTO> findAll();

    void deleteById(Long id);

    List<ProductStockDTO> init(int multiplier);

    ProductStockDTO save(ProductStockDTO productStock);
}
