package com.szabodev.example.spring.product.store.microservice.service;

import com.szabodev.example.spring.product.store.microservice.dto.ProductDTO;
import com.szabodev.example.spring.product.store.microservice.dto.ProductStockDTO;
import com.szabodev.example.spring.product.store.microservice.model.ProductStock;
import com.szabodev.example.spring.product.store.microservice.repository.ProductStockRepository;
import com.szabodev.example.spring.product.store.microservice.service.mapper.ProductStockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductStockServiceImpl implements ProductStockService {

    private final ProductStockRepository productStockRepository;

    private final ProductStockMapper productStockMapper;

    private final RestTemplate restTemplate;

    @Value("${book.microservice.apiUrl}")
    private String apiUrl;

    @Override
    public Optional<ProductStockDTO> findById(Long id) {
        Optional<ProductStock> byId = productStockRepository.findById(id);
        return Optional.ofNullable(productStockMapper.toDTO(byId.orElse(null)));
    }

    @Override
    public Optional<ProductStockDTO> findByProductId(Long id) {
        Optional<ProductStock> byId = productStockRepository.findByProductId(id);
        return Optional.ofNullable(productStockMapper.toDTO(byId.orElse(null)));
    }

    @Override
    public List<ProductStockDTO> findAll() {
        return productStockMapper.toDTOs((List<ProductStock>) productStockRepository.findAll());
    }

    @Override
    public void deleteById(Long id) {
        productStockRepository.deleteById(id);
    }

    @Override
    public List<ProductStockDTO> init(int multiplier) {
        productStockRepository.deleteAll();
        ProductDTO[] products = restTemplate.getForObject(apiUrl + "/products", ProductDTO[].class);
        if (products != null) {
            for (ProductDTO product : products) {
                productStockRepository.save(ProductStock.builder()
                        .productId(product.getId())
                        .available(product.getMinimumStock() * multiplier)
                        .build());
            }
        }
        return findAll();
    }
}
