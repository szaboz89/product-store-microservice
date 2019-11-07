package com.szabodev.example.spring.product.store.microservice.service.mapper;

import com.szabodev.example.spring.product.store.microservice.dto.ProductStockDTO;
import com.szabodev.example.spring.product.store.microservice.model.ProductStock;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductStockMapper {

    ProductStockDTO toDTO(ProductStock productStock);

    List<ProductStockDTO> toDTOs(List<ProductStock> productStocks);

    ProductStock toEntity(ProductStockDTO productStockDTO);
}
