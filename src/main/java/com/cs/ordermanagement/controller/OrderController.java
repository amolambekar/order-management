package com.cs.ordermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.ordermanagement.repository.OrderRepository;

@RestController
@RequestMapping("/v1/orders")
public class OrderController {
	
	@Autowired
	OrderRepository repository;

	
	@GetMapping(path="/count/{orderBookId}")
    public Long getOrdeCount(@PathVariable Long orderBookId) {
		return repository.countByOrderBookId(orderBookId);

}
	
	 

}