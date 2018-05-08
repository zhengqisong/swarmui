package com.apigate.swarmui.model;

import java.util.Date;

public class Result {
	Date timestamp;
	int status;
	String error;
	Object message;
	
	public static String SUCCESS = "success";
	public static String FAILURE = "failure";
	
	public Result(){
		timestamp = new Date();
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
	
	
	
	
}
