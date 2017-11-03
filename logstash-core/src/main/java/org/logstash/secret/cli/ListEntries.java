package org.logstash.secret.cli;

public class ListEntries implements SecretStoreAction {
    @Override
    public String getDescription() {
        return "List entries in the keystore";
    }

    @Override
    public void execute() {

    }
}
