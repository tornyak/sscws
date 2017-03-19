package com.ericsson.rest;

import com.ericsson.sap.connection.SapServerConnectionFactory;
import com.ericsson.sap.sscws.SscwsSapServerConnectionFactory;
import com.sap.engine.services.jmsconnector.container.utils.Strings;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

/**
 * Main class.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI;
    public static final String PROTOCOL;
    public static final String HOST;
    public static final String PATH;
    public static final String PORT;

    static {
        String host = System.getenv("HOSTNAME");
        String port = System.getenv("PORT");
        PROTOCOL = "http://";
        HOST = Strings.isEmpty(host) ? "localhost" : host;
        PORT = Strings.isEmpty(port) ? "33333" : port;
        PATH = "sscws";
        BASE_URI = PROTOCOL + HOST + ":" + PORT + "/" + PATH + "/";
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example.rest package
        final SapServerConnectionFactory sapFactory =
                new SscwsSapServerConnectionFactory();
        final ResourceConfig rc = new ResourceConfig().register(new DataForwarder(sapFactory));

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {
        final HttpServer server = startServer();
        LOGGER.info("SSC WS Started on base URI: " + BASE_URI);
        System.in.read();
        server.shutdownNow();
    }
}

