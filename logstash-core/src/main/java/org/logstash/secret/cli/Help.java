package org.logstash.secret.cli;

public class Help implements SecretStoreAction{
    @Override
    public String getDescription() {
        return "Display this help menu.";
    }

    @Override
    public void execute() {
        // Do nothing, the Cli handles the help output.
    }
}
