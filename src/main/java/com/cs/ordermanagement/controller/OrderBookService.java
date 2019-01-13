package com.cs.ordermanagement.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.ordermanagement.InstrumentDAO;
import com.cs.ordermanagement.domain.Execution;
import com.cs.ordermanagement.domain.Order;
import com.cs.ordermanagement.domain.Order.OrderStatus;
import com.cs.ordermanagement.domain.Order.OrderType;
import com.cs.ordermanagement.domain.OrderBook;
import com.cs.ordermanagement.domain.OrderBook.Status;
import com.cs.ordermanagement.repository.OrderBookRepository;

@Service
public class OrderBookService {
	
	public OrderBookRepository orderBookRepository;

	@Autowired
	public OrderBookService(InstrumentDAO instrumentDAO,OrderBookRepository orderBookRepository) {
	
		this.orderBookRepository=orderBookRepository;
	}
	
	public void addExecution(Execution execution,Long orderBookId) {
        System.out.println("calling service method");
		OrderBook orderBook = orderBookRepository.findOne(orderBookId);
		 System.out.println("orderBook is found with status "+orderBook.getStatus());
		if (orderBook.getStatus().equals(Status.CLOSED)) {
			orderBook.getExecutions().add(execution);
			List<Order> validOrders = this.getValidOrders(orderBook);
			Integer totalSum = validOrders.stream().map(e -> e.getOrderQuantity()).reduce(Integer::sum).get();
			System.out.println("total valid sum is "+totalSum+ "     "+execution.getQuantity());
			if (execution.getQuantity().equals(totalSum)) {
				System.out.println("setting order as executed");
				orderBook.setStatus(Status.EXECUTED);
			}
			 System.out.println("start distribution");
			applyExecution(execution, orderBook, validOrders);
			System.out.println("The status of orderbooks is "+orderBook.getStatus());
			orderBook.getOrders().stream().forEach(e->System.out.println(e.getOrderQuantity()));

		}
	
	}

	private void applyExecution(Execution execution, OrderBook orderBook, List<Order> validOrders) {
		Integer executionQuantity = execution.getQuantity();
		validOrders.sort((o1, o2) -> o2.getOrderQuantity().compareTo(o1.getOrderQuantity()));
		
		while (executionQuantity > 0) {
			int totalDistributedQuantity =0;
			Integer intermediateSum = validOrders.stream().map(e -> e.getOrderQuantity()).reduce(Integer::sum).get();
			for (Order order : validOrders) {
				Integer orderQuantity = order.getOrderQuantity();
				int distributedQuantity = (orderQuantity * executionQuantity) / intermediateSum;
				
				if (distributedQuantity > 0) {
					order.setOrderQuantity(orderQuantity - distributedQuantity);
					totalDistributedQuantity=totalDistributedQuantity+distributedQuantity;
					
				} else {
					Order largestOrder = validOrders.get(0);
					int largestOrderCurrentQuantity = largestOrder.getOrderQuantity();
					largestOrder.setOrderQuantity(largestOrderCurrentQuantity - executionQuantity);
					totalDistributedQuantity=totalDistributedQuantity+ executionQuantity;
					System.out.println(" orderId   distributed quantity  remainingQuantity ");
					System.out.println("     "+largestOrder.getOrderId()+ "             "+executionQuantity+ "            "+largestOrder.getOrderQuantity());
					break;
				}
				
				System.out.println(" orderId   distributed quantity  remainingQuantity ");
				System.out.println("     "+order.getOrderId()+ "             "+distributedQuantity+ "            "+order.getOrderQuantity());
			}
			
			System.out.println("total distributed quantity "+totalDistributedQuantity);
			executionQuantity = executionQuantity - totalDistributedQuantity;
			
		}
		System.out.println("final remaining execution quantity "+executionQuantity);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		orderBookRepository.save(orderBook);
	}
	
	public List<Order> getValidOrders(OrderBook orderBook) {
		if(orderBook !=null && orderBook.getExecutions()!=null) {
			Execution execution =orderBook.getExecutions().get(0);
		
		return orderBook.getOrders().stream()
		.filter(e -> e.getOrderType()==OrderType.MARKET || e.getPrice().compareTo(execution.getPrice())>=0).peek(e->e.setStatus(OrderStatus.VALID))
		.collect(Collectors.toList());
		}
		
		return new ArrayList<>();
	}
	
	public List<Order> getInValidOrders(OrderBook orderBook) {
		if(orderBook !=null && orderBook.getExecutions()!=null) {
			Execution execution =orderBook.getExecutions().get(0);
		
		return orderBook.getOrders().stream()
		.filter(e -> e.getPrice().compareTo(execution.getPrice()) < 0).peek(e->e.setStatus(OrderStatus.INVALID))
		.collect(Collectors.toList());
		}
		
		return new ArrayList<>();
	}
	
	
}