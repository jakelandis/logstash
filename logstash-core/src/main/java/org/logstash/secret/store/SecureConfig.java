package org.logstash.secret.store;

import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;


public class SecureConfig {

    Map<String, CharBuffer> config = new HashMap<>();

    public void add(String key, char[] value) {
        config.put(key, CharBuffer.wrap(SecretStoreUtil.obfuscate(value)));
    }

    public void clearValues() {
        config.forEach((k, v) -> SecretStoreUtil.clearChars(v.array()));
    }

    public char[] getPlainText(String key) {
        if (config.get(key) != null) {

            //need to clone since deObfuscate tries to clear the original value, and we want to hold on it here.
            return SecretStoreUtil.deObfuscate(config.get(key).array().clone());
        }
        return null;
    }

    public boolean has(String key) {
        return config.get(key) == null;
    }
}
