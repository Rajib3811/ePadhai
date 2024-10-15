package com.ePadhai.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class CustomMessage {

    private String message;
    private boolean isSuccess = false;
    private int statusCode;
	
    
    public CustomMessage() {
		super();
	}
	public CustomMessage(String message, boolean isSuccess, int statusCode) {
		super();
		this.message = message;
		this.isSuccess = isSuccess;
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	
    
    

}
