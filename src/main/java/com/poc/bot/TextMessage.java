package com.poc.bot;

public class TextMessage implements Message {

	private String payload;
	public TextMessage(String payload){
		this.payload=payload;
	}
	@Override
	public String getPayload() {
		// TODO Auto-generated method stub
		return this.payload;
	}

}
