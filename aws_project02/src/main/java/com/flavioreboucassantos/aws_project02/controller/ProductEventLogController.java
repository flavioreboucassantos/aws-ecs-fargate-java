package com.flavioreboucassantos.aws_project02.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flavioreboucassantos.aws_project02.model.ProductEventLogDto;
import com.flavioreboucassantos.aws_project02.repository.ProductEventLogRepository;

@RestController
@RequestMapping("/api")
public class ProductEventLogController {
	
	private ProductEventLogRepository productEventLogRepository;

    @Autowired
    public ProductEventLogController(ProductEventLogRepository productEventLogRepository) {
        this.productEventLogRepository = productEventLogRepository;
    }
    
    @GetMapping("/events")
    public List<ProductEventLogDto> getAllEvents() {
        return StreamSupport
                .stream(productEventLogRepository.findAll().spliterator(), false)                
                .map(ProductEventLogDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}")
    public List<ProductEventLogDto> findByCode(@PathVariable String code) {
        return productEventLogRepository.findAllByPk(code)
                .stream()
                .map(ProductEventLogDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events/{code}/{event}")
    public List<ProductEventLogDto> findByCodeAndEventType(@PathVariable String code,
                                                           @PathVariable String event) {
        return productEventLogRepository.findAllByPkAndSkStartsWith(code, event)
                .stream()
                .map(ProductEventLogDto::new)
                .collect(Collectors.toList());
    }

}
