package com.cs.ordermanagement.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class OrderExecution {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long OrderExecutionId;
	
	
	@OneToOne(fetch = FetchType.EAGER, optional = false,mappedBy="orderExecution")
	private Order orderId;
	
	@Column
	private Long quantity;
	
	@Column
	private BigDecimal executionPrice;
	
	public enum OrderStatus{
		VALID,INVALID
	}
	
	
	@Column	
	private OrderStatus orderStatus;
	
	
	
	
 

}
