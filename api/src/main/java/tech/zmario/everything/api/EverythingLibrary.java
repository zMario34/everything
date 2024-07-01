package tech.zmario.everything.api;

import net.byteflux.libby.LibraryManager;
import net.kyori.adventure.platform.AudienceProvider;
import revxrsal.commands.CommandHandler;
import tech.zmario.everything.api.manager.CooldownsManager;
import tech.zmario.everything.api.scheduler.Scheduler;

import java.io.File;
import java.util.logging.Logger;

public interface EverythingLibrary {

    Logger getLogger();

    void disable();

    <T> void registerService(Class<T> clazz, T service);

    LibraryManager getLibraryManager();

    Scheduler getScheduler();

    AudienceProvider getAudiences();

    File getFolder();

    CommandHandler getCommandHandler();

    CooldownsManager getCooldownsManager();

}
