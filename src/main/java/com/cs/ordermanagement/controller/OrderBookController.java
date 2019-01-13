package com.cs.ordermanagement.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
import com.cs.ordermanagement.domain.OrderBook.Status;
import com.cs.ordermanagement.repository.OrderBookRepository;

@RestController
@RequestMapping("/v1/orderbooks")
public class OrderBookController {
	
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
        Instrument instrument = new Instrument();
        instrument.setInstrumentId(instrumentId);
		OrderBook orderBook = new OrderBook();
		orderBook.setInstrumentId(instrument);
		orderBook.setStatus(Status.OPEN);
		orderBook =orderBookRepository.save(orderBook);
		return new ResponseEntity<OrderBook>(orderBook,HttpStatus.CREATED);

	}
	
	@Transactional
	@PutMapping(path="/{orderBookId}/{status}")
	public void updateOrderBookStatus(@PathVariable(name="orderBookId")Long orderBookId,@PathVariable(name="status")String status) {
		OrderBook orderBook =orderBookRepository.findOne(orderBookId);
		orderBook.setStatus(Status.valueOf(status));
		orderBookRepository.save(orderBook);

	}
	
	@Transactional
	@PostMapping(path="/{orderBookId}/orders")
	public void addOrders(@PathVariable Long orderBookId,@RequestBody List<Order> orders){
		OrderBook orderBook = orderBookRepository.findOne(orderBookId);
		if(orderBook.getStatus().equals(Status.OPEN)) {
			orderBook.getOrders().addAll(orders);
			orderBookRepository.save(orderBook);
		}
	}
	
	@Transactional
	@PostMapping(path="/{orderBookId}/executions") 
	public void addExecution(@PathVariable Long orderBookId, @RequestBody Execution execution) {
		this.orderBokkService.addExecution(execution, orderBookId);
	}
	
	@GetMapping(path="/{orderBookId}")
	public ResponseEntity<OrderBook> findOrderBook(@PathVariable(name="orderBookId") Long orderBookId){
    OrderBook orderBook =orderBookRepository.findOne(orderBookId);
    return new ResponseEntity<>(orderBook,HttpStatus.OK);
    
    
    }
	
	
    	
    
    


	
	

}
