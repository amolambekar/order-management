package com.cs.ordermanagement.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.cs.ordermanagement.domain.OrderBook;
import com.cs.ordermanagement.domain.OrderBook.OrderBookStatus;

@Import({springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration.class})
@RepositoryRestResource(collectionResourceRel = "orderBooks", path = "orderBooks")
public interface OrderBookRepository extends JpaRepository<OrderBook, Long>{
	
	static final Map<Long,ReentrantLock> orderBookLockMap = new ConcurrentHashMap<>();
	
	
	OrderBook findOne(Long orderBookId);
	
	
	
	 OrderBook save(OrderBook orderBook);
	 
	
	 @Modifying
	 @Query("UPDATE OrderBook SET status= :status where orderBookId=:id") 
	 void update(@Param("status") OrderBookStatus status,@Param("id") Long id);
	 
	 
	 
	 
	 
	 

}
