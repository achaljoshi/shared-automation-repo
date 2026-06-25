package com.sharedframework.cucumber;

import java.util.concurrent.ConcurrentHashMap;

public class ScenarioContext {

    private final ConcurrentHashMap<String, Object> context = new ConcurrentHashMap<>();

    public void set(String key, Object value) {
        context.put(key, value);
    }

    public Object get(String key) {
        return context.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = context.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new ClassCastException(String.format(
                    "Value for key '%s' is of type %s, cannot cast to %s",
                    key, value.getClass().getName(), type.getName()));
        }
        return type.cast(value);
    }

    public boolean contains(String key) {
        return context.containsKey(key);
    }

    public void remove(String key) {
        context.remove(key);
    }

    public void clear() {
        context.clear();
    }

    public int size() {
        return context.size();
    }
}
