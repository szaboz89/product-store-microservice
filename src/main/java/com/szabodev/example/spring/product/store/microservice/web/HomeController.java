package com.szabodev.example.spring.product.store.microservice.web;

import com.szabodev.example.spring.product.store.microservice.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private static final String PRODUCT_STOCK_PRODUCT_STOCKS = "product-stock/product-stocks";
    private static final String REDIRECT_PRODUCT_STOCKS = "redirect:/product-stocks";

    private final ProductStockService productStockService;

    @GetMapping({"/", "/product-stocks"})
    public String productStocks(Model model) {
        model.addAttribute("productSocks", productStockService.findAll());
        return PRODUCT_STOCK_PRODUCT_STOCKS;
    }


    @GetMapping("/product-stocks/{id}/delete")
    public String deleteProductStock(@PathVariable Long id) {
        productStockService.findById(id).ifPresent(productStock -> {
            log.info("Delete productStock: {}", productStock);
            productStockService.deleteById(id);
        });
        return REDIRECT_PRODUCT_STOCKS;
    }

    @GetMapping("/product-stocks/{id}/complete-demand")
    public String completedDemand(@PathVariable Long id) {
        productStockService.findById(id).ifPresent(productStock -> {
            log.info("Complete demand for productStock: {}", productStock);
            if (productStock.getRequiredAmount() != null) {
                if (productStock.getAvailable() != null) {
                    productStock.setAvailable(productStock.getAvailable() + productStock.getRequiredAmount());
                } else if (productStock.getRequiredAmount() != null) {
                    productStock.setAvailable(productStock.getRequiredAmount());
                }
            }
            productStock.setRequiredAmount(null);
            productStockService.save(productStock);
        });
        return REDIRECT_PRODUCT_STOCKS;
    }

    @GetMapping("/product-stocks/{id}/delete-demand")
    public String deleteDemand(@PathVariable Long id) {
        productStockService.findById(id).ifPresent(productStock -> {
            log.info("Delete demand for productStock: {}", productStock);
            productStock.setRequiredAmount(null);
            productStockService.save(productStock);
        });
        return REDIRECT_PRODUCT_STOCKS;
    }

    @GetMapping("/product-stocks/{id}/decrease-available")
    public String decreaseAvailableCount(@PathVariable Long id) {
        productStockService.findById(id).ifPresent(productStock -> {
            log.info("Decrease available count for productStock: {}", productStock);
            if (productStock.getAvailable() != null && productStock.getAvailable() > 0) {
                productStock.setAvailable(productStock.getAvailable() - 1);
                productStockService.save(productStock);
            }
        });
        return REDIRECT_PRODUCT_STOCKS;
    }

    @GetMapping("/product-stocks/{id}/increase-available")
    public String increaseAvailableCount(@PathVariable Long id) {
        productStockService.findById(id).ifPresent(productStock -> {
            log.info("Increase available count for productStock: {}", productStock);
            if (productStock.getAvailable() != null) {
                productStock.setAvailable(productStock.getAvailable() + 1);
            } else {
                productStock.setAvailable(1);
            }
            productStockService.save(productStock);
        });
        return REDIRECT_PRODUCT_STOCKS;
    }
}
