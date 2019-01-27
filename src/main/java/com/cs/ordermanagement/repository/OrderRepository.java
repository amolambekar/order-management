package com.cs.ordermanagement.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.cs.ordermanagement.domain.Order;

@RepositoryRestResource(collectionResourceRel = "orders", path = "orders")
public interface OrderRepository extends JpaRepository<Order, Long>{
	
	
	@Override
	  @RestResource(exported = false)
	  void delete(Long id);

	  @Override
	  @RestResource(exported = false)
	  void delete(Order entity);
	  
	 
	
	//amount of orders in Each Book
	 @RestResource(exported = true)
	Long countByOrderBookId(@Param("orderBookId")Long orderBookId);
	
	//biggest order by price
	 @RestResource(exported = true)
	Order findFirstByOrderBookIdOrderByPriceDesc(@Param( "orderBookId" ) Long orderBookId);
	
	//demand =accumulated quantity
	 @RestResource(exported = true)
	@Query("select ord.orderId,ord.orderType, ord.orderQuantity from Order ord inner join ord.orderExecution orderExecution where orderExecution.orderStatus = 'VALID' and ord.orderBookId=:orderBookId")
	Long getValidDemandbyOrderBookId(@Param("orderBookId")  Long orderBookId);
	
	 @RestResource(exported = true)
	@Query("select ord.orderId,ord.orderType, ord.orderQuantity from Order ord inner join ord.orderExecution orderExecution where orderExecution.orderStatus = 'INVALID' and ord.orderBookId=:orderBookId")
	Long getInValidDemandbyOrderBookId(@Param("orderBookId")  Long orderBookId);
	
	
	//smallest order by price
	 @RestResource(exported = true)
	Order findFirstByOrderBookIdOrderByPriceAsc(@Param("orderBookId") Long orderBookId);
	
	 @RestResource(exported = true)
	@Query("select count(ord.orderQuantity),ord.price from Order ord where ord.orderType='LIMIT' and ord.orderBookId=:orderBookId group by ord.price")
	Map<BigDecimal,Long> findCountOrderQuantityByOrderBookIdGroupByPrice(@Param("orderBookId")  Long orderBookId);
	
	 @RestResource(exported = true)
	@Query("select ord.orderExecution.orderStatus, ord.orderExecution.quantity ,ord.price, ord.orderExecution.executionPrice from Order ord where ord.orderId =:orderId")
	List<Object> findOrderStatusAndOrderExecutionQuantityandOrderPriceAndOrderExecutionPriceByOrderId(@Param("orderId") Long orderId);
	
	 
		
	
	
	

}
