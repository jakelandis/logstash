package org.logstash.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Http server backed by Jetty.
 */
public class HttpServer {

    private static final Logger LOGGER = LogManager.getLogger(HttpServer.class);


    public static void start() throws Exception {

        Thread t = new Thread(new Jetty());
        t.setDaemon(true);
        t.start();
    }

    static class Jetty implements Runnable {


        @Override
        public void run() {
            ResourceConfig config = new ResourceConfig();
            config.packages("org.logstash.http");
            ServletHolder servlet = new ServletHolder(new ServletContainer(config));
            //TODO: walk up the ports till an open one is found
            Server server = new Server(9700);
            ServletContextHandler context = new ServletContextHandler(server, "/*", false, false);
            context.addServlet(servlet, "/*");

            try {
                server.start();
                server.join();
            } catch (Exception e) {
                //TODO: fixme, better message and don't log and throw
                LOGGER.error("OUCH!!", e);
                throw new RuntimeException(e);

            } finally {

                server.destroy();
            }

        }
    }
}