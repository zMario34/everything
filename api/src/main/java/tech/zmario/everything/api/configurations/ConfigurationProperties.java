package tech.zmario.everything.api.configurations;

import java.util.Locale;

/*
 * This interface is used to mark a class as a configuration properties class where all the properties are defined.
 */
public interface ConfigurationProperties {

    default void init(Locale locale) {
    }
}
