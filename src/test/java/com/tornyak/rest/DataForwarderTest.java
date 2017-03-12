package com.tornyak.rest;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class DataForwarderTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig().packages("com.tornyak").property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "INFO");
    }


    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testConfigure() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("FAP23003_20170309_slimmad.xml").getPath());
        byte[] data = Files.readAllBytes(path);

        Response response = target("configure").request().post(Entity.xml(data));

        assertEquals(200, response.getStatus());
        assertEquals(new String(data), response.readEntity(String.class));
    }
}
