package com.szabodev.example.spring.product.store.microservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szabodev.example.spring.product.store.microservice.config.JmsConfig;
import com.szabodev.example.spring.product.store.microservice.dto.OrderRequestDTO;
import com.szabodev.example.spring.product.store.microservice.dto.OrderResponseDTO;
import com.szabodev.example.spring.product.store.microservice.dto.ProductDemandDTO;
import com.szabodev.example.spring.product.store.microservice.model.ProductStock;
import com.szabodev.example.spring.product.store.microservice.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Component
@Slf4j
public class MessageListener {

    private final ObjectMapper objectMapper;
    private final ProductStockRepository productStockRepository;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.PRODUCT_DEMAND_QUEUE)
    public void demandListener(Message message) throws JMSException {
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

    @JmsListener(destination = JmsConfig.ORDER_REQUEST_QUEUE)
    public void orderListener(Message message) throws JMSException {
        log.info("Handling order. Message: {}", message);
        AtomicReference<String> status = new AtomicReference<>("NOK");
        TextMessage textMessage = (TextMessage) message;
        try {
            OrderRequestDTO orderRequest = objectMapper.readValue(textMessage.getText(), OrderRequestDTO.class);
            productStockRepository.findByProductId(orderRequest.getProductId()).ifPresent(productStock -> {
                log.info("Stock found");
                if (productStock.getAvailable() != null && productStock.getAvailable() >= orderRequest.getAmount()) {
                    productStock.setAvailable(productStock.getAvailable() - orderRequest.getAmount());
                    productStockRepository.save(productStock);
                    status.set("OK");
                    log.info("Amount available");
                } else {
                    log.info("Amount not available");
                }
            });
        } catch (JsonProcessingException e) {
            System.out.println("MessageListener - Cannot convert to object: " + textMessage.getText());
        }
        OrderResponseDTO replyMessage = OrderResponseDTO.builder().status(status.get()).build();
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), replyMessage);
        log.info("Reply message sent: {}", message);
    }
}
