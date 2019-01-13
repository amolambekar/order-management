package com.cs.ordermanagement.controller;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.ordermanagement.InstrumentDAO;
import com.cs.ordermanagement.domain.Instrument;

@RestController
@RequestMapping("/v1/instruments")
public class InstrumentController {
	
	private InstrumentDAO instrumentDAO;
	
	public InstrumentController(@Autowired InstrumentDAO instrumentDAO) {
		this.instrumentDAO=instrumentDAO;
	}
	 
	@Transactional
	@PostMapping(path ="/{instrumentName}",produces = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Instrument> createInstrument(@PathVariable(name="instrumentName") String instrumentName){
		Instrument instrument = new Instrument(null,instrumentName);
		instrumentDAO.save(instrument);
		return new ResponseEntity<Instrument>(instrument,HttpStatus.CREATED);
	}
	
	/*@GetMapping(path="/{instrumentId}",produces=MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Instrument> findInstrument(@PathVariable(name="instrumentId") Long instrumentId){
		Instrument instrument = instrumentDAO.findOne(instrumentId);
		return new ResponseEntity<Instrument>(instrument,HttpStatus.OK);
		
	}
	
	
	@GetMapping(path="/{instrumentName}",produces=MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Instrument> findInstrumentByName(@PathVariable(name="instrumentName") String instrumentName){
		Instrument instrument = instrumentDAO.findByName(instrumentName);
		return new ResponseEntity<Instrument>(instrument,HttpStatus.OK);
		
	}*/

}
