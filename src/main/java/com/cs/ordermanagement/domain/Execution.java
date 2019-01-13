package com.cs.ordermanagement.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Execution implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7059165426884645187L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long executionId;
	
	@Column
	private Integer quantity;
	
	@Column
	private BigDecimal price;
	
	@Column(name="ORDER_BOOK_ID")
	private Long orderBookId;

}
