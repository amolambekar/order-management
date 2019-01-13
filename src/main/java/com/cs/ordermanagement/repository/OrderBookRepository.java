package com.cs.ordermanagement.repository;

import javax.persistence.LockModeType;

import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.cs.ordermanagement.domain.OrderBook;
import com.cs.ordermanagement.domain.OrderBook.Status;

@Import({springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration.class})
@RepositoryRestResource(collectionResourceRel = "orderBooks", path = "orderBooks")
public interface OrderBookRepository extends JpaRepository<OrderBook, Long>{
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	OrderBook findOne(Long orderBookId);
	
	
	
	 OrderBook save(OrderBook orderBook);
	 
	
	 @Modifying
	 @Query("UPDATE OrderBook SET status= :status where orderBookId=:id") 
	 void update(@Param("status") Status status,@Param("id") Long id);
	 
	 

}
