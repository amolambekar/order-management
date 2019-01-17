package com.cs.ordermanagement.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cs.ordermanagement.domain.Order;
import com.cs.ordermanagement.domain.Order.OrderType;

public class TestDataLoder {
	public static List<Order> addMarketOrders() {
	List<Order> orders = new ArrayList<>();
	Order order1 = new Order();
	order1.setOrderQuantity(5000l);
	order1.setOrderType(OrderType.MARKET);
	orders.add(order1);
	Order order2 = new Order();
	order2.setOrderQuantity(3000l);
	order2.setOrderType(OrderType.MARKET);
	orders.add(order2);
	Order order3 = new Order();
	order3.setOrderQuantity(2000l);
	order3.setOrderType(OrderType.MARKET);
	orders.add(order3);
	return orders;
	
	}
	
	public static  List<Order> createLimitOrders() {
		List<Order> orders = new ArrayList<>();
		Order order4 = new Order();
		order4.setOrderQuantity(5000l);
		order4.setEntryDate(new Date());
		order4.setOrderType(OrderType.LIMIT);
		orders.add(order4);
		
		Order order5 = new Order();
		order5.setOrderQuantity(3000l);
		order5.setOrderType(OrderType.LIMIT);
		orders.add(order5);
		
		Order order6 = new Order();
		order6.setOrderQuantity(1000l);
		order6.setOrderType(OrderType.LIMIT);
		orders.add(order6);
		return orders;
	}

}
