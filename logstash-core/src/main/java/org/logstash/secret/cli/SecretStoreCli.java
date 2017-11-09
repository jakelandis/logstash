package org.logstash.secret.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.logstash.secret.store.SecureConfig;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * Command line entry point for the Secret store command line
 */
public class SecretStoreCli {
    private static final Logger LOGGER = LogManager.getLogger(SecretStoreCli.class);

    public static void main(String[] args) throws Exception {

        try {
            //to assist with testing...
            boolean hasConsole = Boolean.valueOf(System.getProperty("cli.console", String.valueOf(System.console() != null)));

            Terminal terminal = hasConsole ? new ConsoleTerminal() : new SystemTerminal();

            SecureConfig configuration = getConfiguration();
            Map<String, SecretStoreAction> actions = new HashMap<>(5);
            actions.put("list", new ListEntries(configuration));
            actions.put("add", new AddSecret());
            actions.put("remove", new RemoveSecret());
            actions.put("loadSecretStore", new CreateStore(configuration));
            actions.put("help", new Help());

            boolean help = false;
            if (args == null || args.length != 1 || !actions.containsKey(args[0].toLowerCase(Locale.US)) || (help = args[0].equalsIgnoreCase("help"))) {
                if (!help) {
                    terminal.writeLine(String.format("Invalid command '%s'", args == null ? "" : String.join(" ", args)));
                }
                terminal.writeLine("");
                terminal.writeLine("Commands");
                terminal.writeLine("--------");
                actions.forEach((k, v) -> terminal.writeLine(String.format("%-6s - %s", k, v.getDescription())));
                System.exit(0);
            }
            String action = args[0];
            try {
                actions.get(action).execute();
            } catch (Exception e) {

                LOGGER.error("Error while trying to perform the action: {}", action, e);
                //ensure the logger is flushed
                LogManager.shutdown();
                System.exit(1);
            }
        } catch (Throwable t) { //catch any errors (class not found, etc. and print to stdout)
            LOGGER.error("Unknown error occurred", t);
            //ensure the logger is flushed
            LogManager.shutdown();
            System.exit(1);
        }
    }

    //TODO: read this from the YAML, and add proper logic here
    @SuppressWarnings("unchecked")
    private static SecureConfig getConfiguration() {
        SecureConfig secureConfig = new SecureConfig();
        //get path here.

        secureConfig.add("keystore.path", "/tmp/logstash.keystore".toCharArray());

        return secureConfig;
    }


    /**
     * Inspired by Elasticsearch's Terminal of the same name
     */
    interface Terminal {
        /**
         * Writes a single line.
         *
         * @param line the line to write.
         */
        void writeLine(String line);

        /**
         * Reads a single line
         *
         * @return the line
         */
        String readLine();

        /**
         * Reads a secret
         *
         * @return the char[] representation of the secret.
         */
        char[] readSecret();
    }

    static class ConsoleTerminal implements Terminal {

        @Override
        public void writeLine(String line) {
            System.console().writer().println(line);
            System.console().writer().flush();
        }

        @Override
        public String readLine() {
            return System.console().readLine();
        }

        @Override
        public char[] readSecret() {
            return System.console().readPassword();
        }


    }

    static class SystemTerminal implements Terminal {


        Scanner scanner = new Scanner(System.in);

        @Override
        public void writeLine(String line) {
            System.out.println(line);
        }

        @Override
        public String readLine() {
            return scanner.next();
        }

        @Override
        public char[] readSecret() {
            return scanner.next().toCharArray();
        }


    }

}
