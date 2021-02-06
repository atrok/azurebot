package com.poc.bot.Messages.Genesys;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.poc.bot.Message;

public class QuickReply implements Message{
/*
	{
	    "type": "Structured",
	    "text": "Will 3:00PM tomorrow work for you?",
	    "contentType": "quick-replies",
	    "content": [

	        {"id": "1", "type": "quick-reply", "action": "message","text": "Sounds Good."},
	        {"id": "2", "type": "quick-reply", "action": "message","text": "No, sorry."},
	        {"id": "3", "type": "quick-reply", "action": "message","text": "What else?"}
	    ]
	}
*/	
	
	public class ContentItem {
		
		private JsonObjectBuilder obj=Json.createObjectBuilder()
		.add("type", "quick-reply")
		.add("action", "message");
		
		public ContentItem(int id){
			obj.add("id", id);
		}
		
		public ContentItem addItem(String key, String value){
			obj.add(key,value);
			
			return this;
		}
		
		public JsonObjectBuilder get(){return obj;}
	}
	
	
	private JsonArrayBuilder content=Json.createArrayBuilder();
	private String type="Structured";
	private String contentType="quick-replies";
	private String action="message"; /// it's the only supported action type
	private  JsonObjectBuilder value;
	
	public QuickReply(){
		 value = Json.createObjectBuilder()
				 .add("type", type)
				 .add("contentType", contentType);
		 
	}
	
	public QuickReply addText(String msg){
		value.add("text", msg);
		return this;
	}
	
	public QuickReply addContent(ContentItem[] msgs){
		
		
		for (ContentItem item: msgs) {
			
			content.add(item.get());
			
		}
		
		return this;
		
	}
	
	public JsonObject getJsonObject(){
		
		return value.build();
	}
	
	
	@Override
	public String getPayload() {
		// TODO Auto-generated method stub
		return value.build().toString();
	}

}
