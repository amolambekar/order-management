package com.cs.ordermanagement.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs.ordermanagement.domain.Execution;
import com.cs.ordermanagement.domain.Instrument;
import com.cs.ordermanagement.domain.Order;
import com.cs.ordermanagement.domain.Order.OrderType;
import com.cs.ordermanagement.domain.OrderBook;
import com.cs.ordermanagement.domain.OrderBook.OrderBookStatus;
import com.cs.ordermanagement.domain.OrderExecution;
import com.cs.ordermanagement.domain.OrderExecution.OrderStatus;
import com.cs.ordermanagement.repository.ExecutionRepository;
import com.cs.ordermanagement.repository.OrderBookRepository;
import com.cs.ordermanagement.repository.OrderRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderBookService {

	public OrderBookRepository orderBookRepository;

	@Autowired
	public ExecutionRepository executionRepository;

	private static final long TIME_OUT = 500L;

	private static final TimeUnit TIME_UNIT_MILISECONDS = TimeUnit.MILLISECONDS;

	@Autowired
	public OrderBookService(OrderBookRepository orderBookRepository) {

		this.orderBookRepository = orderBookRepository;
	}

	@Transactional
	public OrderBook createAndopenNewWorkBook(Long instrumentId) {
		Instrument instrument = new Instrument();
		instrument.setInstrumentId(instrumentId);
		OrderBook orderBook = new OrderBook(instrument, null, null, OrderBookStatus.OPEN);

		orderBook = orderBookRepository.save(orderBook);
		OrderBookRepository.orderBookLockMap.putIfAbsent(orderBook.getOrderBookId(), new ReentrantLock(true));
		return orderBook;
	}

	@Transactional
	public Execution addExecution(final Long orderBookId, final Long quantity, final BigDecimal price) {

		Execution execution = null;
		ReentrantLock orderBookLock = OrderBook.getLock(orderBookId);
		log.info(Thread.currentThread() + "calling service method");
		try {
			orderBookLock.tryLock(TIME_OUT, TIME_UNIT_MILISECONDS);
			OrderBook orderBook = orderBookRepository.findOne(orderBookId);
			

			log.info(Thread.currentThread() + "orderBook is found with status " + orderBook.getOrderBookStatus());
			if (orderBook.getOrderBookStatus().equals(OrderBookStatus.CLOSED)) {

				setOrderStatusValidity(orderBook, price);
				List<OrderExecution> validOrderExecutions = 
						orderBook.getOrders().stream().
						peek(e -> {

							if (e.getOrderType() == OrderType.LIMIT && e.getPrice().compareTo(price) < 0)
								e.getOrderExecution().setOrderStatus(OrderStatus.INVALID);
							e.getOrderExecution().setOrderStatus(OrderStatus.VALID);})
						
						
						.filter(e -> (e.getOrderExecution().getOrderStatus().equals(OrderStatus.VALID))
								&& e.getOrderExecution().getQuantity() > 0)
						.map(e -> e.getOrderExecution())
						.sorted((o1, o2) -> o2.getQuantity().compareTo(o1.getQuantity())).collect(Collectors.toList());
				if (validOrderExecutions.size() > 0) {
					Long totalSum = validOrderExecutions.stream().map(e -> e.getQuantity()).reduce(Long::sum).get();
					log.info("total sum ****************************************"+totalSum);
					// log.info("total valid sum is "+totalSum+ " "+execution.getQuantity());
					if (quantity.equals(totalSum)) {
						// log.info("setting order as executed");
						orderBook.setOrderBookStatus(OrderBookStatus.EXECUTED);
					}
					// log.info("start distribution");
					if(totalSum>0)
					execution = applyExecution(quantity, orderBook, validOrderExecutions, price);
				}
			}else {
				log.info("OrderBook status is "+orderBook.getOrderBookStatus()+". Execution cannot be added");
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (orderBookLock.isHeldByCurrentThread())
				orderBookLock.unlock();
		}

		return execution;

	}

	private Execution applyExecution(final Long quantity, final OrderBook orderBook,
			final List<OrderExecution> validOrderExecutions, final BigDecimal price) {
		Long executionQuantity = quantity;
		while (executionQuantity > 0) {
			Long totalDistributedQuantity = 0l;
			Long intermediateSum = validOrderExecutions.stream().map(e -> e.getQuantity()).reduce(Long::sum).get();
			if (intermediateSum > 0) {
				for (OrderExecution orderExecution : validOrderExecutions) {
					Long orderQuantity = orderExecution.getQuantity();
					Long distributedQuantity = (orderQuantity * executionQuantity) / intermediateSum;

					if (distributedQuantity > 0) {
						orderExecution.setQuantity(orderQuantity - distributedQuantity);
						totalDistributedQuantity = totalDistributedQuantity + distributedQuantity;

					} else {
						OrderExecution largestOrder = validOrderExecutions.get(0);
						Long largestOrderCurrentQuantity = largestOrder.getQuantity();
						largestOrder.setQuantity(largestOrderCurrentQuantity - executionQuantity);
						totalDistributedQuantity = totalDistributedQuantity + executionQuantity;
						log.info(Thread.currentThread() + " orderId   distributed quantity  remainingQuantity ");
						log.info(Thread.currentThread() + "     " + largestOrder.getOrderId().getOrderId().longValue()
								+ "             " + executionQuantity + "            " + largestOrder.getQuantity());
						break;
					}

					log.info(" orderId   distributed quantity  remainingQuantity ");
					log.info("     " + orderExecution.getOrderId().getOrderId() + "             " + orderExecution.getQuantity()
							+ "            " + orderExecution.getQuantity());
				}
			}

			executionQuantity = executionQuantity - totalDistributedQuantity;

		}
		log.info("final remaining execution quantity " + executionQuantity);

		Execution execution = new Execution();
		execution.setOrderBookId(orderBook.getOrderBookId());
		execution.setPrice(price);
		execution.setQuantity(quantity);
		orderBook.getExecutions().add(execution);
		orderBookRepository.save(orderBook);
		executionRepository.save(execution);

		System.out.println("execution id is %%%%% " + execution.getExecutionId());
		return execution;
	}

	private void setOrderStatusValidity(OrderBook orderBook, BigDecimal price) {
		orderBook.getOrders().stream().forEach(e -> {

			if (e.getOrderType() == OrderType.LIMIT && e.getPrice().compareTo(price) < 0)
				e.getOrderExecution().setOrderStatus(OrderStatus.INVALID);
			e.getOrderExecution().setOrderStatus(OrderStatus.VALID);
			e.getOrderExecution().setExecutionPrice(price);

		});
	}

	@Transactional
	public void updateOrderBookStatus(final Long orderBookId, final String status) {
		

		ReentrantLock orderBookLock = (ReentrantLock) (OrderBook.getLock(orderBookId));
		try {
			orderBookLock.tryLock(TIME_OUT, TIME_UNIT_MILISECONDS);
			OrderBook orderBook = orderBookRepository.findOne(orderBookId);

			if (orderBook.getOrderBookStatus().equals(OrderBookStatus.EXECUTED) == false) {
				orderBook.setOrderBookStatus(OrderBookStatus.valueOf(status));
				orderBookRepository.save(orderBook);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (orderBookLock.isHeldByCurrentThread())
				orderBookLock.unlock();

		}

	}

	public void addOrders(final Long orderBookId, final List<Order> orders) {

		
		ReentrantLock orderBookLock = OrderBook.getLock(orderBookId);
		try {
			log.info(Thread.currentThread() + "calling addOrders method");
			orderBookLock.tryLock(TIME_OUT, TIME_UNIT_MILISECONDS);
			OrderBook orderBook = orderBookRepository.findOne(orderBookId);

			if (orderBook.getOrderBookStatus().equals(OrderBookStatus.OPEN)) {
				for (Order order : orders)
					order.getOrderExecution().setQuantity(order.getOrderQuantity());
				orderBook.getOrders().addAll(orders);
				orderBookRepository.save(orderBook);
				orderBook.getOrders().stream().forEach(e -> System.out.println(e.getOrderId()));
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (orderBookLock.isHeldByCurrentThread())
				orderBookLock.unlock();
		}
	}

}