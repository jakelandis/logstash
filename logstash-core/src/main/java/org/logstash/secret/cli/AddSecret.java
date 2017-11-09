package org.logstash.secret.cli;

public class AddSecret implements SecretStoreAction{
    @Override
    public String getDescription() {
        return "Add a secret value to the keystore";
    }

    @Override
    public void execute() {

    }
}
