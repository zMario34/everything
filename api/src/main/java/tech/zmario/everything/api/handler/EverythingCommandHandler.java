package tech.zmario.everything.api.handler;

import revxrsal.commands.CommandHandler;
import revxrsal.commands.process.ResponseHandler;
import tech.zmario.everything.api.EverythingLibrary;

public class EverythingCommandHandler {

    public EverythingCommandHandler(EverythingLibrary library) {
        CommandHandler commandHandler = library.getCommandHandler();

        commandHandler.registerResponseHandler(String.class, ResponseHandler::reply);
    }
}
