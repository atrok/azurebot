package com.poc.bot;

import com.genesyslab.chat.bots.chatbotapi.ChatBot;
import com.genesyslab.chat.bots.chatbotapi.ChatBotFactory;
import com.genesyslab.chat.bots.chatbotapi.platform.ChatInteractionInfo;
import com.genesyslab.chat.bots.chatbotapi.platform.KeyValueMap;
import com.genesyslab.chat.bots.chatbotapi.platform.BotCreationAttributes;
import com.genesyslab.chat.bots.chatbotapi.platform.LoggerFactory;
import org.slf4j.Logger;


public class AzureBotFactory implements ChatBotFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AzureBotFactory.class);
    final private BotConfiguration botConfig = new BotConfiguration(LOG);

    @Override
    public void initialize(KeyValueMap configuration) {
    	
    	botConfig.parseConfiguration(configuration);
    }

    @Override
    public void configurationUpdated(KeyValueMap configuration) {
    	botConfig.parseConfiguration(configuration);
    }

    // for testing purposes
    public void initialize(String configuration) {
    	
    	botConfig.parseConfiguration(configuration);
    }

    public void configurationUpdated(String configuration) {
    	botConfig.parseConfiguration(configuration);
    }

    public void shutdown() {
        // TODO to be implemented
    }

    public ChatBot createChatBot(KeyValueMap espParameters, ChatInteractionInfo interactionInfo, BotCreationAttributes botCreationAttributes) {
        String mediaOrigin = null;
        KeyValueMap.ValueHolder valueForOrigin = interactionInfo.getUserData().get("MediaOrigin");
        if (null != valueForOrigin) {
            mediaOrigin = valueForOrigin.getAsString();
        }

        if (null != mediaOrigin && !mediaOrigin.isEmpty()) {
            return new AzureBot(botConfig, mediaOrigin);
        } else {
            LOG.info("Refusing to start the bot because MediaOrigin is not defined in userdata of interaction.");
            return null;
        }
    }
    
    /// for testing only, remove
    public ChatBot createChatBot() {

            return new AzureBot(botConfig, "genesys-chat");

    }

    @Override
    public String getBotId() {
        return "AzureBot";
    }
}
