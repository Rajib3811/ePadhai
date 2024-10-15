package com.ePadhai.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class CustomMessage {

    private String message;
    private boolean isSuccess = false;
    private int statusCode;
	public CustomMessage(String message, boolean isSuccess, int statusCode) {
		super();
		this.message = message;
		this.isSuccess = isSuccess;
		this.statusCode = statusCode;
	}
    
    

}
