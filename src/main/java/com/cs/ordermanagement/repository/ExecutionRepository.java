package com.cs.ordermanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cs.ordermanagement.domain.Execution;

public interface ExecutionRepository extends JpaRepository<Execution, Long>{
	
	@Query("select count(exe.quantity),exe.price from Execution exe where exe.orderBookId=:orderBookId")
	List<Object> findCountOrderQuantityByOrderBookIdGroupByPrice(Long orderBookId);

}
