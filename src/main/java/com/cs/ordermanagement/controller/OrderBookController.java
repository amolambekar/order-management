package com.cs.ordermanagement.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.transaction.Transactional;

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

import com.cs.ordermanagement.InstrumentDAO;
import com.cs.ordermanagement.domain.Execution;
import com.cs.ordermanagement.domain.Instrument;
import com.cs.ordermanagement.domain.Order;
import com.cs.ordermanagement.domain.OrderBook;
import com.cs.ordermanagement.domain.OrderBook.OrderBookStatus;
import com.cs.ordermanagement.repository.OrderBookRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/orderbooks")
public class OrderBookController
 {
	
	private OrderBookService orderBokkService;
	
	public InstrumentDAO instrumentDAO;
	public OrderBookRepository orderBookRepository;

	@Autowired
	public OrderBookController(OrderBookService orderBookService,InstrumentDAO instrumentDAO,OrderBookRepository orderBookRepository) {
		this.orderBokkService=orderBookService;
		this.instrumentDAO=instrumentDAO;
		this.orderBookRepository=orderBookRepository;
	}
    
	
	@Transactional
	@PostMapping(path="/{instrumentId}",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrderBook> openNewOrderBook(@PathVariable(name="instrumentId")Long instrumentId) {
        OrderBook orderBook = orderBokkService.createAndopenNewWorkBook(instrumentId);
		return new ResponseEntity<OrderBook>(orderBook,HttpStatus.CREATED);

	}


	
	
	@Transactional
	@PutMapping(path="/{orderBookId}/{status}")
	public void updateOrderBookStatus(@PathVariable(name="orderBookId")Long orderBookId,@PathVariable(name="status")String status) {
	 orderBokkService.updateOrderBookStatus(orderBookId,status);

	}
	
	@Transactional
	@PostMapping(path="/{orderBookId}/orders")
	public void addOrders(@PathVariable Long orderBookId,@RequestBody List<Order> orders){
		orderBokkService.addOrders(orderBookId,orders);
	}
	
	@Transactional
	@PostMapping(path="/{orderBookId}/executions/{quantity}/{price}") 
	public void addExecution(@PathVariable(name="orderBookId") Long orderBookId, @PathVariable(name="quantity") Long quantity,@PathVariable(name="price") BigDecimal price) {
		Execution executionId =this.orderBokkService.addExecution( orderBookId,quantity,price);
		if(executionId!=null)
		log.trace("the execution Id is ***********************************  "+executionId.getExecutionId());
	}
	
	@GetMapping(path="/{orderBookId}")
	public ResponseEntity<OrderBook> findOrderBook(@PathVariable(name="orderBookId") Long orderBookId){
    OrderBook orderBook =orderBookRepository.findOne(orderBookId);
    return new ResponseEntity<>(orderBook,HttpStatus.OK);
    
    
    }
	
	
	
	
    	
    
    


	
	

}
