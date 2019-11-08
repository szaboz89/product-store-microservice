package com.szabodev.example.spring.product.store.microservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szabodev.example.spring.product.store.microservice.config.JmsConfig;
import com.szabodev.example.spring.product.store.microservice.dto.ProductDemandDTO;
import com.szabodev.example.spring.product.store.microservice.model.ProductStock;
import com.szabodev.example.spring.product.store.microservice.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class DemandListener {

    private final ObjectMapper objectMapper;
    private final ProductStockRepository productStockRepository;

    @JmsListener(destination = JmsConfig.PRODUCT_DEMAND_QUEUE)
    public void listen(Message message) throws JMSException {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                ProductDemandDTO productDemand = objectMapper.readValue(textMessage.getText(), ProductDemandDTO.class);
                Optional<ProductStock> productStock = productStockRepository.findByProductId(productDemand.getProductId());
                if (productStock.isPresent()) {
                    productStock.get().setRequiredAmount(productDemand.getRequiredAmount());
                    productStockRepository.save(productStock.get());
                } else {
                    ProductStock newStock = ProductStock.builder()
                            .productId(productDemand.getProductId())
                            .requiredAmount(productDemand.getRequiredAmount())
                            .build();
                    productStockRepository.save(newStock);
                }
            } catch (JsonProcessingException e) {
                System.out.println("MessageListener - Cannot convert to object: " + textMessage.getText());
            }
        }
    }
}
