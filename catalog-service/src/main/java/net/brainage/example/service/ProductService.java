package net.brainage.example.service;

import lombok.extern.slf4j.Slf4j;
import net.brainage.example.domain.Product;
import net.brainage.example.domain.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAllProducts() {
        return this.productRepository.findAll();
    }

    public Optional<Product> findProductByCode(final String code) {
        return this.productRepository.findByCode(code);
    }

}
