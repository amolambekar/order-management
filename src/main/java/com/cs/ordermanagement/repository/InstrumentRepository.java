package com.cs.ordermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cs.ordermanagement.domain.Instrument;

@Repository
public interface InstrumentRepository  extends JpaRepository<Instrument, Long>{
	 
	Instrument findByName(String instrumentName);

	
	
	

}
