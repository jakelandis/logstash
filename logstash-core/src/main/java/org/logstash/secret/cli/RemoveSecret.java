package org.logstash.secret.cli;

public class RemoveSecret implements SecretStoreAction {
    @Override
    public String getDescription() {
        return "Remove a secret from the keystore";
    }

    @Override
    public void execute() {

    }
}
