package org.logstash.secret.cli;

/**
 * Placeholder action that has does nothing but echo's a display.
 */
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
