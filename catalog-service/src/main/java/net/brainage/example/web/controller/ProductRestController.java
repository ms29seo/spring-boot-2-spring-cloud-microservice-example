package net.brainage.example.web.controller;

import lombok.extern.slf4j.Slf4j;
import net.brainage.example.domain.Product;
import net.brainage.example.service.ProductService;
import net.brainage.example.web.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = {"/api/products"})
public class ProductRestController {

    private final ProductService productService;

    @Autowired
    public ProductRestController(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> allProducts(final HttpServletRequest httpRequest) {
        log.info("Finding all products");
        return this.productService.findAllProducts();
    }

    @GetMapping(path = {"/{code}"})
    public Product productByCode(final @PathVariable(name = "code") String code) {
        return this.productService.findProductByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product with code [%s] dosen't exist.", code)));
    }

}
