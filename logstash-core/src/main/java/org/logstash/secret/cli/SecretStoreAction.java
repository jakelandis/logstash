package org.logstash.secret.cli;

public interface SecretStoreAction {

    String getDescription();

    void execute();
}
