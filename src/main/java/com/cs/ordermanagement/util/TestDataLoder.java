package com.cs.ordermanagement.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cs.ordermanagement.domain.Instrument;
import com.cs.ordermanagement.domain.Order;
import com.cs.ordermanagement.domain.Order.OrderType;

public class TestDataLoder {
	public static List<Order> addMarketOrders() {
	List<Order> orders = new ArrayList<>();
	Order order1 = new Order(OrderType.MARKET,5000l,new BigDecimal(100l),new Instrument(1l,"IBM Ltd."));

	orders.add(order1);
	Order order2 = new Order(OrderType.MARKET,3000l,new BigDecimal(100l),new Instrument(1l,"IBM Ltd."));
	
	orders.add(order2);
	Order order3 = new Order(OrderType.MARKET,2000l,new BigDecimal(100l),new Instrument(1l,"IBM Ltd."));
	
	orders.add(order3);
	return orders;
	
	}
	
	public static  List<Order> createLimitOrders() {
		List<Order> orders = new ArrayList<>();
		Order order4 =new Order(OrderType.LIMIT,5000l,new BigDecimal(100l),new Instrument(1l,"IBM Ltd."));		
		orders.add(order4);		
		Order order5 = new Order(OrderType.LIMIT,3000l,new BigDecimal(100l),new Instrument(1l,"IBM Ltd."));		
		orders.add(order5);		
		Order order6 = 
		new Order(OrderType.LIMIT,2000l,new BigDecimal(100l),new Instrument(1l,"IBM Ltd."));
		orders.add(order6);
		return orders;
	}

}
