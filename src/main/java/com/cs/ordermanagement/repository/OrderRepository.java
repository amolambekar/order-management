package com.cs.ordermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cs.ordermanagement.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
	
	//amount of orders in Each Book
	Long countByOrderBookId(Long orderBookId);
	
	//biggest order by price
	Order findFirstByOrderBookIdOrderByPriceDesc(Long orderBookId);
	
	//demand =accumulated quantity
	
	Long countQuantityByOrderBookId(Long orderBookId);
	
	//smallest order by price
	Order findFirstByOrderBookIdOrderByPriceAsc(Long orderBookId);
	
	
	

}
