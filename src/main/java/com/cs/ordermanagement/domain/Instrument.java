package com.cs.ordermanagement.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Instrument implements Serializable{
	
	
	public Instrument(String instrumentName) {
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = -1434276552156346862L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long instrumentId;
	
	
	@Column(unique=true  )
	private String name;
	
	

}
