package com.cs.ordermanagement.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Required;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionReuest {
	
	@NotNull
	private Long orderBookId;
	
	@NotNull
	private Long quantity;
	
	@NotNull
	private BigDecimal price;
	

}
