package com.cs.ordermanagement.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.cs.ordermanagement.domain.Order.OrderStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class OrderBook implements Serializable {

	private static final long serialVersionUID = 8513860251912363310L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long orderBookId;

	@NotNull
	@OneToOne(fetch = FetchType.EAGER, optional = false)
	private Instrument instrumentId;

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name = "order_book_id")
	private Set<Order> orders = new TreeSet<>((o1,o2)->o2.getOrderQuantity().compareTo(o1.getOrderQuantity()));

	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name = "order_book_id")
	private List<Execution> executions;

	public enum Status {
		OPEN, CLOSED, EXECUTED
	};

	@NotNull
	@Column
	private Status status;
	
	@Version
	private Long version;
	



   @CreationTimestamp
   @Temporal(TemporalType.TIMESTAMP)
   @Column
   private Date createDate;
   
   @CreationTimestamp
   @Temporal(TemporalType.TIMESTAMP)
   @Column
   private Date modifiedDate;
	
	
}
