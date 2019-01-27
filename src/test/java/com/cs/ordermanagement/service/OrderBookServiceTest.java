package com.cs.ordermanagement.service;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.concurrent.locks.ReentrantLock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.cs.ordermanagement.domain.OrderBook;
import com.cs.ordermanagement.domain.OrderBook.OrderBookStatus;
import com.cs.ordermanagement.exception.ClosedOrderBookException;
import com.cs.ordermanagement.exception.OrderBookManagementException;
import com.cs.ordermanagement.repository.ExecutionRepository;
import com.cs.ordermanagement.repository.OrderBookRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderBookServiceTest {
	
	
	
	@Mock
    private	OrderBookRepository orderBookRepository;
	
	@Mock
	private ExecutionRepository executionRepository;
	
	@Autowired
	@InjectMocks
	private OrderBookService underTest;
	
    
	
	@Before
	public void setup() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    ReflectionTestUtils.setField(underTest, "orderBookRepository", orderBookRepository);
	    ReflectionTestUtils.setField(underTest, "executionRepository", executionRepository);
	}
	
	@Test
	public void testCreateandOpenOrderBook() {
		OrderBook orderBook =  Mockito.spy(OrderBook.class);
		when(orderBook.getOrderBookId()).thenReturn(1l);
		when(orderBookRepository.save(any(OrderBook.class))).thenReturn(orderBook);
		
		underTest.createAndOpenNewOrderBook(1l);
		Mockito.verify(orderBookRepository,Mockito.times(1)).save(any(OrderBook.class));
		
	}
	
	
	@Test
	public void testCLoseOrderBook() throws ClosedOrderBookException, OrderBookManagementException {
		
		OrderBook orderBook =  Mockito.spy(OrderBook.class);
		when(orderBook.getOrderBookId()).thenReturn(1l);
		OrderBookRepository.orderBookLockMap.put(1l, new ReentrantLock());
		orderBook.setOrderBookStatus(OrderBookStatus.OPEN);
		
		when(orderBookRepository.findOne(1l)).thenReturn(orderBook);
		underTest.closeOrderBook(1l);
		Mockito.verify(orderBookRepository,Mockito.times(1)).saveAndFlush(any(OrderBook.class));
		assertTrue(orderBook.getOrderBookStatus().equals(OrderBookStatus.CLOSED));
		
	}
	
	@Test(expected=ClosedOrderBookException.class)
public void testCLoseOrderBookForException() throws ClosedOrderBookException, OrderBookManagementException {
		
		OrderBook orderBook =  Mockito.spy(OrderBook.class);
		when(orderBook.getOrderBookId()).thenReturn(1l);
		orderBook.setOrderBookStatus(OrderBookStatus.CLOSED);
		OrderBookRepository.orderBookLockMap.put(1l, new ReentrantLock());
		
		when(orderBookRepository.findOne(1l)).thenReturn(orderBook);
		underTest.closeOrderBook(1l);
		Mockito.verify(orderBookRepository,Mockito.times(0)).saveAndFlush(any(OrderBook.class));
	
		
	}
	

	

}
