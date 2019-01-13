package com.cs.ordermanagement.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="ORDER_TABLE")
public class Order {
	

	
	public enum OrderType {
      MARKET,LIMIT
	}
	
	public enum OrderStatus{
		VALID,INVALID
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long orderId;
	
	@NotNull
	@Column
	private OrderType orderType;
	
	
    @Column(name="ORDER_BOOK_ID")
	private Long orderBookId;
    
    @Column
    private OrderStatus status;
	
	@NotNull
    @Column
	private Integer orderQuantity;
	
	   @CreationTimestamp
	   @Temporal(TemporalType.TIMESTAMP)
	   @Column
	   private Date entryDate;
	   
	   @CreationTimestamp
	   @Temporal(TemporalType.TIMESTAMP)
	   @Column
	   private Date modifiedDate;	
	   
	  
	/*@OneToOne
	private Instrument instrumentId;*/
	
	@Column
	private BigDecimal price;
	
	@Override
	public boolean equals(Object obj) {
		if(obj!=null) 
		return this.orderId.equals(((Order)obj).getOrderId());
		return false;
	}
	

	

}
