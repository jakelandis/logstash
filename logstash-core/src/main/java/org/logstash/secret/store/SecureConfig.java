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
        return config.get(key) == null ? null : SecretStoreUtil.deObfuscate(config.get(key).array().clone());
    }

    public boolean has(String key) {
        return config.get(key) == null;
    }
}
