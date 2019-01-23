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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.cs.ordermanagement.domain.OrderBook;
import com.cs.ordermanagement.domain.OrderBook.OrderBookStatus;
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
	private OrderBookService underTest;
	
    
	
	@Before
	public void setup() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    ReflectionTestUtils.setField(underTest, "orderBookRepository", orderBookRepository);
	    ReflectionTestUtils.setField(underTest, "executionRepository", executionRepository);
	}
	
	@Test
	public void testCreateandOpenOrderBook() {
		
		OrderBook orderBook =new OrderBook();
		orderBook.setOrderBookId(1l);
		ReentrantLock lock = new ReentrantLock();
		orderBook.setOrderBookStatus(OrderBookStatus.OPEN);
		when(orderBookRepository.save(any(OrderBook.class))).thenReturn(orderBook);
		underTest.createAndOpenNewOrderBook(1l);
		assertTrue(orderBook.getOrderBookStatus().equals(OrderBookStatus.OPEN));
		
	}

	

}
