package com.cs.ordermanagement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.cs.ordermanagement.domain.Order;
import com.cs.ordermanagement.request.ExecutionReuest;
import com.cs.ordermanagement.util.TestDataLoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrdermanagementApplicationTests {

	private MockMvc mockMvc;


	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void prepareMockMvc() throws Exception {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
		this.mockMvc.perform(post("/v1/instruments/{instrumentName}", "IBM Ltd.")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/instruments/{instrumentName}", "HSBC Holdings.")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/instruments/{instrumentName}", "CS Ltd.")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/orderbooks/{instrumentId}",new Long(1))).andExpect(status().isCreated());
		List<Order> orders= TestDataLoder.addMarketOrders();
		//List<Order> limitOrders=TestDataLoder.createLimitOrders();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(orders);
		this.mockMvc.perform(post("/v1/orderbooks/{orderBookId}/orders",new Long(1)).contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().is(201));
		
		this.mockMvc.perform(put("/v1/orderbooks/{orderBookId}/closeorderbook", new Long(1))).andExpect(status().is(200));
		
		this.mockMvc.perform(post("/v1/orderbooks/{instrumentId}",new Long(2))).andExpect(status().isCreated());
		this.mockMvc.perform(put("/v1/orderbooks/{orderBookId}/closeorderbook", new Long(2))).andExpect(status().is(200));
		
		this.mockMvc.perform(post("/v1/orderbooks/{instrumentId}",new Long(3))).andExpect(status().isCreated());
		this.mockMvc.perform(put("/v1/orderbooks/{orderBookId}/closeorderbook", new Long(2))).andExpect(status().is(500));
		
		
		
	}

    @Test
	public void when_orderBookId_is_valid_then_executions_are_added_until_orderbook_is_executed() throws Exception {
		
		ExecutorService service = Executors.newCachedThreadPool();
		ExecutionReuest request = new ExecutionReuest(1l,5000l,new BigDecimal(100l));
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(request);
		
		
		for(int i=0;i<10;i++) {
		service.execute(()->{try {
			this.mockMvc.perform(post("/v1/orderbooks/{orderBookId}/executions",new Long(1)).contentType(MediaType.APPLICATION_JSON).content(json));
			this.mockMvc.perform(put("/v1/orderbooks/{orderBookId}/closeorderbook", new Long(1))).andExpect(status().is(500));
			this.mockMvc.perform(post("/v1/orderbooks/{orderBookId}/executions",new Long(1)).contentType(MediaType.APPLICATION_JSON).content(json));

			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}});
		}
	service.awaitTermination(10, TimeUnit.SECONDS);
	
    }	
}
