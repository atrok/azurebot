package com.poc.bot;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.poc.bot.BotConfiguration.DirectLineEndpoint;
import com.poc.bot.BotConfiguration.Endpoint;

public class AzureConversation {

	private String conversationId;
	private String latestActivity;
	private String token;
	private Client client;
	private Logger logger;
	private String latest_activityId = "00000";
	private AzureBot bot;
	private BotConfiguration theConfig;
	private BotConfiguration.Endpoint startConversationConfig;
	private BotConfiguration.Endpoint sendActivityConfig;
	private BotConfiguration.Endpoint getActivitiesConfig;
	private String streamUrl;

	public AzureConversation(AzureBot bot, BotConfiguration config,
			Logger parentlogger) {
		this.bot = bot;
		this.theConfig = config;
		startConversationConfig = theConfig.DirectLineEndpoints
				.get(DirectLineEndpoint.STARTCONVERSATION);
		sendActivityConfig = theConfig.DirectLineEndpoints
				.get(DirectLineEndpoint.SENDACTIVITY);
		getActivitiesConfig = theConfig.DirectLineEndpoints
				.get(DirectLineEndpoint.GETACTIVITIES);

		logger = parentlogger;
		client = ClientBuilder.newClient();
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

	}

	static class AzureMessage{
		
	//JsonObject json=Json.createReader(new StringReader("{    \"locale\": \"en-EN\", \"type\": \"message\", \"from\": { \"id\": \"user1\"} }")).readObject();
	
	public static String getPayload(String msg){
		
		JsonObject value=Json.createObjectBuilder().add("locale", "en-EN").add("type", "message").add("from", Json.createObjectBuilder()
			.add("id", "user"))
			.add("text", msg).build();
		
		return value.toString();
	
	}
	}
	
	public void openConversation() {
		try {

			openHTTPConnection();
			openWebSocket();

		} catch (URISyntaxException e) {
			System.err.println(e.getStackTrace());
		}
	}

	private void openHTTPConnection() {

		JsonObject jsonObj = sendHTTPData(startConversationConfig,
				"{ user: { id: 'dl_user_id', name: 'NONAME' } }");
		token = jsonObj.getString("token");
		conversationId = jsonObj.getString("conversationId");
		streamUrl = jsonObj.getString("streamUrl");

		System.out.println("---- DirectLine HTTP dialog ----\n token:\n"
				+ token.length()  + ",\n conversationId: " + conversationId);
	}

	public JsonObject getActivities() {

		BotConfiguration.Endpoint endpoint = new BotConfiguration.Endpoint()
				.from(getActivitiesConfig);

		endpoint.url=endpoint.url.replaceAll("%conversationId%", conversationId);
		endpoint.url=endpoint.url.replaceAll("%latest_activityId%", latest_activityId);

		return sendHTTPData(endpoint, null);
	}

	public void sendActivity(String payload) {

		BotConfiguration.Endpoint endpoint = new BotConfiguration.Endpoint().from(sendActivityConfig);

		endpoint.url=endpoint.url.replaceAll("%conversationId%", conversationId);

		JsonObject json = sendHTTPData(endpoint, payload);

		latestActivity = json.getString("id").substring(26); // "id":
																// "2kh1njkNdxJL3aIxsxFUWZ-o|0000002"

		System.out.println("Sent message id:" + latestActivity);
	}

	public String getConversationId() {
		return conversationId;
	}

	private void openWebSocket() throws URISyntaxException {

		final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(
				new URI(streamUrl));

		// add listener
		clientEndPoint
				.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
					public void handleMessage(String message) {
						System.out.println(message);

						if(message.length()>0){// we received a message with a content
						ArrayList<Message> messages = parseMessage(message);

						messages.forEach((item) -> {
							bot.sendChatMessage((Message) item);
						});
						}else{//is it a refresh poll?
							System.out.println("Got empty message from "+theConfig.botname);
						}
					}
				});
	}

	private ArrayList<Message> parseMessage(String message) {
		ArrayList<Message> messages = new ArrayList<Message>();

		if (message.length() > 0) {
			JsonReader jsonReader = Json
					.createReader(new StringReader(message));
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			JsonArray activities = (JsonArray) jsonObject.get("activities"); // find
																				// activities
																				// node

			if (activities != null
					&& activities.getValueType() == JsonValue.ValueType.ARRAY) {

				int i = 0;
				
				activities.forEach((_item) -> {
					JsonObject entity = (JsonObject) _item;
					if (entity.getValueType()
							.equals(JsonValue.ValueType.OBJECT)) {
						String type = entity.getString("type");
						String text = (entity.containsKey("text"))?entity.getString("text"):null;
						
						JsonObject from=(JsonObject)entity.get("from");
						String botname=from.getString("id");
						
						
						if (type.equals("message") && text != null
								&& text.length() > 0 && botname.equals(theConfig.botname)) {

							messages.add(new TextMessage(text));

						}
					}
				});
			}
			return messages;

		}

		return null;

	}
	
	private JsonValue getValueFromJsonObjectByKeyName(String key, JsonObject json){
		
		return (json.containsKey(key))?json.get(key):null;
	}

	private JsonObject sendHTTPData(Endpoint config, String payload) {
		Response response;

		String token;

		// in case the dialog is not opened we need to send a secret, checking
		// if token exists
		token = (this.token == null) ? theConfig.directlinesecret : this.token;

		Invocation.Builder invocationBuilder = client.target(config.url)
				.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token);

		// if no payload then turn it into empty string to avoid exceptions
		if (null == payload)
			payload = "";

		switch (config.method) {
		case GET:
			response = invocationBuilder.get();
			break;
		case POST:
			response = invocationBuilder.post(Entity.json(AzureMessage.getPayload(payload)));
			break;
		default:
			throw new RuntimeException("HTTP method " + config.method.name()
					+ " is not supported");
		}

		String rsp = response.readEntity(String.class);
		JsonReader jsonReader = Json.createReader(new StringReader(rsp));
		JsonObject json=jsonReader.readObject();
		
		if(json.containsKey("error")) throw new RuntimeException(json.get("error").toString());
		
		return json;
	}
}
