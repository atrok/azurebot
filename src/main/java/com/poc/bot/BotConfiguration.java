package com.poc.bot;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.genesyslab.chat.bots.chatbotapi.platform.KeyValueMap;
import com.genesyslab.chat.bots.chatbotapi.platform.KeyValueMap.ValueHolder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 * {
DirectLine :{
    secret: ,
    endpoints:{
        startconversation: { 
        	url: "https://directline.botframework.com/v3/directline/conversations", 
        	method: "POST" }
        sendactivity: {
        	url: "https://directline.botframework.com/v3/directline/conversations/%conversationId%/activities", 
        	method: "POST"}
        getactivities: {
        	url: "https://directline.botframework.com/v3/directline/conversations/%conversationId%/activities?watermark=%latest_activityId%", 
        	method: "GET"
        	}
        }
    }
}
 */
public class BotConfiguration {
	
    private final Logger theLogger; // bot-session aware logger

    public BotConfiguration(Logger logger) {
    	if(null!=logger)        theLogger = logger;
    	else
    		theLogger=(Logger) java.util.logging.Logger.getLogger(BotConfiguration.class.getName());
    }
    
    
public enum DirectLineEndpoint{
	STARTCONVERSATION,
	SENDACTIVITY,
	GETACTIVITIES
}

public enum HTTPMETHOD {
	GET,
	POST,
	PUT,
	UPDATE,
	DELETE
}

    public static class Endpoint {
        public DirectLineEndpoint endpoint;

        public String url;
        public HTTPMETHOD method;

        
        // copy properties of another endpoint configuration into a new Endpoint instance;
        public Endpoint from(Endpoint e){
        	
        	this.url=e.url;
        	this.endpoint=e.endpoint;
        	this.method=e.method;
        	return this;
        }
        
        private void setUrl(String itemSr) throws Exception{
        	if (null != itemSr) {

                url = itemSr;
            }else throw new Exception("Missing value for configuration item");
        	
        }
        private void setMethod(String itemSr) throws Exception{
        	
        	if (itemSr==null) throw new Exception("Missing value for configuration item");
        	
        	method = HTTPMETHOD.valueOf(itemSr);
        	
        	if (method.name().length()==0) throw new Exception("HTTP Method `"+itemSr+"` is not found in HTTPMETHOD Enum");
        	
        }
    }

    public final Map<DirectLineEndpoint,Endpoint> DirectLineEndpoints = new HashMap<>();
	public String directlinesecret;
	public String welcome;
	public String botname;

    public void parseConfiguration(KeyValueMap configuration) {

        // reading DirectLine configuration  from configuration file
        try {


            ValueHolder config = configuration.get("DirectLine"); // find prompts node
            if (null != config && config.getValueType().equals(KeyValueMap.ValueType.KV_MAP)) {

                KeyValueMap configEntities = config.getAsKeyValueMap();
                // for every prompt we read it content
                directlinesecret=configEntities.get("secret").getAsString();
                welcome=configEntities.get("welcomeMessage").getAsString();
                botname=configEntities.get("botservicename").getAsString();
                
                
                KeyValueMap endpoints= configEntities.get("endpoints").getAsKeyValueMap();
                
                endpoints.entrySet().forEach((_item) -> {
                   try { 
                	   ValueHolder entity = _item.getValue();
                    if (entity.getValueType().equals(KeyValueMap.ValueType.KV_MAP)) {
                        // this is the Directline endpoint
                    	Endpoint newEndpoint = new Endpoint();
                        newEndpoint.endpoint = DirectLineEndpoint.valueOf(_item.getKey());

                        KeyValueMap listProperties = entity.getAsKeyValueMap();
                        
                        
							newEndpoint.setUrl(listProperties.get("url").getAsString());

                        newEndpoint.setMethod(listProperties.get("method").getAsString());
									DirectLineEndpoints.put(
											newEndpoint.endpoint, newEndpoint);
                    }
                        
						} catch (Exception ex) {
							// TODO Auto-generated catch block

							theLogger.error(ex.toString());
							
						}

                    
                });
            } //
        } catch (Exception ex) {
            theLogger.error(ex.toString());
        }
    }
    
    // for testing as there is no access to create KeyValue required by original parseConfiguration method
    public void parseConfiguration(String json){
		JsonReader jsonReader = Json.createReader(new StringReader(json));
		JsonObject configuration=jsonReader.readObject();
		
        // reading DirectLine configuration  from configuration file
        try {


        	JsonObject config = (JsonObject)configuration.get("DirectLine"); // find prompts node
            if (null != config && config.getValueType().equals(JsonValue.ValueType.OBJECT)) {

                //KeyValueMap configEntities = config.getAsKeyValueMap();
                // for every prompt we read it content
                directlinesecret=config.getString("secret");
                welcome=config.getString("welcomeMessage");
                botname=config.getString("botservicename");
                
                JsonObject endpoints= (JsonObject)config.get("endpoints");
                
                endpoints.entrySet().forEach((_item) -> {
                   try { 
                	   JsonObject entity = (JsonObject) _item.getValue();
                    if (entity.getValueType().equals(JsonValue.ValueType.OBJECT)) {
                        // this is the Directline endpoint
                    	Endpoint newEndpoint = new Endpoint();
                        newEndpoint.endpoint = DirectLineEndpoint.valueOf(_item.getKey());

                       // KeyValueMap listProperties = entity.getAsKeyValueMap();
                        
                        
							newEndpoint.setUrl(entity.getString("url"));

                        newEndpoint.setMethod(entity.getString("method"));
									DirectLineEndpoints.put(
											newEndpoint.endpoint, newEndpoint);
                    }
                        
						} catch (Exception ex) {
							// TODO Auto-generated catch block

							theLogger.error(ex.toString());
							
						}

                    
                });
            } //
        } catch (Exception ex) {
            theLogger.error(ex.toString());
        }

    }

    
}

