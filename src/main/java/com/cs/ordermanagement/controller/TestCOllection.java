package com.cs.ordermanagement.controller;

import java.util.Set;
import java.util.TreeSet;

public class TestCOllection {
	
	public static void main(String ... args) {
		TreeSet<String> treeSet = new TreeSet();
		
		treeSet.add("hello");
		treeSet.add("world");
		
		Set<String> mySet = treeSet;
		mySet.remove("hello");
		
		for(String str:treeSet) {
			System.out.println(str);
		}
		
		
		
		
	}

}
