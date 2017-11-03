package org.logstash.secret.cli;

import java.util.*;

import static org.jruby.RubyClass.CS_NAMES.length;

/**
 * Command line entry point for the Secret store command line
 */
public class SecretStoreCli {

    public static void main(String[] args) throws Exception {

        try {
            boolean hasConsole = Boolean.valueOf(System.getProperty("cli.console", String.valueOf(System.console() != null)));

            Terminal terminal = hasConsole ? new ConsoleTerminal() : new SystemTerminal();

            Map<String, SecretStoreAction> actions = new HashMap<>(5);
            actions.put("list", new ListEntries());
            actions.put("add", new AddSecret());
            actions.put("remove", new RemoveSecret());
            actions.put("create", new CreateStore());
            actions.put("help", new Help());

            boolean help = false;
            if (args == null || args.length != 1 || !actions.containsKey(args[0].toLowerCase(Locale.US)) || (help = args[0].equalsIgnoreCase("help"))) {
                if (!help) {
                    terminal.writeLine(String.format("Invalid command '%s'",  args == null ? "" : String.join(" ",args)));
                }
                terminal.writeLine("");
                terminal.writeLine("Commands");
                terminal.writeLine("--------");
                actions.forEach((k, v) -> terminal.writeLine(String.format("%-6s - %s", k, v.getDescription())));
                System.exit(0);
            }
            try {
                actions.get(args[0]).execute();
            } catch (Exception e) {

                StackTraceElement[] stackTrace = e.getStackTrace();
                for (int i = 0; i < stackTrace.length; i++) {
                    terminal.writeLine(stackTrace[i].toString());
                }
                System.exit(1);
            }
        } catch (Throwable t) { //catch any errors (class not found, etc. and print to stdout)
            t.printStackTrace();
            System.exit(1);
        }
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
            return null;
        }

        @Override
        public char[] readSecret() {
            return new char[0];
        }


    }

    static class SystemTerminal implements Terminal {


        @Override
        public void writeLine(String line) {
            System.out.println(line);
        }

        @Override
        public String readLine() {
            return null;
        }

        @Override
        public char[] readSecret() {
            return new char[0];
        }


    }

}
