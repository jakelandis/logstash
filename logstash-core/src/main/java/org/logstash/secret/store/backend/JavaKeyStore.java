package org.logstash.secret.store.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.logstash.secret.SecretIdentifier;
import org.logstash.secret.store.*;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>Java Key Store implementation for the {@link SecretStore}.</p>
 * <p>Note this implementation should not be used for high volume or large datasets.</p>
 * <p>This class is threadsafe.</p>
 */
public final class JavaKeyStore implements SecretStore {
    static final String LOGSTASH_MARKER = "logstash-key-store";
    private static final Logger LOGGER = LogManager.getLogger(JavaKeyStore.class);

    private char[] keyStorePass;
    private Path keyStorePath;
    private final ProtectionParameter protectionParameter;
    private final Lock readLock;
    private final Lock writeLock;
    private KeyStore keyStore;

    /**
     * Constructor - will loadSecretStore the keystore if it does not exist
     *
     * @param config The configuration for this keystore
     * @throws SecretStoreException if errors occur while trying to loadSecretStore or access the keystore
     */
    public JavaKeyStore(SecureConfig config) {
        try {
            this.keyStorePath = Paths.get(new String(config.getPlainText("keystore.path")));
            //TODO: validate non-null throw appriate exception
            //use base64 encoded char[] for the actual pass since the underlying library requires ASCII, but we want to support non-ASCII keystore passwords
            this.keyStorePass = SecretStoreUtil.base64encode(config.getPlainText(SecretStoreFactory.KEYSTORE_ACCESS_KEY));
            config.clearValues();
            char[] configuredType = config.getPlainText("keystore.type");
            String keyStoreType = configuredType == null ? "pkcs12" : new String(configuredType);
            this.keyStore = KeyStore.getInstance(keyStoreType);

            ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
            readLock = readWriteLock.readLock();
            writeLock = readWriteLock.writeLock();
            this.protectionParameter = new PasswordProtection(keyStorePass);
            SecretIdentifier logstashMarker = new SecretIdentifier(LOGSTASH_MARKER);

            try (final InputStream is = Files.newInputStream(keyStorePath)) {
                keyStore.load(is, keyStorePass);
                byte[] marker = retrieveSecret(logstashMarker);
                if (marker == null) {
                    throw new SecretStoreException.NotLogstashKeyStore("Found a keystore, but it is not a logstash keystore.");
                }
            } catch (NoSuchFileException noSuchFileException) {
                LOGGER.warn("Keystore not found at {}. Creating new keystore.", keyStorePath.toAbsolutePath().toString());

                //loadSecretStore the keystore on disk with a default entry to identify this as a logstash keystore
                try (final OutputStream os = Files.newOutputStream(keyStorePath)) {
                    keyStore = KeyStore.Builder.newInstance(keyStoreType, null, protectionParameter).getKeyStore();
                    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
                    byte[] base64 = Base64.getEncoder().encode(LOGSTASH_MARKER.getBytes(StandardCharsets.UTF_8));
                    SecretKey secretKey = factory.generateSecret(new PBEKeySpec(SecretStoreUtil.asciiBytesToChar(base64)));
                    keyStore.setEntry(logstashMarker.toExternalForm(), new KeyStore.SecretKeyEntry(secretKey), protectionParameter);
                    keyStore.store(os, keyStorePass);

                    PosixFileAttributeView attrs = Files.getFileAttributeView(keyStorePath, PosixFileAttributeView.class);
                    if (attrs != null) {
                        attrs.setPermissions(PosixFilePermissions.fromString("rw-rw----"));
                    }
                }
            } catch (IOException ioe) {
                if (ioe.getCause() instanceof UnrecoverableKeyException) {
                    throw new SecretStoreException.AccessException(
                            String.format("Can not access Java keystore at %s, check file permissions and logstash.keystore.pass", keyStorePath.toAbsolutePath()), ioe);
                } else {
                    throw ioe;
                }
            }
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | InvalidKeySpecException e) {
            throw new SecretStoreException("Error while trying to loadSecretStore or launch the JavaKeyStore", e);
        }
    }

    @Override
    public Collection<SecretIdentifier> list() {
        Set<SecretIdentifier> identifiers = new HashSet<>();
        try {
            readLock.lock();
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                identifiers.add(SecretIdentifier.fromExternalForm(alias));
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        return identifiers;
    }

    @Override
    public void persistSecret(SecretIdentifier identifier, byte[] secret) {
        try {
            writeLock.lock();
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
            //PBEKey requires an ascii password, so base64 encode it
            byte[] base64 = Base64.getEncoder().encode(secret);
            PBEKeySpec passwordBasedKeySpec = new PBEKeySpec(SecretStoreUtil.asciiBytesToChar(base64));
            SecretKey secretKey = factory.generateSecret(passwordBasedKeySpec);
            keyStore.setEntry(identifier.toExternalForm(), new KeyStore.SecretKeyEntry(secretKey), protectionParameter);
            try (final OutputStream os = Files.newOutputStream(keyStorePath)) {
                keyStore.store(os, keyStorePass);
            } finally {
                passwordBasedKeySpec.clearPassword();
                SecretStoreUtil.clearBytes(secret);
            }
        } catch (Exception e) {
            throw new SecretStoreException.PersistException(identifier, e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void purgeSecret(SecretIdentifier identifier) {
        try {
            writeLock.lock();
            try (final InputStream is = Files.newInputStream(keyStorePath)) {
                keyStore.load(is, keyStorePass);
                keyStore.deleteEntry(identifier.toExternalForm());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try (final OutputStream os = Files.newOutputStream(keyStorePath)) {
                keyStore.store(os, keyStorePass);
            }
        } catch (Exception e) {
            throw new SecretStoreException.PurgeException(identifier, e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public byte[] retrieveSecret(SecretIdentifier identifier) {
        if (identifier != null && identifier.getKey() != null && !identifier.getKey().isEmpty()) {
            try {
                readLock.lock();
                try (final InputStream is = Files.newInputStream(keyStorePath)) {
                    keyStore.load(is, keyStorePass);
                    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
                    KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(identifier.toExternalForm(), protectionParameter);
                    //not found
                    if (secretKeyEntry == null) {
                        return null;
                    }
                    PBEKeySpec passwordBasedKeySpec = (PBEKeySpec) factory.getKeySpec(secretKeyEntry.getSecretKey(), PBEKeySpec.class);
                    //base64 encoded char[]
                    char[] base64secret = passwordBasedKeySpec.getPassword().clone();
                    byte[] secret = Base64.getDecoder().decode(SecretStoreUtil.asciiCharToBytes(base64secret));
                    passwordBasedKeySpec.clearPassword();
                    return secret;
                }
            } catch (Exception e) {
                throw new SecretStoreException.RetrievalException(identifier, e);
            } finally {
                readLock.unlock();
            }
        }
        return null;
    }
}

