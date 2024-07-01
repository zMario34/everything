package tech.zmario.everything.api.storage.providers.impl.redis.objects;

import java.io.Serializable;

/**
 * Packet class; Useful for sending objects over redis
 */
public abstract class Packet implements Serializable {

    public abstract String getChannel();

}
