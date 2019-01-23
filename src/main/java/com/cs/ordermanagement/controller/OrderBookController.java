package com.cs.ordermanagement.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.ordermanagement.domain.Order;
import com.cs.ordermanagement.domain.OrderBook;
import com.cs.ordermanagement.exception.ClosedOrderBookException;
import com.cs.ordermanagement.exception.OrderBookManagementException;
import com.cs.ordermanagement.repository.OrderBookRepository;
import com.cs.ordermanagement.request.ExecutionReuest;
import com.cs.ordermanagement.service.OrderBookService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/orderbooks")
public class OrderBookController
 {
	
	private OrderBookService orderBokkService;
	
	private OrderBookRepository orderBookRepository;

	@Autowired
	public OrderBookController(OrderBookService orderBookService,OrderBookRepository orderBookRepository) {
		this.orderBokkService=orderBookService;
		this.orderBookRepository=orderBookRepository;
	}
    
	
	@PostMapping(path="/{instrumentId}",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderBook> openNewOrderBook(@PathVariable(name="instrumentId")Long instrumentId) {
        OrderBook orderBook = orderBokkService.createAndOpenNewOrderBook(instrumentId);
		return new ResponseEntity<OrderBook>(orderBook,HttpStatus.CREATED);

	}


	@PutMapping(path="/{orderBookId}/closeorderbook")
	public ResponseEntity<Object> closeOrderBook(  @Valid @PathVariable(name="orderBookId")Long orderBookId) {
		
	 try {
		orderBokkService.closeOrderBook(orderBookId);
		return new ResponseEntity<Object>(HttpStatus.OK);
	} catch (ClosedOrderBookException | OrderBookManagementException e) {
		return new ResponseEntity<Object>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

	}
	

	@PostMapping(path="/{orderBookId}/orders")
	public ResponseEntity<Object> addOrders(@PathVariable Long orderBookId,@RequestBody List<Order> orders){
		try {
			orderBokkService.addOrders(orderBookId,orders);
			return new ResponseEntity<Object>(HttpStatus.CREATED);
		} catch (OrderBookManagementException e) {
			return new ResponseEntity<Object>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	@PostMapping(path="/{orderBookId}/executions") 
	public ResponseEntity<Object> addExecution(@PathVariable  @Valid Long orderBookId, @Valid @RequestBody ExecutionReuest executionRequest) {
		try {
			this.orderBokkService.addExecution( orderBookId,executionRequest.getQuantity(),executionRequest.getPrice());
			return new ResponseEntity<Object>(HttpStatus.CREATED);
		} catch (OrderBookManagementException e) {
			log.error(e.getExceptionMessage());
			return new ResponseEntity<Object>(e.getExceptionMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
	
	@GetMapping(path="/{orderBookId}")
	public ResponseEntity<OrderBook> findOrderBook(@PathVariable(name="orderBookId") Long orderBookId){
    OrderBook orderBook =orderBookRepository.findOne(orderBookId);
    return new ResponseEntity<>(orderBook,HttpStatus.OK);
    
    
    }
	
 }
