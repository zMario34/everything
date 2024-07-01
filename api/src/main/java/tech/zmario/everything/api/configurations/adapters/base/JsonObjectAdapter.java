package tech.zmario.everything.api.configurations.adapters.base;

import com.google.gson.JsonObject;
import tech.zmario.everything.api.configurations.adapters.ObjectAdapter;

public abstract class JsonObjectAdapter<T> extends ObjectAdapter<T, JsonObject> {

    protected JsonObjectAdapter(Class<?> clazz) {
        super(clazz);
    }
}
