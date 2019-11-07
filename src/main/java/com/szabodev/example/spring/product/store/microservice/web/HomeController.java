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
}
