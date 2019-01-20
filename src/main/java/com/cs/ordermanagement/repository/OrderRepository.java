package com.cs.ordermanagement.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cs.ordermanagement.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
	
	
	
	//amount of orders in Each Book
	Long countByOrderBookId(Long orderBookId);
	
	//biggest order by price
	Order findFirstByOrderBookIdOrderByPriceDesc(Long orderBookId);
	
	//demand =accumulated quantity
	@Query("select ord.orderId,ord.orderType, ord.orderQuantity from Order ord inner join ord.orderExecution orderExecution where orderExecution.orderStatus = 'VALID' and ord.orderBookId=:orderBookId")
	Long getValidDemandbyOrderBookId(Long orderBookId);
	
	@Query("select ord.orderId,ord.orderType, ord.orderQuantity from Order ord inner join ord.orderExecution orderExecution where orderExecution.orderStatus = 'INVALID' and ord.orderBookId=:orderBookId")
	Long getInValidDemandbyOrderBookId(Long orderBookId);
	
	
	//smallest order by price
	Order findFirstByOrderBookIdOrderByPriceAsc(Long orderBookId);
	
	@Query("select count(ord.orderQuantity),ord.price from Order ord where ord.orderType='LIMIT' and ord.orderBookId=:orderBookId group by ord.price")
	Map<BigDecimal,Long> findCountOrderQuantityByOrderBookIdGroupByPrice(Long orderBookId);
	
	
	@Query("select ord.orderExecution.orderStatus, ord.orderExecution.quantity ,ord.price, ord.orderExecution.executionPrice from Order ord where ord.orderId =:orderId")
	List<Object> findOrderStatusAndOrderExecutionQuantityandOrderPriceAndOrderExecutionPriceByOrderId(Long orderId);
	
	 
		
	
	
	

}
