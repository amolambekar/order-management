package com.cs.ordermanagement.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
import com.cs.ordermanagement.exception.ClosedOrderBookException;
import com.cs.ordermanagement.exception.ExecutionPriceMisMatchException;
import com.cs.ordermanagement.exception.OrderBookManagementException;
import com.cs.ordermanagement.repository.ExecutionRepository;
import com.cs.ordermanagement.repository.OrderBookRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderBookService {

	private OrderBookRepository orderBookRepository;

	private ExecutionRepository executionRepository;

	private static final long TIME_OUT = 500L;

	private static final TimeUnit TIME_UNIT_MILISECONDS = TimeUnit.MILLISECONDS;

	@Autowired
	public OrderBookService(OrderBookRepository orderBookRepository, ExecutionRepository executionRepository) {

		this.orderBookRepository = orderBookRepository;
		this.executionRepository = executionRepository;
	}

	@Transactional
	public OrderBook createAndOpenNewOrderBook(Long instrumentId) {
		Instrument instrument = new Instrument();
		instrument.setInstrumentId(instrumentId);
		OrderBook orderBook = new OrderBook(instrument, null, null, OrderBookStatus.OPEN);

		orderBook = orderBookRepository.save(orderBook);
		OrderBookRepository.orderBookLockMap.putIfAbsent(orderBook.getOrderBookId(), new ReentrantLock(true));
		return orderBook;
	}

	public Execution addExecutionToOrder(final Long orderBookId, final Long quantity, final BigDecimal price) {
		Execution execution = new Execution();
		return execution;

	}

	@Transactional
	public Execution addExecution(final Long orderBookId, final Long quantity, final BigDecimal price)
			throws OrderBookManagementException, ExecutionPriceMisMatchException {
		Execution execution = new Execution();
		ReentrantLock orderBookLock = OrderBook.getLock(orderBookId);

		try {

			if (orderBookLock.tryLock(TIME_OUT, TIME_UNIT_MILISECONDS)) {
				OrderBook orderBook = orderBookRepository.findOne(orderBookId);
				log.info("orderBook is found with status " + orderBook.getOrderBookStatus());
				if (orderBook.getOrderBookStatus().equals(OrderBookStatus.CLOSED) == false) {
					log.error("OrderBook status is " + orderBook.getOrderBookStatus() + ". Execution cannot be added");
					throw new OrderBookManagementException(
							"OrderBook status is " + orderBook.getOrderBookStatus() + ". Execution cannot be added");

				} else {
                    validateExecutionPrice(orderBook.getExecutions(),price);
					setOrderStatusValidity(orderBook, price);
					List<OrderExecution> orderExecutions = orderBook.getOrders().stream()
							.map(e -> e.getOrderExecution()).collect(Collectors.toList());

					List<OrderExecution> validOrderExecutions = orderExecutions.stream()
							.filter(e -> (e.getOrderStatus().equals(OrderStatus.VALID)) && e.getQuantity() > 0)
							.sorted((o1, o2) -> o2.getQuantity().compareTo(o1.getQuantity()))
							.collect(Collectors.toList());

					if (validOrderExecutions.size() > 0) {
						Long totalSum = validOrderExecutions.stream().mapToLong(e -> e.getQuantity()).reduce(Long::sum)
								.getAsLong();
						if (totalSum > 0)
							execution = applyExecution(quantity, orderBook, validOrderExecutions, price);
					}
				}
			}
		} catch (InterruptedException e1) {
			log.error("An error occured while adding execution to the orderBook", e1);
			throw new OrderBookManagementException(
					"An error occured while adding order, please contact system administartor");
		} finally {
			if (orderBookLock.isHeldByCurrentThread())
				orderBookLock.unlock();
		}

		return execution;

	}

	private void validateExecutionPrice(List<Execution> executions,BigDecimal price) throws ExecutionPriceMisMatchException {
		
		if(executions!=null && executions.size()>0) {
			if(executions.get(0).getPrice()!=price) {
				throw new ExecutionPriceMisMatchException("Requested xecution price does not match with existing execution price");
			}
		}
			
	
		
	}

	private void updateOrderExecution(final BigDecimal price, Order e) {
		if (e.getOrderType() == OrderType.LIMIT && e.getPrice().compareTo(price) < 0)
			e.getOrderExecution().setOrderStatus(OrderStatus.INVALID);
		e.getOrderExecution().setOrderStatus(OrderStatus.VALID);
	}

	private Execution applyExecution(final Long quantity, final OrderBook orderBook,
			final List<OrderExecution> validOrderExecutions, final BigDecimal price) {
		Long executionQuantity = quantity;
		while (executionQuantity > 0) {
			executionQuantity = applyExecution(validOrderExecutions, executionQuantity);
		}

		Execution execution = new Execution();
		execution.setOrderBookId(orderBook.getOrderBookId());
		execution.setPrice(price);
		execution.setQuantity(quantity);
		orderBook.getExecutions().add(execution);
		Long totalExecutionQuantity = orderBook.getExecutions().stream().parallel().mapToLong(e -> e.getQuantity())
				.reduce(Long::sum).getAsLong();
		Long totalSum = validOrderExecutions.stream().mapToLong(e -> e.getQuantity() + e.getExecutedQuantity())
				.reduce(Long::sum).getAsLong();
		if (totalExecutionQuantity.equals(totalSum)) {
			log.info("setting order as executed");
			orderBook.setOrderBookStatus(OrderBookStatus.EXECUTED);
		}
		orderBookRepository.saveAndFlush(orderBook);
		executionRepository.saveAndFlush(execution);

		return execution;
	}

	private Long applyExecution(final List<OrderExecution> validOrderExecutions, Long executionQuantity) {
		Long totalDistributedQuantity = 0l;
		Long intermediateSum = validOrderExecutions.stream().map(e -> e.getQuantity()).reduce(Long::sum).get();
		if (intermediateSum > 0) {
			for (OrderExecution orderExecution : validOrderExecutions) {
				Long orderQuantity = orderExecution.getQuantity();
				Long distributedQuantity = (orderQuantity * executionQuantity) / intermediateSum;

				if (distributedQuantity > 0) {
					orderExecution.setQuantity(orderQuantity - distributedQuantity);
					Long executedQuantity = orderExecution.getExecutedQuantity();
					if (executedQuantity == null)
						orderExecution.setExecutedQuantity(distributedQuantity);
					orderExecution.setExecutedQuantity(orderExecution.getExecutedQuantity() + distributedQuantity);
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
				log.info("     " + orderExecution.getOrderId().getOrderId() + "             " + distributedQuantity
						+ "            " + orderExecution.getQuantity());
			}
		}

		executionQuantity = executionQuantity - totalDistributedQuantity;
		return executionQuantity;
	}

	private void setOrderStatusValidity(OrderBook orderBook, BigDecimal price) {
		orderBook.getOrders().stream().forEach(e -> {

			updateOrderExecution(price, e);
			e.getOrderExecution().setExecutionPrice(price);

		});
	}

	@Transactional
	public void closeOrderBook(final Long orderBookId) throws ClosedOrderBookException, OrderBookManagementException {

		ReentrantLock orderBookLock = (ReentrantLock) (OrderBook.getLock(orderBookId));
		try {
			if (orderBookLock.tryLock(TIME_OUT, TIME_UNIT_MILISECONDS)) {

				OrderBook orderBook = orderBookRepository.findOne(orderBookId);
				OrderBookStatus status = orderBook.getOrderBookStatus();
				if (status.equals(OrderBookStatus.EXECUTED) || status.equals(OrderBookStatus.CLOSED)) {
					log.error("Orderbook  status is already " + status);
					throw new ClosedOrderBookException("the orderBook is already closed");
				} else {
					orderBook.setOrderBookStatus(OrderBookStatus.CLOSED);
					orderBookRepository.saveAndFlush(orderBook);
				}
			}
		} catch (InterruptedException e) {
			throw new OrderBookManagementException(
					"An error occured while adding order, please contact system administartor");
		} finally {
			if (orderBookLock.isHeldByCurrentThread()) {
				orderBookLock.unlock();
			}

		}

	}

	public void addOrders(final Long orderBookId, final List<Order> orders) throws OrderBookManagementException {

		ReentrantLock orderBookLock = OrderBook.getLock(orderBookId);
		try {
			if (orderBookLock.tryLock(TIME_OUT, TIME_UNIT_MILISECONDS)) {
				OrderBook orderBook = orderBookRepository.findOne(orderBookId);

				if (orderBook.getOrderBookStatus().equals(OrderBookStatus.OPEN)) {
					for (Order order : orders) {
						order.getOrderExecution().setQuantity(order.getOrderQuantity());
						order.getOrderExecution().setOrderId(order);
					}
					orderBook.getOrders().addAll(orders);
					orderBookRepository.save(orderBook);
				}
			}
		} catch (InterruptedException e1) {
			throw new OrderBookManagementException(
					"An error occured while adding order, please contact system administartor");
		} finally {
			if (orderBookLock.isHeldByCurrentThread())
				orderBookLock.unlock();
		}
	}

}