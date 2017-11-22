package org.logstash.secret.cli;

import org.logstash.secret.SecretIdentifier;
import org.logstash.secret.store.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Command line interface for the {@link SecretStore}. <p>Currently expected to be called from Ruby since all the required configuration is currently read from Ruby.</p>
 * <p>Note - this command line user interface intentionally mirrors Elasticsearch's </p>
 */
public class SecretStoreCli {

    enum COMMAND {
        CREATE("create"), LIST("list"), ADD("add"), REMOVE("remove"), HELP("help");

        private final String option;

        COMMAND(String option) {
            this.option = option;
        }

        static COMMAND fromString(final String input) {
            Optional<COMMAND> command = EnumSet.allOf(COMMAND.class).stream().filter(c -> c.option.equals(input)).findFirst();
            if (command.isPresent()) {
                return command.get();
            } else {
                String message = String.format("Invalid command '%s'", input);
                if (input != null && !input.isEmpty()) {
                    Terminal.writeLine(message);
                }
                return HELP;
            }
        }
    }

    public static void command(String command, SecureConfig config, String secretId) {
        Terminal.writeLine("");
        switch (COMMAND.fromString(command)) {
            case CREATE: {
                if (SecretStoreFactory.exists(config.clone())) {
                    Terminal.write("An Logstash keystore already exists. Overwrite ? [y/N] ");
                    if (isYes(Terminal.readLine())) {
                        create(config);
                    }
                } else {
                    create(config);
                }
                break;
            }
            case LIST: {
                Collection<SecretIdentifier> ids = SecretStoreFactory.load(config).list();
                List<String> keys = ids.stream().map(id -> id.getKey()).collect(Collectors.toList());
                Collections.sort(keys);
                keys.forEach(Terminal::writeLine);
                break;
            }
            case ADD: {
                if (secretId == null || secretId.isEmpty()) {
                    Terminal.writeLine("ERROR: You must supply a value to add.");
                    return;
                }
                if (SecretStoreFactory.exists(config.clone())) {
                    SecretIdentifier id = new SecretIdentifier(secretId);
                    SecretStore secretStore = SecretStoreFactory.load(config);
                    byte[] s = secretStore.retrieveSecret(id);
                    if (s == null) {
                        Terminal.write(String.format("Enter value for %s: ", secretId));
                        char[] secret = Terminal.readSecret();
                        add(secretStore, id, SecretStoreUtil.asciiCharToBytes(secret));
                    } else {
                        SecretStoreUtil.clearBytes(s);
                        Terminal.write(String.format("%s already exists. Overwrite ? [y/N] ", secretId));
                        if (isYes(Terminal.readLine())) {
                            Terminal.write(String.format("Enter value for %s: ", secretId));
                            char[] secret = Terminal.readSecret();
                            add(secretStore, id, SecretStoreUtil.asciiCharToBytes(secret));
                        }
                    }
                } else {
                    Terminal.writeLine(String.format("ERROR: Logstash keystore not found. Use 'create' command to create one."));
                }
                break;
            }
            case REMOVE: {
                if (secretId == null || secretId.isEmpty()) {
                    Terminal.writeLine("ERROR: You must supply a value to add.");
                    return;
                }
                SecretIdentifier id = new SecretIdentifier(secretId);

                SecretStore secretStore = SecretStoreFactory.load(config);
                byte[] s = secretStore.retrieveSecret(id);
                if (s == null) {
                    Terminal.writeLine(String.format("ERROR: '%s' does not exist in the Logstash keystore.", secretId));
                } else {
                    SecretStoreUtil.clearBytes(s);
                    secretStore.purgeSecret(id);
                    Terminal.writeLine(String.format("Removed '%s' from the Logstash keystore.", id.getKey()));
                }
                break;
            }
            case HELP: {
                Terminal.writeLine("");
                Terminal.writeLine("Commands");
                Terminal.writeLine("--------");
                Terminal.writeLine("create - Creates a new Logstash keystore");
                Terminal.writeLine("list   - List entries in the keystore");
                Terminal.writeLine("add    - Add a value to the keystore");
                Terminal.writeLine("remove - Remove a value from the keystore");
                Terminal.writeLine("");
                break;
            }
        }
    }

    private static void add(SecretStore secretStore, SecretIdentifier id, byte[] secret) {
        secretStore.persistSecret(id, secret);
        Terminal.writeLine(String.format("Added '%s' to the Logstash keystore.", id.getKey()));
        SecretStoreUtil.clearBytes(secret);
    }

    private static void create(SecureConfig config) {
        if (System.getenv(SecretStoreFactory.ENVIRONMENT_PASS_KEY) == null) {
            Terminal.write(String.format("WARNING: The keystore password is not set. Please set the environment variable `%s`. Failure to do so will result in" +
                    " reduced security. Continue anyway ? [y/N] ", SecretStoreFactory.ENVIRONMENT_PASS_KEY));
            if (isYes(Terminal.readLine())) {
                deleteThenCreate(config);
            }
        } else {
            deleteThenCreate(config);
        }
    }

    private static void deleteThenCreate(SecureConfig config) {
        SecretStoreFactory.delete(config.clone());
        SecretStoreFactory.create(config.clone());
        char[] fileLocation = config.getPlainText("keystore.file");
        Terminal.writeLine("Created Logstash keystore" + (fileLocation == null ? "." : " at " + new String(fileLocation)));
    }

    private static boolean isYes(String response) {
        return "y".equalsIgnoreCase(response) || "yes".equalsIgnoreCase(response);
    }
}
