package org.logstash.secret.store;

import org.logstash.secret.store.backend.JavaKeyStore;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class SecretStoreConfig {

    private final char[] password;
    private final Path path;
    private final Class<? extends SecretStore> implementation;

    private SecretStoreConfig(char[] password, Path path, Class<? extends SecretStore> implementation) {
        this.password = password;
        this.path = path;
        this.implementation = implementation;
    }

    public char[] getPassword() {
        return password;
    }

    public Path getPath() {
        return path;
    }

    public Class<? extends SecretStore> getImplementation() {
        return implementation;
    }

    public class Builder {

        private char[] password = null;
        private Path path = null;
        private Class<? extends SecretStore> implementation = JavaKeyStore.class;

        public Builder() {
            String keyStorePassEnv = System.getenv("LOGSTASH_KEYSTORE_PASS");

            String keyStorePassProp = System.getProperty("logstash.keystore.pass");
            int[] codepoints = {0xD83E, 0xDD21, 0xD83E, 0xDD84}; //TODO: add more code points here

            password = keyStorePassProp != null ? SecretStoreUtil.obfuscate(keyStorePassProp)
                    : keyStorePassEnv != null ? SecretStoreUtil.obfuscate(keyStorePassEnv)
                    : SecretStoreUtil.obfuscate(new String(codepoints, 0, codepoints.length));
        }

        Builder secretStorePassword(char[] password) {
            this.password = password;
            return this;
        }

        Builder secretStorePath(Path path) {
            this.path = path;
            return this;
        }

        Builder secretStoreImplementation(Class<? extends SecretStore> implementation) {
            this.implementation = implementation;
            return this;
        }

        SecretStoreConfig build() {
            if (JavaKeyStore.class.equals(implementation)) {
                Set invalid = validateAllNonNull(new ValidationPair<>("path", path), new ValidationPair<>("password", password));
                if (!invalid.isEmpty()) {
                    throw new SecretStoreException.ConfigurationException(String.format("%s can not not be null", String.join(", ", invalid)));
                }

            }
            return new SecretStoreConfig(password, path, implementation);
        }


        /**
         * Validates all inputs are non null
         *
         * @param pairs
         */
        private Set<String> validateAllNonNull(ValidationPair... pairs) {
            Set<String> invalid = new HashSet<>(1);
            for (ValidationPair pair : pairs) {
                if (pair.object == null) {
                    invalid.add(pair.name);
                }
            }
            return invalid;
        }

        //TODO: write temporary
        private boolean validateWritable(Path path) {
            return true;
        }

        /**
         * 2-Tuple named objects
         *
         * @param <T> The type of Object
         */
        class ValidationPair<T> {
            private final String name;
            private final T object;

            public ValidationPair(String name, T object) {
                this.name = name;
                this.object = object;
            }
        }

    }
}
