package com.cs.ordermanagement.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.ordermanagement.domain.Instrument;
import com.cs.ordermanagement.repository.InstrumentRepository;

@RestController
@RequestMapping("/v1/instruments")
public class InstrumentController {
	
	private InstrumentRepository instrumentRepository;
	
	public InstrumentController(@Autowired InstrumentRepository instrumentRepository) {
		this.instrumentRepository=instrumentRepository;
	}
	 
	@Transactional
	@PostMapping(path ="/{instrumentName}",produces = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Instrument> createInstrument(@PathVariable(name="instrumentName") String instrumentName){
		Instrument instrument = new Instrument(null,instrumentName);
		instrumentRepository.save(instrument);
		return new ResponseEntity<Instrument>(instrument,HttpStatus.CREATED);
	}
	
	

}
