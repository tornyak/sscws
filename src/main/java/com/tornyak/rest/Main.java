package com.tornyak.rest;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Main class.
 *
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI;
    public static final String PROTOCOL;
    public static final Optional<String> HOST;
    public static final String PATH;
    public static final Optional<String> PORT;

    static {
        PROTOCOL = "http://";
        HOST = Optional.ofNullable(System.getenv("HOSTNAME"));
        PORT = Optional.ofNullable(System.getenv("PORT"));
        PATH = "sscws";
        BASE_URI = PROTOCOL + HOST.orElse("localhost") + ":" + PORT.orElse("8080") + "/" + PATH + "/";
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example.rest package
        final ResourceConfig rc = new ResourceConfig().packages("com.tornyak.rest");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        LOGGER.info("SSC WS Started on base URI: " + BASE_URI);
        System.in.read();
        server.shutdownNow();
    }
}

