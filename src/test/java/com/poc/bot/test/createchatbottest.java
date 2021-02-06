package com.poc.bot.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.genesyslab.chat.bots.chatbotapi.ChatBot;
import com.genesyslab.chat.bots.chatbotapi.platform.ChatInteractionInfo;
import com.genesyslab.chat.bots.chatbotapi.platform.KeyValueMap;
import com.poc.bot.*;

public class createchatbottest {

	private KeyValueMap espParameters;
	private ChatInteractionInfo interactionInfo;

	private BotConfiguration testconfig = new BotConfiguration(com.genesyslab.chat.bots.chatbotapi.platform.LoggerFactory.getLogger(BotConfiguration.class));

	public String loadFromFile() throws IOException {
		String c= new String(
				Files.readAllBytes(Paths
						.get("F:\\work\\Projects\\MS BOT\\Bot Gateway\\AzureBot\\testconfig.txt")));
		
		return c.replaceAll("(\r?\n|\t)", "");
		
	}

	@Test
	public void test() {

		try {


			AzureBotFactory botfactory = new AzureBotFactory();
			
			botfactory.initialize(loadFromFile());

			AzureBot bot = (AzureBot)botfactory.createChatBot();
			
			assert (bot != null);
bot.onCommandStart(null, 0, null);
			
Thread.sleep(5000);
bot.dialog.sendActivity("Book a flight from Paris to Berlin on March 22, 2020");

			Thread.sleep(5000);
			
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
