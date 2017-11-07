package org.logstash.secret.cli;

import org.logstash.secret.store.SecretStoreFactory;
import org.logstash.secret.store.SecureConfig;

public class CreateStore implements SecretStoreAction {

    private final SecureConfig configuration;

    public CreateStore(SecureConfig configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getDescription() {
        return "Creates a new Logstash keystore";
    }

    @Override
    public void execute() {
        //TODO: check if it is already created.
        String className = System.getProperty("org.logstash.secret.store.SecretStore", "org.logstash.secret.store.backend.JavaKeyStore");

        SecretStoreFactory.create(className, configuration);
        configuration.clearValues();
        System.out.println("whoo! it be created" );
    }
}
