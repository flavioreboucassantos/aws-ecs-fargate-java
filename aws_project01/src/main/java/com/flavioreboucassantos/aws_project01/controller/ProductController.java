package com.flavioreboucassantos.aws_project01.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flavioreboucassantos.aws_project01.enums.EventType;
import com.flavioreboucassantos.aws_project01.model.Product;
import com.flavioreboucassantos.aws_project01.repository.ProductRepository;
import com.flavioreboucassantos.aws_project01.service.ProductPublisher;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private ProductRepository productRepository;
	private ProductPublisher productPublisher;

	@Autowired
	public ProductController(ProductRepository productRepository, ProductPublisher productPublisher) {
		this.productRepository = productRepository;
		this.productPublisher = productPublisher;
	}

	@GetMapping
	public Iterable<Product> findAll() {
		return productRepository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> findById(@PathVariable long id) {
		Optional<Product> optProduct = productRepository.findById(id);
		if (optProduct.isPresent()) {
			return new ResponseEntity<Product>(optProduct.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping
	public ResponseEntity<Product> saveProduct(
			@RequestBody Product product) {
		product.setId(null);
		Product productCreated = productRepository.save(product);

		productPublisher.publishProductEvent(productCreated, EventType.PRODUCT_CREATED, "matilde");
		
		return new ResponseEntity<Product>(productCreated,
				HttpStatus.CREATED);
	}

	@PutMapping(path = "/{id}")
	public ResponseEntity<Product> updateProduct(
			@RequestBody Product product, @PathVariable("id") long id) {
		if (productRepository.existsById(id)) {
			product.setId(id);

			Product productUpdated = productRepository.save(product);
			
			productPublisher.publishProductEvent(productUpdated, EventType.PRODUCT_UPDATE, "doralice");

			return new ResponseEntity<Product>(productUpdated,
					HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
		Optional<Product> optProduct = productRepository.findById(id);
		if (optProduct.isPresent()) {
			Product product = optProduct.get();

			productRepository.delete(product);
			
			productPublisher.publishProductEvent(product, EventType.PRODUCT_DELETED, "hannah");

			return new ResponseEntity<Product>(product, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(path = "/bycode")
	public ResponseEntity<Product> findByCode(@RequestParam String code) {
		Optional<Product> optProduct = productRepository.findByCode(code);
		if (optProduct.isPresent()) {
			return new ResponseEntity<Product>(optProduct.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
