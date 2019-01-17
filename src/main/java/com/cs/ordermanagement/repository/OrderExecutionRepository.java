package com.cs.ordermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs.ordermanagement.domain.OrderExecution;

public interface OrderExecutionRepository  extends JpaRepository<OrderExecution, Long>{
	 /* //find valid and Invalid orders
			List<Order> findAllByStatus(OrderStatus OrderStatus);
			
			//find valid/Invalid demand
			Long countQuantityByStatusAndOrderBookId(OrderStatus OrderStatus,Long OrderBookId);
			
*/
}
