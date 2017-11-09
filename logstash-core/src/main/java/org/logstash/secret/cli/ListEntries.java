package org.logstash.secret.cli;

import org.logstash.secret.SecretIdentifier;
import org.logstash.secret.store.SecretStore;
import org.logstash.secret.store.SecretStoreFactory;
import org.logstash.secret.store.SecureConfig;

public class ListEntries implements SecretStoreAction {

    private final SecureConfig configuration;

    public ListEntries(SecureConfig configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getDescription() {
        return "List entries in the keystore";
    }

    @Override
    public void execute() {
        SecretStore secretStore = SecretStoreFactory.loadSecretStore(configuration);
        secretStore.persistSecret(new SecretIdentifier("foo"), "bar".getBytes());
        secretStore.list().forEach(l -> System.out.println(l.toExternalForm()));
        configuration.clearValues();
    }
}
