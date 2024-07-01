package tech.zmario.everything.api.configurations.adapters.base;

import tech.zmario.everything.api.configurations.adapters.ObjectAdapter;

import java.util.Map;

public abstract class YamlObjectAdapter<T> extends ObjectAdapter<T, Map<String, Object>> {

    protected YamlObjectAdapter(Class<?> clazz) {
        super(clazz);
    }
}
