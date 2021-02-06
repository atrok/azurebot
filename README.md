# Azure bot

Azure Bot plugin for Genesys Bot gateway

1. Architecture

Bot Gateway is a part of Genesys Digital Message Server. It provides an API to develop pluged-in modules to interface thirdparty NLUs (Google, Azure, AWS)
This particular bot is developed to interface with the bot application deployed in Azure cloud via DirectLine channel.

It serves as a proxy between Genesys Widget chat client and Azure bot, and provides seemless transformation of native Microsoft Bot Framework Activities to Genesys Widget messages. 
Only text messages are supported as a part of this implementation.

