package com.poc.bot;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.genesyslab.chat.bots.chatbotapi.ChatBot;
import com.genesyslab.chat.bots.chatbotapi.ChatBot.StopReason;
import com.genesyslab.chat.bots.chatbotapi.chat.ChatEventInfo;
import com.genesyslab.chat.bots.chatbotapi.chat.Enums.Action;
import com.genesyslab.chat.bots.chatbotapi.chat.Enums.EventType;
import com.genesyslab.chat.bots.chatbotapi.chat.Enums.UserType;
import com.genesyslab.chat.bots.chatbotapi.platform.ChatUserInfo;
import com.genesyslab.chat.bots.chatbotapi.platform.EspRequestParameters;
import com.genesyslab.chat.bots.chatbotapi.platform.ChatBotPlatform;
import com.genesyslab.chat.bots.chatbotapi.platform.GenesysChatSession;
import com.poc.bot.BotConfiguration.DirectLineEndpoint;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.slf4j.Logger;

public class AzureBot implements ChatBot {
	private ChatBotPlatform cbpInstance;
	private Logger logger; // bot-session aware logger
	private BotConfiguration theConfig;
	private String theMediaOrigin;
	private boolean waitingForReply;
	public AzureConversation dialog;
	private GenesysChatSession session;

	public AzureBot(BotConfiguration config, String mediaOrigin) {
		theConfig = config;
		theMediaOrigin = mediaOrigin;
		waitingForReply = false;

		/*
		try {
			dialog = new AzureConversation(this,theConfig,logger);
			dialog.openConversation();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		*/
	}

	//remove!!
	public AzureConversation getDialog(){return dialog;}

	@Override
	public void setCbpInstance(ChatBotPlatform cbpInstance) {
		this.cbpInstance = cbpInstance;
		this.logger = cbpInstance.getLogger();
	}

	@Override
	public void onCommandStart(GenesysChatSession session, int eventJoinedId,
			EspRequestParameters espParameters) {


		this.session = session;
		
		try {
			if (null == dialog) {
				if(this.cbpInstance!=null) this.cbpInstance.sendMessage(theConfig.welcome);
				dialog = new AzureConversation(this,theConfig,logger);
				dialog.openConversation();
				
			}

		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void onCommandStop(StopReason reason, ChatEventInfo eventInfo,
			EspRequestParameters espParameters) {
		// TODO to be implemented
	}

	@Override
	public void onSessionActivity(ChatEventInfo eventInfo) {
		ChatUserInfo originator = session.getParticipant(eventInfo.getUserId());
		if (eventInfo.getEventType() == EventType.MESSAGE
				&& originator.getUserType() != UserType.SYSTEM
				&& originator.getUserType() != UserType.EXTERNAL) {
			if (eventInfo.getMessageText().matches(
					"stop(:(keep_alive|force_close|close_if_no_agents))?")) {
				Action action = Action.KEEP_ALIVE;
				String[] tokens = eventInfo.getMessageText().split(":");
				if (tokens.length > 1) {
					action = Action.valueOf(tokens[1].toUpperCase());
				}

				// The bot decides to stop execution.
				// The bot will be removed from chat session and discarded in
				// BGS.
				// If running in waiting mode, the interaction will be released
				// in workflow.
				// If the bot need to communicate something to workflow, it
				// could invoke updateUserdata at this moment.
				logger.info("Leaving chat session with after-action: "
						+ action.name());
				cbpInstance.leaveSession(action);
			} else {
				logger.info("Sending message to DirectLine id: "
						+ dialog.getConversationId() + " payload: "
						+ eventInfo.getMessageText());
				dialog.sendActivity(eventInfo.getMessageText());

				// cbpInstance.sendMessage(eventInfo.getMessageText());
			}
		}

	}

	@Override
	public void onCommandUpdate(EspRequestParameters espParameters) {
		// TODO to be implemented
	}
	

	public void sendChatMessage(Message msg){
		
		System.out.println("-------- Received from bot: "+msg.getPayload());
		if(this.cbpInstance!=null) this.cbpInstance.sendMessage(msg.getPayload());
	}

}
