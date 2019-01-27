package com.cs.ordermanagement.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.cs.ordermanagement.repository.OrderBookRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor

@RepositoryRestResource(collectionResourceRel = "orderBooks", path = "orderBooks")
public class OrderBook implements Serializable {

	
	private static final long serialVersionUID = -4514295710367090347L;
	

	public OrderBook (Instrument instrument,List<Order>orders,List<Execution>executions,OrderBookStatus status) {
		this.instrumentId=instrument;
		this.orders=orders;
		this.executions=executions;
		this.orderBookStatus=status;
	}
    
	
	public static final ReentrantLock getLock(Long orderBookId) { return OrderBookRepository.orderBookLockMap.get(orderBookId);}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long orderBookId;

	@NotNull
	@OneToOne(fetch = FetchType.EAGER, optional = false)
	private Instrument instrumentId;

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.EAGER )
	@JoinColumn(name = "order_book_id")
	private List<Order> orders = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name = "order_book_id")
	private List<Execution> executions=new ArrayList<>();

	public enum OrderBookStatus {
		OPEN, CLOSED, EXECUTED
	};

	@NotNull
	@Column
	private OrderBookStatus orderBookStatus;
	
	
	



   @CreationTimestamp
   @Temporal(TemporalType.TIMESTAMP)
   @Column
   private Date createDate;
   
   @CreationTimestamp
   @Temporal(TemporalType.TIMESTAMP)
   @Column
   private Date modifiedDate;
   
   public boolean equals(Object orderBook) {
	   if(orderBook!=null && orderBook instanceof OrderBook) {
		   return this.orderBookId.equals(((OrderBook)orderBook).getOrderBookId());
	   }
	   return false;
	   
   }
   
   @Override
   public int hashCode() {
       return Objects.hashCode(this.orderBookId);
   }
	
	
}
