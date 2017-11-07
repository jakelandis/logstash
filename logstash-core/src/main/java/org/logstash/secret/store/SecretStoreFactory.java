package org.logstash.secret.store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;

/**
 * <p>A factory to load the implementation of a {@link SecretStore}. Where the implementation requires a constructor that accepts a {@link SecureConfig} as it's only parameter.
 * </p>
 */
public class SecretStoreFactory {

    public static final String KEYSTORE_ACCESS_KEY = "keystore.pass";

    /**
     * Private constructor
     */
    private SecretStoreFactory() {
    }

    private static final Logger LOGGER = LogManager.getLogger(SecretStoreFactory.class);

    /**
     * Creates a {@link SecretStore} based on the provided configuration
     *
     * @param secureConfig The configuration to pass to the implementation
     * @return
     */
    @SuppressWarnings({"unchecked", "JavaReflectionMemberAccess"})
    static public SecretStore create(String className, SecureConfig secureConfig) {
        try {
            LOGGER.debug("Attempting to create secret store with implementation: {}", className);
            Class<? extends SecretStore> implementation = (Class<? extends SecretStore>) Class.forName(className);
            Constructor<? extends SecretStore> constructor = implementation.getConstructor(SecureConfig.class);
            addSecretStoreAccess(secureConfig);
            return constructor.newInstance(secureConfig);

        } catch (Exception e) {
            throw new SecretStoreException.ImplementationNotFoundException(
                    String.format("Could not create class %s, please ensure it is on the Java classpath, implements org.logstash.secret.store.SecretStore, and has a 1 argument " +
                            "constructor that accepts a org.logstash.secret.store.SecureConfig", className), e);
        }
    }

    /**
     * <p>Adds the obfuscated version of the credential needed to access the {@link SecretStore}. The credential is searched for in the following order:</p>
     * <ul>
     * <li>Java System Property "logstash.keystore.pass </li>
     * <li>Environment variable "LOGSTASH_KEYSTORE_PASS"</li>
     * <li>Hardcoded password.</li>
     * </ul>
     *
     * @param secureConfig The configuration to add the secret store access
     */
    private static void addSecretStoreAccess(SecureConfig secureConfig) {

        String keyStorePassProp = System.getProperty("logstash.keystore.pass");
        String keyStorePassEnv = System.getenv("LOGSTASH_KEYSTORE_PASS");

        int[] codepoints = {0xD83E, 0xDD21, 0xD83E, 0xDD84}; //TODO: add more code points here

        String pass = keyStorePassProp != null ? keyStorePassProp
                : keyStorePassEnv != null ? keyStorePassEnv
                : new String(codepoints, 0, codepoints.length);

        char[] value = pass.toCharArray();
        //futile attempt to remove the pass from memory
        pass = null;
        System.gc();

        secureConfig.add(KEYSTORE_ACCESS_KEY, value);
    }

}
