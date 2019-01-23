package com.cs.ordermanagement.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name="ORDER_TABLE")
public class Order {
	
	public Order(OrderType orderType,Long orderQuantity,BigDecimal price,Instrument instrumentId ) {
		this.orderType=orderType;
		this.orderQuantity=orderQuantity;
		this.price=price;
		this.instrumentId=instrumentId;
		this.orderExecution= new OrderExecution();
	}

	
	public enum OrderType {
      MARKET,LIMIT
	}
	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long orderId;
	
	@NotNull
	@Column
	private OrderType orderType;
	
	
    @Column(name="ORDER_BOOK_ID")
	private Long orderBookId;
    
 
	@NotNull
    @Column
	private Long orderQuantity;
	
	
	
	@OneToOne(fetch = FetchType.EAGER, optional = false,cascade=CascadeType.ALL)
	@JoinColumn(name="OrderExecutionId")
	private OrderExecution orderExecution;
	
	
	   @JsonIgnore
	   @CreationTimestamp
	   @Temporal(TemporalType.TIMESTAMP)
	   @Column
	   private Date entryDate;
	   
	   @JsonIgnore
	   @CreationTimestamp
	   @Temporal(TemporalType.TIMESTAMP)
	   @Column
	   private Date modifiedDate;	
	   
	  
	@OneToOne
	private Instrument instrumentId;
	
	@NotNull
	@Column
	private BigDecimal price;
	
	@Override
	public boolean equals(Object obj) {
		if(obj!=null) 
		return this.orderId.equals(((Order)obj).getOrderId());
		return false;
	}
	

	

}
